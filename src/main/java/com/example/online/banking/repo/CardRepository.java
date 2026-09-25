package com.example.online.banking.repo;

import com.example.online.banking.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardNumber(String cardNumber);

    List<Card> findByAccountAccountNumber(String accountNumber);

    boolean existsByCardNumber(String cardNumber);

    Optional<Card>  findByAccountCustomerUserUsername(String username);
}