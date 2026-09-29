package com.example.online.banking.service;

import com.example.online.banking.ENum.ApplicationStatus;
import com.example.online.banking.ENum.Role;
import com.example.online.banking.dto.ApproveApplicationRequest;
import com.example.online.banking.dto.VerifyApplicationRequest;
import com.example.online.banking.exception.ResourceNotFoundException;
import com.example.online.banking.model.AccountOpeningApplication;
import com.example.online.banking.model.Employee;
import com.example.online.banking.model.User;
import com.example.online.banking.repo.AccountOpeningApplicationRepository;
import com.example.online.banking.repo.EmployeeRepository;
import com.example.online.banking.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final AccountOpeningApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    // Document Verification Staff
    public List<AccountOpeningApplication> getSubmittedApplications() {

        return applicationRepository.findByApplicationStatus(
                ApplicationStatus.SUBMITTED
        );
    }

    // Bank Manager
    public List<AccountOpeningApplication> getVerifiedApplications() {

        return applicationRepository.findByApplicationStatus(
                ApplicationStatus.VERIFIED
        );
    }

    // Bank Manager - Approve / Reject
    @Transactional
    public AccountOpeningApplication approveApplication(
            Long applicationId,
            ApproveApplicationRequest request,
            String username) {

        User managerUser = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Manager user not found"
                        )
                );

        if (managerUser.getRole() != Role.BANK_MANAGER) {
            throw new IllegalStateException(
                    "Only Bank Manager can approve applications"
            );
        }

        Employee manager = employeeRepository
                .findByUserUserId(managerUser.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Manager employee profile not found"
                        )
                );

        AccountOpeningApplication application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found with id: "
                                                + applicationId
                                )
                        );

        if (application.getApplicationStatus() !=
                ApplicationStatus.VERIFIED) {

            throw new IllegalStateException(
                    "Only VERIFIED applications can be approved"
            );
        }

        if (request.approved()) {

            application.setApplicationStatus(
                    ApplicationStatus.APPROVED
            );

            application.setApprovedBy(manager);

            application.setApprovedAt(
                    LocalDateTime.now()
            );

        } else {

            application.setApplicationStatus(
                    ApplicationStatus.REJECTED
            );
        }

        application.setUpdatedAt(
                LocalDateTime.now()
        );

        return applicationRepository.save(application);
    }

    // Document Verification Staff
    @Transactional
    public AccountOpeningApplication verifyApplication(
            Long applicationId,
            VerifyApplicationRequest request,
            String username) {

        User staffUser = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Staff user not found"
                        )
                );

        if (staffUser.getRole() !=
                Role.DOCUMENT_VERIFICATION_STAFF) {

            throw new IllegalStateException(
                    "Only Document Verification Staff can verify applications"
            );
        }

        Employee employee = employeeRepository
                .findByUserUserId(staffUser.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee profile not found"
                        )
                );

        AccountOpeningApplication application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found with id: "
                                                + applicationId
                                )
                        );

        if (application.getApplicationStatus() !=
                ApplicationStatus.SUBMITTED) {

            throw new IllegalStateException(
                    "Only SUBMITTED applications can be verified"
            );
        }

        if (request.verified()) {

            application.setApplicationStatus(
                    ApplicationStatus.VERIFIED
            );

            application.setVerifiedBy(employee);

            application.setVerifiedAt(
                    LocalDateTime.now()
            );

        } else {

            application.setApplicationStatus(
                    ApplicationStatus.REJECTED
            );
        }

        application.setUpdatedAt(
                LocalDateTime.now()
        );

        return applicationRepository.save(application);
    }

    public List<AccountOpeningApplication> getAllApplications() {
      return   applicationRepository.findAll();
    }

}