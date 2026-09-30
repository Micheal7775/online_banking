package com.example.online.banking.controller;

import com.example.online.banking.dto.TransferConfirmRequest;
import com.example.online.banking.dto.TransferOtpRequest;
import com.example.online.banking.exception.ResourceNotFoundException;
import com.example.online.banking.model.Customer;
import com.example.online.banking.model.OTP;
import com.example.online.banking.model.Transaction;
import com.example.online.banking.model.User;
import com.example.online.banking.repo.CustomerRepository;
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
    private  final CustomerRepository customerRepository;

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

    @PostMapping("/{accountNumber}/transfer/otp")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> generateTransferOtp(
            @PathVariable String accountNumber,
            @Valid @RequestBody TransferOtpRequest request,
            Authentication authentication) {

        // Get logged-in User
        User user =
                userRepository
                        .findByUsername(authentication.getName())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        // Get Customer from User
        Customer customer =
                customerRepository
                        .findByUserUserId(
                                user.getUserId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found"
                                )
                        );

        // Generate OTP and send to customer email
        otpService.generateTransferOtp(
                customer,
                request.toAccountNumber(),
                request.amount()
        );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "OTP sent successfully to your registered email"
                )
        );
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