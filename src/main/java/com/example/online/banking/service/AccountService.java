package com.example.online.banking.service;

import com.example.online.banking.ENum.*;
import com.example.online.banking.dto.DepositRequest;
import com.example.online.banking.dto.TransferRequest;
import com.example.online.banking.dto.WithdrawRequest;
import com.example.online.banking.exception.InsufficientBalanceException;
import com.example.online.banking.exception.ResourceNotFoundException;
import com.example.online.banking.model.*;
import com.example.online.banking.repo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountOpeningApplicationRepository applicationRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;
    private final OtpService otpService;
    private final OTPRepository otpRepository;
    private final CustomerRepository customerRepository;



    public Account getCustomerAccount(String username) {

        return accountRepository
                .findByCustomerUserUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found"));
    }

    public Account getAccountByNumber(String accountNumber) {

        return accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Account not found: " + accountNumber
                        )
                );
    }


    // =========================
    // CREATE ACCOUNT
    // =========================

    @Transactional
    public Account createAccount(Long applicationId) {

        AccountOpeningApplication application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found with id: "
                                                + applicationId
                                )
                        );

        if (application.getApplicationStatus()
                != ApplicationStatus.APPROVED) {

            throw new IllegalStateException(
                    "Only APPROVED applications can create an account"
            );
        }

        Account account = new Account();

        account.setAccountNumber(generateAccountNumber());
        account.setCustomer(application.getCustomer());
        account.setBranch(application.getBranch());
        account.setAccountType(application.getAccountType());
        account.setBalance(BigDecimal.ZERO);
        account.setCurrency("INR");
        account.setStatus(AccountStatus.ACTIVE);
        account.setOpenedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());

        account = accountRepository.save(account);

        application.setApplicationStatus(
                ApplicationStatus.ACCOUNT_CREATED
        );

        application.setUpdatedAt(LocalDateTime.now());

        applicationRepository.save(application);

        application.getCustomer().setStatus(
                CustomerStatus.ACTIVE
        );

        log.info(
                "Account created successfully. Account number: {}",
                account.getAccountNumber()
        );

        return account;
    }


    // =========================
    // DEPOSIT
    // =========================

    @Transactional
    public Transaction deposit(
            String accountNumber,
            DepositRequest request) {

        log.info(
                "Deposit requested for account: {}",
                accountNumber
        );

        Account account =
                accountRepository
                        .findByAccountNumber(accountNumber)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Account not found: "
                                                + accountNumber
                                )
                        );

        if (account.getStatus() != AccountStatus.ACTIVE) {

            throw new IllegalStateException(
                    "Account is not active"
            );
        }

        BigDecimal amount = request.amount();

        Transaction transaction = new Transaction();

        transaction.setReferenceNumber(
                generateTransactionReference()
        );

        transaction.setFromAccount(null);
        transaction.setToAccount(account);

        transaction.setTransactionType(
                TransactionType.DEPOSIT
        );

        transaction.setAmount(amount);

        transaction.setStatus(
                TransactionStatus.SUCCESS
        );

        transaction.setDescription(
                request.description()
        );

        transaction.setTransactionDate(
                LocalDateTime.now()
        );

        transaction.setCreatedAt(
                LocalDateTime.now()
        );

        transaction =
                transactionRepository.save(transaction);

        notificationService.createNotification(
                account.getCustomer().getUser(),
                "Deposit Successful",
                "₹" + amount
                        + " deposited successfully into your account.",
                NotificationType.DEPOSIT
        );

        log.info(
                "Deposit successful. Reference: {}, Account: {}, Amount: {}",
                transaction.getReferenceNumber(),
                accountNumber,
                amount
        );

        return transaction;
    }


    // =========================
    // WITHDRAW
    // =========================

    @Transactional
    public Transaction withdraw(
            String accountNumber,
            WithdrawRequest request) {

        log.info(
                "Withdrawal requested for account: {}",
                accountNumber
        );

        Account account =
                accountRepository
                        .findByAccountNumber(accountNumber)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Account not found: "
                                                + accountNumber
                                )
                        );

        if (account.getStatus() != AccountStatus.ACTIVE) {

            throw new IllegalStateException(
                    "Account is not active"
            );
        }

        BigDecimal amount = request.amount();

        if (account.getBalance().compareTo(amount) < 0) {

            throw new InsufficientBalanceException(
                    "Insufficient balance"
            );
        }

        Transaction transaction = new Transaction();

        transaction.setReferenceNumber(
                generateTransactionReference()
        );

        transaction.setFromAccount(account);
        transaction.setToAccount(null);

        transaction.setTransactionType(
                TransactionType.WITHDRAW
        );

        transaction.setAmount(amount);

        transaction.setStatus(
                TransactionStatus.SUCCESS
        );

        transaction.setDescription(
                request.description()
        );

        transaction.setTransactionDate(
                LocalDateTime.now()
        );

        transaction.setCreatedAt(
                LocalDateTime.now()
        );

        transaction =
                transactionRepository.save(transaction);

        notificationService.createNotification(
                account.getCustomer().getUser(),
                "Withdrawal Successful",
                "₹" + amount
                        + " withdrawn successfully from your account.",
                NotificationType.WITHDRAW
        );

        log.info(
                "Withdrawal successful. Reference: {}, Account: {}, Amount: {}",
                transaction.getReferenceNumber(),
                accountNumber,
                amount
        );

        return transaction;
    }


    // =========================


    // =========================
    // TRANSFER OTP
    // =========================

    @Transactional
    public Transaction transferWithOtp(
            String fromAccountNumber,
            String otpCode,
            String description,
            User user) {

        // =========================================
        // FIND ACTIVE TRANSFER OTP
        // =========================================

        OTP otp =
                otpRepository
                        .findTopByUserUserIdAndStatusOrderByCreatedAtDesc(
                                user.getUserId(),
                                OtpStatus.ACTIVE
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Active transfer OTP not found"
                                )
                        );


        // =========================================
        // CHECK OTP PURPOSE
        // =========================================

        if (otp.getPurpose()
                != OtpPurpose.MONEY_TRANSFER) {

            throw new IllegalStateException(
                    "Invalid OTP purpose"
            );
        }


        // =========================================
        // VERIFY OTP - ONLY ONCE
        // =========================================

        boolean verified =
                otpService.verifyOtp(
                        user,
                        otpCode
                );

        if (!verified) {

            throw new IllegalStateException(
                    "Invalid or expired OTP"
            );
        }


        // =========================================
        // GET TRANSFER DETAILS FROM OTP
        // =========================================

        String toAccountNumber =
                otp.getBeneficiaryAccountNumber();

        BigDecimal amount =
                otp.getTransactionAmount();


        // =========================================
        // FIND SENDER ACCOUNT
        // =========================================

        Account fromAccount =
                accountRepository
                        .findByAccountNumber(
                                fromAccountNumber
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Sender account not found"
                                )
                        );


        // =========================================
        // FIND RECEIVER ACCOUNT
        // =========================================

        Account toAccount =
                accountRepository
                        .findByAccountNumber(
                                toAccountNumber
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Receiver account not found"
                                )
                        );


        // =========================================
        // SAME ACCOUNT CHECK
        // =========================================

        if (fromAccount.getAccountNumber()
                .equals(toAccount.getAccountNumber())) {

            throw new IllegalStateException(
                    "Cannot transfer to the same account"
            );
        }


        // =========================================
        // SENDER STATUS
        // =========================================

        if (fromAccount.getStatus()
                != AccountStatus.ACTIVE) {

            throw new IllegalStateException(
                    "Sender account is not active"
            );
        }


        // =========================================
        // RECEIVER STATUS
        // =========================================

        if (toAccount.getStatus()
                != AccountStatus.ACTIVE) {

            throw new IllegalStateException(
                    "Receiver account is not active"
            );
        }


        // =========================================
        // BALANCE CHECK
        // =========================================

        if (fromAccount.getBalance()
                .compareTo(amount) < 0) {

            throw new InsufficientBalanceException(
                    "Insufficient balance"
            );
        }


        // =========================================
        // CREATE TRANSACTION
        // =========================================

        Transaction transaction =
                new Transaction();

        transaction.setReferenceNumber(
                generateTransactionReference()
        );

        transaction.setFromAccount(
                fromAccount
        );

        transaction.setToAccount(
                toAccount
        );

        transaction.setTransactionType(
                TransactionType.TRANSFER
        );

        transaction.setAmount(amount);

        transaction.setStatus(
                TransactionStatus.SUCCESS
        );

        transaction.setDescription(
                description
        );

        transaction.setTransactionDate(
                LocalDateTime.now()
        );

        transaction.setCreatedAt(
                LocalDateTime.now()
        );


        // =========================================
        // SAVE TRANSACTION
        // =========================================

        transaction =
                transactionRepository.save(
                        transaction
                );


        // =========================================
        // SENDER NOTIFICATION
        // =========================================

        notificationService.createNotification(
                fromAccount.getCustomer().getUser(),
                "Transfer Successful",
                "₹" + amount +
                        " transferred successfully.",
                NotificationType.TRANSFER
        );


        // =========================================
        // RECEIVER NOTIFICATION
        // =========================================

        notificationService.createNotification(
                toAccount.getCustomer().getUser(),
                "Money Received",
                "₹" + amount +
                        " received successfully in your account.",
                NotificationType.TRANSFER
        );


        // =========================================
        // LOG
        // =========================================

        log.info(
                "OTP transfer successful. Reference: {}, From: {}, To: {}, Amount: {}",
                transaction.getReferenceNumber(),
                fromAccountNumber,
                toAccountNumber,
                amount
        );


        return transaction;
    }


    // =========================
    // TRANSACTION HISTORY
    // =========================

    @Transactional
    public List<Transaction> getTransactionHistory(
            String accountNumber) {

        accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Account not found: "
                                        + accountNumber
                        )
                );

        List<Transaction> sent =
                transactionRepository
                        .findByFromAccountAccountNumberOrderByTransactionDateDesc(
                                accountNumber
                        );

        List<Transaction> received =
                transactionRepository
                        .findByToAccountAccountNumberOrderByTransactionDateDesc(
                                accountNumber
                        );

        List<Transaction> history =
                new ArrayList<>();

        history.addAll(sent);
        history.addAll(received);

        history.sort(
                Comparator.comparing(
                        Transaction::getTransactionDate
                ).reversed()
        );

        return history;
    }


    // =========================
    // ACCOUNT STATEMENT
    // =========================

    @Transactional(readOnly = true)
    public List<Transaction> getStatement(
            String accountNumber,
            LocalDate fromDate,
            LocalDate toDate) {

        if (fromDate.isAfter(toDate)) {

            throw new IllegalArgumentException(
                    "From date cannot be after to date"
            );
        }

        accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Account not found: "
                                        + accountNumber
                        )
                );

        LocalDateTime start =
                fromDate.atStartOfDay();

        LocalDateTime end =
                toDate
                        .plusDays(1)
                        .atStartOfDay()
                        .minusNanos(1);

        List<Transaction> sent =
                transactionRepository
                        .findByFromAccountAccountNumberAndTransactionDateBetweenOrderByTransactionDateDesc(
                                accountNumber,
                                start,
                                end
                        );

        List<Transaction> received =
                transactionRepository
                        .findByToAccountAccountNumberAndTransactionDateBetweenOrderByTransactionDateDesc(
                                accountNumber,
                                start,
                                end
                        );

        List<Transaction> statement =
                new ArrayList<>();

        statement.addAll(sent);
        statement.addAll(received);

        statement.sort(
                Comparator.comparing(
                        Transaction::getTransactionDate
                ).reversed()
        );

        return statement;
    }


    // =========================
    // ACCOUNT NUMBER GENERATOR
    // =========================

    private String generateAccountNumber() {

        return "AC" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 12)
                        .toUpperCase();
    }


    // =========================
    // TRANSACTION REFERENCE
    // =========================

    private String generateTransactionReference() {

        return "TXN-" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 12)
                        .toUpperCase();
    }
}