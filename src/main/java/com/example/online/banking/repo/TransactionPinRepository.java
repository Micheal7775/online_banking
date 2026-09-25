package com.example.online.banking.repo;

import com.example.online.banking.model.TransactionPin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionPinRepository
        extends JpaRepository<TransactionPin, Long> {

    Optional<TransactionPin> findByCustomerCustomerId(Long customerId);

    boolean existsByCustomerCustomerId(Long customerId);
}