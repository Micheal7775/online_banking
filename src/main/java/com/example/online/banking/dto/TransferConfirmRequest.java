package com.example.online.banking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TransferConfirmRequest(



        @NotBlank(message = "OTP is required")
        String otp,

        @Size(max = 200, message = "Description cannot exceed 200 characters")
        String description
) {
}