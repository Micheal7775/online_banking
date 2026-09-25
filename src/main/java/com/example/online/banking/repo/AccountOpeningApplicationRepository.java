package com.example.online.banking.repo;

import com.example.online.banking.ENum.ApplicationStatus;
import com.example.online.banking.model.AccountOpeningApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountOpeningApplicationRepository
        extends JpaRepository<AccountOpeningApplication, Long> {

    List<AccountOpeningApplication> findByApplicationStatus(
            ApplicationStatus applicationStatus
    );
}