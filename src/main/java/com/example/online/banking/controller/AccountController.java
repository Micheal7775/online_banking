package com.example.online.banking.controller;

import com.example.online.banking.dto.*;
import com.example.online.banking.exception.ResourceNotFoundException;
import com.example.online.banking.model.Account;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/manager/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final UserRepository userRepository;
    private final OtpService otpService;


    // =========================================
    // CREATE ACCOUNT
    // =========================================

    @PostMapping("/create/{applicationId}")
    @PreAuthorize("hasRole('BANK_MANAGER')")
    public ResponseEntity<Account> createAccount(
            @PathVariable Long applicationId) {

        Account account =
                accountService.createAccount(applicationId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(account);
    }


    // =========================================
    // DEPOSIT
    // =========================================

    @PostMapping("/{accountNumber}/deposit")
    @PreAuthorize("hasAnyRole('BANK_MANAGER', 'ACCOUNT_OPENING_STAFF', 'CUSTOMER')")
    public ResponseEntity<Transaction> deposit(
            @PathVariable String accountNumber,
            @Valid @RequestBody DepositRequest request) {

        Transaction transaction =
                accountService.deposit(
                        accountNumber,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transaction);
    }


    // =========================================
    // WITHDRAW
    // =========================================

    @PostMapping("/{accountNumber}/withdraw")
    @PreAuthorize("hasAnyRole('BANK_MANAGER', 'CUSTOMER')")
    public ResponseEntity<Transaction> withdraw(
            @PathVariable String accountNumber,
            @Valid @RequestBody WithdrawRequest request) {

        Transaction transaction =
                accountService.withdraw(
                        accountNumber,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transaction);
    }


    // =========================================
    // TRANSFER OTP GENERATE
    // =========================================

    @PostMapping("/{accountNumber}/transfer/otp")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> generateTransferOtp(
            @PathVariable String accountNumber,
            @Valid @RequestBody TransferOtpRequest request,
            Authentication authentication) {

        User user =
                userRepository
                        .findByUsername(authentication.getName())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                ));

        otpService.generateTransferOtp(
                user,
                request.toAccountNumber(),
                request.amount()
        );

        return ResponseEntity.ok(
                java.util.Map.of(
                        "message",
                        "OTP generated successfully"
                )
        );
    }


    // =========================================
    // TRANSFER OTP CONFIRM
    // =========================================

    @PostMapping("/{accountNumber}/transfer/confirm")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Transaction> transferWithOtp(
            @PathVariable String accountNumber,
            @RequestBody TransferConfirmRequest request,
            Authentication authentication) {

        User user =
                userRepository
                        .findByUsername(authentication.getName())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                ));

        // DEBUG - development only
        System.out.println(
                "CONTROLLER OTP = [" + request.otp() + "]"
        );

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


    // =========================================
    // TRANSACTION HISTORY
    // =========================================

    @GetMapping("/{accountNumber}/transactions")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<Transaction>> getTransactionHistory(
            @PathVariable String accountNumber) {

        return ResponseEntity.ok(
                accountService.getTransactionHistory(
                        accountNumber
                )
        );
    }


    // =========================================
    // STATEMENT
    // =========================================

    @GetMapping("/{accountNumber}/statement")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<Transaction>> getStatement(
            @PathVariable String accountNumber,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate) {

        List<Transaction> statement =
                accountService.getStatement(
                        accountNumber,
                        fromDate,
                        toDate
                );

        return ResponseEntity.ok(statement);
    }


    @GetMapping("/{accountNumber}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Account> getAccount(
            @PathVariable String accountNumber) {

        Account account =
                accountService.getAccountByNumber(accountNumber);

        return ResponseEntity.ok(account);
    }

    // =========================================
    // GET MY ACCOUNT
    // =========================================

    @GetMapping("/my-account")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Account> getMyAccount(
            Authentication authentication) {

        String username =
                authentication.getName();

        return ResponseEntity.ok(
                accountService.getCustomerAccount(
                        username
                )
        );
    }
}