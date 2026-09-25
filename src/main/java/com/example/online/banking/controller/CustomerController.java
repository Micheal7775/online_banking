package com.example.online.banking.controller;


import com.example.online.banking.dto.CreateCustomerRequest;
import com.example.online.banking.model.Customer;
import com.example.online.banking.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasRole('ACCOUNT_OPENING_STAFF')")
    public ResponseEntity<Customer> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request,
            Authentication authentication) {

        String loggedInUsername =
                authentication.getName();

        Customer customer =
                customerService.createCustomer(
                        request,
                        loggedInUsername
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customer);
    }
}

