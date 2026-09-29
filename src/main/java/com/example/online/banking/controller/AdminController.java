package com.example.online.banking.controller;

import com.example.online.banking.model.Branch;
import com.example.online.banking.model.Customer;
import com.example.online.banking.model.Employee;
import com.example.online.banking.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;


    // =====================================================
    // EMPLOYEE
    // =====================================================

    // GET ALL EMPLOYEES

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees() {

        List<Employee> employees =
                adminService.getAllEmployees();

        return ResponseEntity.ok(employees);
    }


    // GET EMPLOYEE BY ID

    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<Employee> getEmployeeById(
            @PathVariable Long employeeId) {

        Employee employee =
                adminService.getEmployeeById(employeeId);

        return ResponseEntity.ok(employee);
    }


    // UPDATE EMPLOYEE

    @PutMapping("/employees/{employeeId}")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable Long employeeId,
            @RequestBody Employee employee) {

        Employee updatedEmployee =
                adminService.updateEmployee(
                        employeeId,
                        employee
                );

        return ResponseEntity.ok(updatedEmployee);
    }


    // DELETE EMPLOYEE

    @DeleteMapping("/employees/{employeeId}")
    public ResponseEntity<String> deleteEmployee(
            @PathVariable Long employeeId) {

        adminService.deleteEmployee(employeeId);

        return ResponseEntity.ok(
                "Employee deleted successfully"
        );
    }


    // =====================================================
    // CUSTOMER
    // =====================================================

    // GET ALL CUSTOMERS

    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {

        List<Customer> customers =
                adminService.getAllCustomers();

        return ResponseEntity.ok(customers);
    }


    // GET CUSTOMER BY ID

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<Customer> getCustomerById(
            @PathVariable Long customerId) {

        Customer customer =
                adminService.getCustomerById(customerId);

        return ResponseEntity.ok(customer);
    }


    // UPDATE CUSTOMER

    @PutMapping("/customers/{customerId}")
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


    // DELETE CUSTOMER

    @DeleteMapping("/customers/{customerId}")
    public ResponseEntity<String> deleteCustomer(
            @PathVariable Long customerId) {

        adminService.deleteCustomer(customerId);

        return ResponseEntity.ok(
                "Customer deleted successfully"
        );
    }


    // =====================================================
    // BRANCH
    // =====================================================

    // GET ALL BRANCHES

    @GetMapping("/branches")
    public ResponseEntity<List<Branch>> getAllBranches() {

        List<Branch> branches =
                adminService.getAllBranches();

        return ResponseEntity.ok(branches);
    }


    // GET BRANCH BY ID

    @GetMapping("/branches/{branchId}")
    public ResponseEntity<Branch> getBranchById(
            @PathVariable Long branchId) {

        Branch branch =
                adminService.getBranchById(branchId);

        return ResponseEntity.ok(branch);
    }


    // CREATE BRANCH


    // UPDATE BRANCH

    @PutMapping("/branches/{branchId}")
    public ResponseEntity<Branch> updateBranch(
            @PathVariable Long branchId,
            @RequestBody Branch branch) {

        Branch updatedBranch =
                adminService.updateBranch(
                        branchId,
                        branch
                );

        return ResponseEntity.ok(updatedBranch);
    }


    // DELETE BRANCH

    @DeleteMapping("/branches/{branchId}")
    public ResponseEntity<String> deleteBranch(
            @PathVariable Long branchId) {

        adminService.deleteCustomer(branchId);

        return ResponseEntity.ok(
                "Branch deleted successfully"
        );
    }

}