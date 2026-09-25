package com.example.online.banking.dto;

import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(

        @NotBlank(message = "Username is required")
        String username
) {
}