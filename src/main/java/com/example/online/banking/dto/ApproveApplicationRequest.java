package com.example.online.banking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ApproveApplicationRequest(

        @NotNull(message = "Approval decision is required")
        Boolean approved,

        @Size(max = 500, message = "Remarks cannot exceed 500 characters")
        String remarks
) {
}