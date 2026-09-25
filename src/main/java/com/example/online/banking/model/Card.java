package com.example.online.banking.model;

import com.example.online.banking.ENum.CardStatus;
import com.example.online.banking.ENum.CardType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "cards",
        indexes = {
                @Index(name = "idx_card_account", columnList = "account_id"),
                @Index(name = "idx_card_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;


    // =========================================
    // CARD NUMBER
    // =========================================

    @Column(nullable = false, unique = true)
    private String cardNumber;


    // =========================================
    // ACCOUNT
    // =========================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;


    // =========================================
    // CARD TYPE
    // =========================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardType cardType;


    // =========================================
    // EXPIRY DATE
    // =========================================

    @Column(nullable = false)
    private LocalDate expiryDate;


    // =========================================
    // CARD STATUS
    // =========================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status;


    // =========================================
    // CARD PIN HASH
    // =========================================

    @Column(name = "pin_hash")
    private String pinHash;


    // =========================================
    // ISSUED DATE
    // =========================================

    @Column(nullable = false)
    private LocalDateTime issuedAt;


    // =========================================
    // BLOCKED DATE
    // =========================================

    private LocalDateTime blockedAt;
}