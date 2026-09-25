package com.example.online.banking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TransferRequest(

        @NotBlank(message = "To account number is required")
        String toAccountNumber,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.00", message = "Amount must be at least 1")
        BigDecimal amount,

        @NotBlank(message = "OTP is required")
        String otp,

        @Size(max = 200, message = "Description cannot exceed 200 characters")
        String description
) {
}