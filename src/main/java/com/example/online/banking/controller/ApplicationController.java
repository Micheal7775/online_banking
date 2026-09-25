package com.example.online.banking.controller;

import com.example.online.banking.dto.ApproveApplicationRequest;
import com.example.online.banking.dto.VerifyApplicationRequest;
import com.example.online.banking.model.AccountOpeningApplication;
import com.example.online.banking.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/verification/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    // Document Verification Staff
    @GetMapping
    @PreAuthorize("hasRole('DOCUMENT_VERIFICATION_STAFF')")
    public ResponseEntity<List<AccountOpeningApplication>>
    getSubmittedApplications() {

        return ResponseEntity.ok(
                applicationService.getSubmittedApplications()
        );
    }

    // Bank Manager
    @GetMapping("/verified")
    @PreAuthorize("hasRole('BANK_MANAGER')")
    public ResponseEntity<List<AccountOpeningApplication>>
    getVerifiedApplications() {

        return ResponseEntity.ok(
                applicationService.getVerifiedApplications()
        );
    }

    // Document Verification Staff
    @PutMapping("/{applicationId}/verify")
    @PreAuthorize("hasRole('DOCUMENT_VERIFICATION_STAFF')")
    public ResponseEntity<AccountOpeningApplication>
    verifyApplication(
            @PathVariable Long applicationId,
            @Valid @RequestBody VerifyApplicationRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        AccountOpeningApplication application =
                applicationService.verifyApplication(
                        applicationId,
                        request,
                        username
                );

        return ResponseEntity.ok(application);
    }

    // Bank Manager
    @PutMapping("/{applicationId}/approve")
    @PreAuthorize("hasRole('BANK_MANAGER')")
    public ResponseEntity<AccountOpeningApplication>
    approveApplication(
            @PathVariable Long applicationId,
            @Valid @RequestBody ApproveApplicationRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        AccountOpeningApplication application =
                applicationService.approveApplication(
                        applicationId,
                        request,
                        username
                );

        return ResponseEntity.ok(application);
    }
}