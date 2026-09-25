package com.example.online.banking.repo;

import com.example.online.banking.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByReferenceNumber(String referenceNumber);


    List<Transaction> findByFromAccountAccountNumberOrderByTransactionDateDesc(
            String accountNumber
    );

    List<Transaction> findByToAccountAccountNumberOrderByTransactionDateDesc(
            String accountNumber
    );

    List<Transaction> findByFromAccountAccountNumberAndTransactionDateBetweenOrderByTransactionDateDesc(
            String accountNumber,
            LocalDateTime fromDate,
            LocalDateTime toDate
    );

    List<Transaction> findByToAccountAccountNumberAndTransactionDateBetweenOrderByTransactionDateDesc(
            String accountNumber,
            LocalDateTime fromDate,
            LocalDateTime toDate
    );
}