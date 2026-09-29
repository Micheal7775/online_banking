package com.example.online.banking.service;


import com.example.online.banking.ENum.*;
import com.example.online.banking.dto.CreateCustomerRequest;
import com.example.online.banking.exception.DuplicateResourceException;
import com.example.online.banking.exception.ResourceNotFoundException;
import com.example.online.banking.model.AccountOpeningApplication;
import com.example.online.banking.model.Customer;
import com.example.online.banking.model.Employee;
import com.example.online.banking.model.User;
import com.example.online.banking.repo.AccountOpeningApplicationRepository;
import com.example.online.banking.repo.CustomerRepository;
import com.example.online.banking.repo.EmployeeRepository;
import com.example.online.banking.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final AccountOpeningApplicationRepository applicationRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Customer createCustomer(
            CreateCustomerRequest request,
            String loggedInUsername) {

        log.info(
                "Customer creation requested by username: {}",
                loggedInUsername
        );

        /*
         * Find logged-in staff user
         */

        User staffUser = userRepository
                .findByUsername(loggedInUsername)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Staff user not found: " + loggedInUsername
                        )
                );

        /*
         * Check staff role
         */

        if (staffUser.getRole() != Role.ACCOUNT_OPENING_STAFF) {

            throw new IllegalStateException(
                    "Only Account Opening Staff can create customers"
            );
        }

        /*
         * Find employee profile
         */

        Employee employee = employeeRepository
                .findByUserUserId(staffUser.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee profile not found for user: "
                                        + loggedInUsername
                        )
                );

        /*
         * Check duplicate email
         */

        if (customerRepository.existsByEmail(request.email())) {

            throw new DuplicateResourceException(
                    "Email already registered"
            );
        }

        /*
         * Check duplicate mobile
         */

        if (customerRepository.existsByMobileNumber(
                request.mobileNumber())) {

            throw new DuplicateResourceException(
                    "Mobile number already registered"
            );
        }

        /*
         * Check duplicate Aadhaar
         */

        if (request.aadhaarNumber() != null &&
                customerRepository.existsByAadhaarNumber(
                        request.aadhaarNumber())) {

            throw new DuplicateResourceException(
                    "Aadhaar number already registered"
            );
        }

        /*
         * Check duplicate PAN
         */

        if (request.panNumber() != null &&
                customerRepository.existsByPanNumber(
                        request.panNumber())) {

            throw new DuplicateResourceException(
                    "PAN number already registered"
            );
        }

        /*
         * Create CUSTOMER login user
         */

        String username =
                generateUsername(request.mobileNumber());

        User customerUser = new User();

        customerUser.setUsername(username);

        customerUser.setPassword(
                passwordEncoder.encode("Temp@12345")
        );

        /*
         * Backend decides customer role
         */

        customerUser.setRole(Role.CUSTOMER);

        customerUser.setStatus(UserStatus.ACTIVE);

        customerUser.setCreatedAt(
                LocalDateTime.now()
        );

        customerUser.setUpdatedAt(
                LocalDateTime.now()
        );

        customerUser = userRepository.save(customerUser);

        /*
         * Create Customer profile
         */

        Customer customer = new Customer();

        customer.setUser(customerUser);

        customer.setCustomerNumber(
                generateUniqueUsername()
        );

        customer.setFullName(
                request.fullName()
        );

        customer.setDateOfBirth(
                request.dateOfBirth()
        );

        customer.setGender(
                request.gender()
        );

        customer.setEmail(
                request.email()
        );

        customer.setMobileNumber(
                request.mobileNumber()
        );

        customer.setAddress(
                request.address()
        );

        customer.setCity(
                request.city()
        );

        customer.setState(
                request.state()
        );

        customer.setPincode(
                request.pincode()
        );

        customer.setAadhaarNumber(
                request.aadhaarNumber()
        );

        customer.setPanNumber(
                request.panNumber()
        );

        /*
         * Customer becomes ACTIVE
         * only after account approval.
         */

        customer.setStatus(
                CustomerStatus.INACTIVE
        );

        customer.setCreatedAt(
                LocalDateTime.now()
        );

        customer.setUpdatedAt(
                LocalDateTime.now()
        );

        customer = customerRepository.save(customer);

        /*
         * Create Account Opening Application
         */

        AccountOpeningApplication application =
                new AccountOpeningApplication();

        application.setCustomer(customer);

        application.setBranch(
                employee.getBranch()
        );

        /*
         * Use account type from request
         */

        application.setAccountType(
                AccountType.CURRENT
        );

        application.setApplicationStatus(
                ApplicationStatus.SUBMITTED
        );

        application.setCreatedBy(
                employee
        );

        application.setSubmittedAt(
                LocalDateTime.now()
        );

        application.setCreatedAt(
                LocalDateTime.now()
        );

        application.setUpdatedAt(
                LocalDateTime.now()
        );

        applicationRepository.save(application);

        log.info(
                "Customer created successfully. Customer number: {}",
                customer.getCustomerNumber()
        );

        return customer;
    }

    private String generateUsername(
            String mobileNumber) {

        return "CUS" + mobileNumber;
    }

    private String generateUniqueUsername() {

        String username;

        do {
            username =
                    "CUS" +
                            (1000000000L +
                                    new java.util.Random().nextLong(900000000L));
        }
        while (userRepository.existsByUsername(username));

        return username;
    }
}

