package com.example.online.banking.service;

import com.example.online.banking.ENum.EmployeeStatus;
import com.example.online.banking.ENum.Role;
import com.example.online.banking.ENum.UserStatus;
import com.example.online.banking.dto.CreateEmployeeRequest;
import com.example.online.banking.exception.DuplicateResourceException;
import com.example.online.banking.exception.ResourceNotFoundException;
import com.example.online.banking.model.Branch;
import com.example.online.banking.model.Employee;
import com.example.online.banking.model.User;
import com.example.online.banking.repo.BranchRepository;
import com.example.online.banking.repo.EmployeeRepository;
import com.example.online.banking.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Employee createEmployee(
            CreateEmployeeRequest request) {

        /*
         * Username duplicate check
         */

        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException(
                    "Username already exists"
            );
        }

        /*
         * Only employee roles are allowed
         */

        if (request.role() == Role.ADMIN ||
                request.role() == Role.CUSTOMER) {

            throw new IllegalArgumentException(
                    "Invalid employee role"
            );
        }

        /*
         * Find branch
         */

        Branch branch = branchRepository
                .findById(request.branchId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Branch not found with id: "
                                        + request.branchId()
                        )
                );

        /*
         * Create User
         */

        User user = new User();

        user.setUsername(request.username());

        user.setPassword(
                passwordEncoder.encode(
                        request.password()
                )
        );

        user.setRole(request.role());

        user.setStatus(UserStatus.ACTIVE);

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        user = userRepository.save(user);

        /*
         * Create Employee
         */

        Employee employee = new Employee();

        employee.setUser(user);

        employee.setEmployeeNumber(
                generateEmployeeNumber()
        );

        employee.setFullName(
                request.fullName()
        );

        employee.setEmail(
                request.email()
        );

        employee.setMobileNumber(
                request.mobileNumber()
        );

        employee.setBranch(branch);

        employee.setJoiningDate(
                request.joiningDate()
        );

        employee.setStatus(
                EmployeeStatus.ACTIVE
        );

        employee.setCreatedAt(
                LocalDateTime.now()
        );

        employee.setUpdatedAt(
                LocalDateTime.now()
        );

        return employeeRepository.save(employee);
    }

    private String generateEmployeeNumber() {

        return "EMP-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();
    }
}