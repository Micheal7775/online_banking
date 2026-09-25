package com.example.online.banking.controller;

import com.example.online.banking.exception.ResourceNotFoundException;
import com.example.online.banking.model.Card;
import com.example.online.banking.repo.CardRepository;
import com.example.online.banking.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final CardRepository cardRepository;


    // =========================================
    // ISSUE DEBIT CARD
    // =========================================

    @PostMapping("/issue/{accountNumber}")
    @PreAuthorize("hasRole('BANK_MANAGER')")
    public ResponseEntity<Card> issueDebitCard(
            @PathVariable String accountNumber) {

        Card card =
                cardService.issueDebitCard(accountNumber);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(card);
    }


    // =========================================
    // GET MY CARD
    // =========================================

    @GetMapping("/my-card")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Card> getMyCard(
            Authentication authentication) {

        String username = authentication.getName();

        Card card = cardRepository
                .findByAccountCustomerUserUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Card not found"
                        ));

        return ResponseEntity.ok(card);
    }


    // =========================================
    // SET CARD PIN
    // =========================================

    @PostMapping("/pin")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> setCardPin(
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        String pin = request.get("pin");

        cardService.setCardPin(
                authentication.getName(),
                pin
        );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Card PIN set successfully"
                )
        );
    }


    @PostMapping("/my-card/block")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Card> blockMyCard(
            Authentication authentication) {

        String username = authentication.getName();

        Card card =
                cardService.blockMyCard(username);

        return ResponseEntity.ok(card);
    }
    // =========================================
    // VERIFY CARD PIN
    // =========================================

    @PostMapping("/pin/verify")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> verifyCardPin(
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        String pin = request.get("pin");

        boolean valid =
                cardService.verifyCardPin(
                        authentication.getName(),
                        pin
                );

        if (!valid) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Incorrect card PIN",
                            "valid",
                            false
                    )
            );
        }

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Card PIN verified successfully",
                        "valid",
                        true
                )
        );
    }


    // =========================================
    // BLOCK CARD
    // =========================================

    @PutMapping("/{cardId}/block")
    @PreAuthorize("hasRole('BANK_MANAGER')")
    public ResponseEntity<Card> blockCard(
            @PathVariable Long cardId) {

        Card card =
                cardService.blockCard(cardId);

        return ResponseEntity.ok(card);
    }


    // =========================================
    // UNBLOCK CARD
    // =========================================

    @PutMapping("/{cardId}/unblock")
    @PreAuthorize("hasRole('BANK_MANAGER')")
    public ResponseEntity<Card> unblockCard(
            @PathVariable Long cardId) {

        Card card =
                cardService.unblockCard(cardId);

        return ResponseEntity.ok(card);
    }
}