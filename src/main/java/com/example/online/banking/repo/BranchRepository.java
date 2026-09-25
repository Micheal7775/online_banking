package com.example.online.banking.repo;

import com.example.online.banking.model.Branch;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository
        extends JpaRepository<Branch, Long> {
    boolean existsByIfscCode(@NotBlank(message = "IFSC code is required") String s);

    boolean existsByBranchCode(@NotBlank(message = "Branch code is required") String s);
}