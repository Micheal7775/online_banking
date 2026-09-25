package com.example.online.banking.service;

import com.example.online.banking.ENum.AccountStatus;
import com.example.online.banking.ENum.CardStatus;
import com.example.online.banking.ENum.CardType;
import com.example.online.banking.exception.DuplicateResourceException;
import com.example.online.banking.exception.ResourceNotFoundException;
import com.example.online.banking.model.Account;
import com.example.online.banking.model.Card;
import com.example.online.banking.repo.AccountRepository;
import com.example.online.banking.repo.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;


    // =========================================
    // ISSUE DEBIT CARD
    // =========================================

    @Transactional
    public Card issueDebitCard(String accountNumber) {

        log.info(
                "Debit card issue requested for account: {}",
                accountNumber
        );

        Account account = accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Account not found: " + accountNumber
                        )
                );

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Card cannot be issued for inactive account"
            );
        }

        Card card = new Card();

        String cardNumber = generateCardNumber();

        if (cardRepository.existsByCardNumber(cardNumber)) {
            throw new DuplicateResourceException(
                    "Card number already exists"
            );
        }

        card.setCardNumber(cardNumber);
        card.setAccount(account);
        card.setCardType(CardType.DEBIT);

        card.setExpiryDate(
                LocalDate.now().plusYears(5)
        );

        card.setStatus(CardStatus.ACTIVE);

        card.setIssuedAt(LocalDateTime.now());

        card = cardRepository.save(card);

        log.info(
                "Debit card issued successfully for account: {}",
                accountNumber
        );

        return card;
    }


    // =========================================
    // SET CARD PIN
    // =========================================

    @Transactional
    public void setCardPin(
            String username,
            String pin) {

        if (pin == null || !pin.matches("\\d{4}")) {
            throw new IllegalArgumentException(
                    "Card PIN must contain exactly 4 digits"
            );
        }

        Card card = cardRepository
                .findByAccountCustomerUserUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Card not found"
                        )
                );

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Card is not active"
            );
        }

        card.setPinHash(
                passwordEncoder.encode(pin)
        );

        cardRepository.save(card);

        log.info(
                "Card PIN set successfully for customer: {}",
                username
        );
    }


    // =========================================
    // VERIFY CARD PIN
    // =========================================

    public boolean verifyCardPin(
            String username,
            String pin) {

        if (pin == null || !pin.matches("\\d{4}")) {
            return false;
        }

        Card card = cardRepository
                .findByAccountCustomerUserUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Card not found"
                        )
                );

        if (card.getStatus() != CardStatus.ACTIVE) {
            return false;
        }

        if (card.getPinHash() == null) {
            throw new IllegalStateException(
                    "Card PIN is not set"
            );
        }

        return passwordEncoder.matches(
                pin,
                card.getPinHash()
        );
    }

    @Transactional
    public Card blockMyCard(String username) {

        log.info("Customer card block requested by: {}", username);

        Card card = cardRepository
                .findByAccountCustomerUserUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Card not found"
                        ));

        if (card.getStatus() == CardStatus.EXPIRED) {
            throw new IllegalStateException(
                    "Expired card cannot be blocked"
            );
        }

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new IllegalStateException(
                    "Card is already blocked"
            );
        }

        card.setStatus(CardStatus.BLOCKED);
        card.setBlockedAt(LocalDateTime.now());

        card = cardRepository.save(card);

        log.info(
                "Customer card blocked successfully. Username: {}",
                username
        );

        return card;
    }


    // =========================================
    // BLOCK CARD
    // =========================================

    @Transactional
    public Card blockCard(Long cardId) {

        log.info(
                "Card block requested. Card ID: {}",
                cardId
        );

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Card not found with id: " + cardId
                        )
                );

        if (card.getStatus() == CardStatus.EXPIRED) {
            throw new IllegalStateException(
                    "Expired card cannot be blocked"
            );
        }

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new IllegalStateException(
                    "Card is already blocked"
            );
        }

        card.setStatus(CardStatus.BLOCKED);
        card.setBlockedAt(LocalDateTime.now());

        card = cardRepository.save(card);

        log.info(
                "Card blocked successfully. Card ID: {}",
                cardId
        );

        return card;
    }


    // =========================================
    // UNBLOCK CARD
    // =========================================

    @Transactional
    public Card unblockCard(Long cardId) {

        log.info(
                "Card unblock requested. Card ID: {}",
                cardId
        );

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Card not found with id: " + cardId
                        )
                );

        if (card.getStatus() == CardStatus.EXPIRED) {
            throw new IllegalStateException(
                    "Expired card cannot be unblocked"
            );
        }

        if (card.getStatus() == CardStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Card is already active"
            );
        }

        card.setStatus(CardStatus.ACTIVE);
        card.setBlockedAt(null);

        card = cardRepository.save(card);

        log.info(
                "Card unblocked successfully. Card ID: {}",
                cardId
        );

        return card;
    }


    // =========================================
    // GENERATE CARD NUMBER
    // =========================================

    private String generateCardNumber() {

        return "4" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 15);
    }
}