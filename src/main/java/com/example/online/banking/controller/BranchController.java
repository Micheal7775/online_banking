package com.example.online.banking.controller;

import com.example.online.banking.dto.CreateBranchRequest;
import com.example.online.banking.model.Branch;
import com.example.online.banking.service.BranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Branch> createBranch(
            @Valid @RequestBody CreateBranchRequest request) {

        Branch branch =
                branchService.createBranch(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(branch);
    }
}