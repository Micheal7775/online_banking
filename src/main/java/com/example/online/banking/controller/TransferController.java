package com.example.online.banking.controller;

import com.example.online.banking.dto.TransferConfirmRequest;
import com.example.online.banking.dto.TransferOtpRequest;
import com.example.online.banking.model.OTP;
import com.example.online.banking.model.Transaction;
import com.example.online.banking.model.User;
import com.example.online.banking.repo.UserRepository;
import com.example.online.banking.service.AccountService;
import com.example.online.banking.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final OtpService otpService;
    private final AccountService accountService;
    private final UserRepository userRepository;

    @PostMapping("/otp")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> generateTransferOtp(
            @Valid @RequestBody TransferOtpRequest request,
            Authentication authentication) {

        User user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        OTP otp = otpService.generateTransferOtp(
                user,
                request.toAccountNumber(),
                request.amount()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "OTP generated successfully",
                        "expiresAt", otp.getExpiresAt()
                ));
    }

    @PostMapping("/{accountNumber}/confirm")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Transaction> confirmTransfer(
            @PathVariable String accountNumber,
            @Valid @RequestBody TransferConfirmRequest request,
            Authentication authentication) {

        User user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Transaction transaction =
                accountService.transferWithOtp(
                        accountNumber,
                        request.otp(),
                        request.description(),
                        user
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transaction);
    }




    @GetMapping("/{accountNumber}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<Transaction>> getTransactionHistory(
            @PathVariable String accountNumber) {

        return ResponseEntity.ok(
                accountService.getTransactionHistory(accountNumber)
        );
    }
}