package com.example.online.banking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record CreateCustomerRequest(

        @NotBlank(message = "Full name is required")
        String fullName,

        LocalDate dateOfBirth,

        String gender,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Mobile number is required")
        @Pattern(
                regexp = "^[0-9]{10}$",
                message = "Mobile number must contain exactly 10 digits"
        )
        String mobileNumber,

        String address,

        String city,

        String state,

        String pincode,

        @Pattern(
                regexp = "^[0-9]{12}$",
                message = "Aadhaar number must contain exactly 12 digits"
        )
        String aadhaarNumber,

        @Pattern(
                regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$",
                message = "Invalid PAN format"
        )
        String panNumber
) {
}