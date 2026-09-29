package com.example.online.banking.controller;

import com.example.online.banking.dto.CreateCustomerRequest;
import com.example.online.banking.model.Customer;
import com.example.online.banking.service.AdminService;
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
    private final AdminService adminService;


    // =====================================================
    // CREATE CUSTOMER
    // POST /api/staff/customers
    // =====================================================

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


    // =====================================================
    // GET CUSTOMER BY ID
    // GET /api/staff/customers/{customerId}
    // =====================================================

    @GetMapping("/{customerId}")
    @PreAuthorize("hasRole('ACCOUNT_OPENING_STAFF')")
    public ResponseEntity<Customer> getCustomerById(
            @PathVariable Long customerId) {

        Customer customer =
                adminService.getCustomerById(customerId);

        return ResponseEntity.ok(customer);
    }


    // =====================================================
    // UPDATE CUSTOMER
    // PUT /api/staff/customers/{customerId}
    // =====================================================

    @PutMapping("/{customerId}")
    @PreAuthorize("hasRole('ACCOUNT_OPENING_STAFF')")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable Long customerId,
            @RequestBody Customer customer) {

        Customer updatedCustomer =
                adminService.updateCustomer(
                        customerId,
                        customer
                );

        return ResponseEntity.ok(updatedCustomer);
    }


    // =====================================================
    // DELETE CUSTOMER
    // DELETE /api/staff/customers/{customerId}
    // =====================================================

    @DeleteMapping("/{customerId}")
    @PreAuthorize("hasRole('ACCOUNT_OPENING_STAFF')")
    public ResponseEntity<String> deleteCustomer(
            @PathVariable Long customerId) {

        adminService.deleteCustomer(customerId);

        return ResponseEntity.ok(
                "Customer deleted successfully"
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ACCOUNT_OPENING_STAFF')")
    public ResponseEntity<?> getAllCustomers() {
        return ResponseEntity.ok(
                adminService.getAllCustomers()
        );
    }
}