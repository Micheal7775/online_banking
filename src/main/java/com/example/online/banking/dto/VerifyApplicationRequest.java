package com.example.online.banking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VerifyApplicationRequest(

        @NotNull(message = "Verification decision is required")
        Boolean verified,

        @Size(max = 500, message = "Remarks cannot exceed 500 characters")
        String remarks
) {
}