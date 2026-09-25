package com.example.online.banking.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record StatementRequest(

        @NotNull(message = "From date is required")
        LocalDate fromDate,

        @NotNull(message = "To date is required")
        LocalDate toDate
) {
}