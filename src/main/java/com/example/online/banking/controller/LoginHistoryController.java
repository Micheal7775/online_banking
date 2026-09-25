package com.example.online.banking.controller;

import com.example.online.banking.exception.ResourceNotFoundException;
import com.example.online.banking.model.LoginHistory;
import com.example.online.banking.model.User;
import com.example.online.banking.repo.UserRepository;
import com.example.online.banking.service.LoginHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/login-history")
@RequiredArgsConstructor
public class LoginHistoryController {

    private final LoginHistoryService loginHistoryService;
    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<LoginHistory>> getLoginHistory(
            Authentication authentication) {

        User user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        List<LoginHistory> history =
                loginHistoryService.getLoginHistory(
                        user.getUserId()
                );

        return ResponseEntity.ok(history);
    }
}