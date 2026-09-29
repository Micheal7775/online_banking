package com.example.online.banking.service;

import com.example.online.banking.exception.ResourceNotFoundException;
import com.example.online.banking.model.Branch;
import com.example.online.banking.model.Customer;
import com.example.online.banking.model.Employee;
import com.example.online.banking.repo.AccountOpeningApplicationRepository;
import com.example.online.banking.repo.BranchRepository;
import com.example.online.banking.repo.CustomerRepository;
import com.example.online.banking.repo.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final BranchRepository branchRepository;

    private  final AccountOpeningApplicationRepository applicationRepository;


    // =====================================================
    // EMPLOYEE
    // =====================================================

    public List<Employee> getAllEmployees() {

        return employeeRepository.findAll();
    }


    public Employee getEmployeeById(Long employeeId) {

        return employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found with id: "
                                        + employeeId
                        )
                );
    }


    @Transactional
    public Employee updateEmployee(
            Long employeeId,
            Employee request) {

        Employee employee =
                employeeRepository.findById(employeeId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Employee not found with id: "
                                                + employeeId
                                )
                        );


        // FULL NAME
        if (request.getFullName() != null) {

            employee.setFullName(
                    request.getFullName()
            );
        }


        // EMAIL
        if (request.getEmail() != null) {

            employee.setEmail(
                    request.getEmail()
            );
        }


        // MOBILE NUMBER
        if (request.getMobileNumber() != null) {

            employee.setMobileNumber(
                    request.getMobileNumber()
            );
        }


        // ROLE
        // Role is stored in User entity
        if (request.getRole() != null
                && employee.getUser() != null) {

            employee.getUser().setRole(
                    request.getRole()
            );
        }


        // STATUS
        if (request.getStatus() != null) {

            employee.setStatus(
                    request.getStatus()
            );
        }


        // JOINING DATE
        if (request.getJoiningDate() != null) {

            employee.setJoiningDate(
                    request.getJoiningDate()
            );
        }


        return employeeRepository.save(employee);
    }
    @Transactional
    public void deleteEmployee(Long employeeId) {

        Employee employee =
                employeeRepository.findById(employeeId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Employee not found with id: "
                                                + employeeId
                                )
                        );

;

        employeeRepository.delete(employee);
    }


    // =====================================================
    // CUSTOMER
    // =====================================================

    public List<Customer> getAllCustomers() {

        return customerRepository.findAll();
    }


    public Customer getCustomerById(Long customerId) {

        return customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with id: "
                                        + customerId
                        )
                );
    }


    @Transactional
    public Customer updateCustomer(
            Long customerId,
            Customer request) {

        Customer customer =
                customerRepository.findById(customerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found with id: "
                                                + customerId
                                )
                        );


        // FULL NAME
        if (request.getFullName() != null) {

            customer.setFullName(
                    request.getFullName()
            );
        }


        // EMAIL
        if (request.getEmail() != null) {

            customer.setEmail(
                    request.getEmail()
            );
        }


        // MOBILE NUMBER
        if (request.getMobileNumber() != null) {

            customer.setMobileNumber(
                    request.getMobileNumber()
            );
        }


        // ADDRESS
        if (request.getAddress() != null) {

            customer.setAddress(
                    request.getAddress()
            );
        }


        // AADHAAR
        if (request.getAadhaarNumber() != null) {

            customer.setAadhaarNumber(
                    request.getAadhaarNumber()
            );
        }


        // PAN
        if (request.getPanNumber() != null) {

            customer.setPanNumber(
                    request.getPanNumber()
            );
        }


        // STATUS
        if (request.getStatus() != null) {

            customer.setStatus(
                    request.getStatus()
            );
        }


        return customerRepository.save(customer);
    }


    // =====================================================
    // BRANCH
    // =====================================================

    public List<Branch> getAllBranches() {

        return branchRepository.findAll();
    }


    public Branch getBranchById(Long branchId) {

        return branchRepository.findById(branchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Branch not found with id: "
                                        + branchId
                        )
                );
    }


    @Transactional
    public Branch createBranch(Branch branch) {

        return branchRepository.save(branch);
    }


    @Transactional
    public Branch updateBranch(
            Long branchId,
            Branch request) {

        Branch branch =
                branchRepository.findById(branchId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Branch not found with id: "
                                                + branchId
                                )
                        );


        // BRANCH CODE
        if (request.getBranchCode() != null) {

            branch.setBranchCode(
                    request.getBranchCode()
            );
        }


        // IFSC CODE
        if (request.getIfscCode() != null) {

            branch.setIfscCode(
                    request.getIfscCode()
            );
        }


        // BRANCH NAME
        if (request.getBranchName() != null) {

            branch.setBranchName(
                    request.getBranchName()
            );
        }


        // ADDRESS
        if (request.getAddress() != null) {

            branch.setAddress(
                    request.getAddress()
            );
        }


        // CITY
        if (request.getCity() != null) {

            branch.setCity(
                    request.getCity()
            );
        }


        // STATE
        if (request.getState() != null) {

            branch.setState(
                    request.getState()
            );
        }


        // PINCODE
        if (request.getPincode() != null) {

            branch.setPincode(
                    request.getPincode()
            );
        }


        // STATUS
        if (request.getStatus() != null) {

            branch.setStatus(
                    request.getStatus()
            );
        }


        return branchRepository.save(branch);
    }


    @Transactional
    public void deleteCustomer(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found"
                        )
                );

        // Delete application records first
        applicationRepository
                .deleteByCustomerCustomerId(customerId);

        // Then delete customer
        customerRepository.delete(customer);
    }
    }

