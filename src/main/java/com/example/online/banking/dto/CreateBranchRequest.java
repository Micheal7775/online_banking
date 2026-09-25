package com.example.online.banking.dto;

import com.example.online.banking.ENum.BranchStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateBranchRequest(

        @NotBlank(message = "Branch code is required")
        String branchCode,

        @NotBlank(message = "Branch name is required")
        String branchName,

        @NotBlank(message = "IFSC code is required")
        String ifscCode,

        @NotBlank(message = "Address is required")
        String address,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "State is required")
        String state,

        @NotBlank(message = "Pincode is required")
        String pincode,

        String phoneNumber,

        @Email(message = "Invalid email format")
        String email,

        @NotNull(message = "Branch status is required")
        BranchStatus status,

        @NotNull(message = "Opened date is required")
        LocalDate openedAt
) {
}