package com.example.online.banking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "OTP is required")
        String otp,

        @NotBlank(message = "New password is required")
        @Size(
                min = 8,
                message = "Password must contain at least 8 characters"
        )
        String newPassword
) {
}