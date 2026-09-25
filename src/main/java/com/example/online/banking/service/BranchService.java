package com.example.online.banking.service;

import com.example.online.banking.dto.CreateBranchRequest;
import com.example.online.banking.exception.DuplicateResourceException;
import com.example.online.banking.model.Branch;
import com.example.online.banking.repo.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;

    @Transactional
    public Branch createBranch(CreateBranchRequest request) {

        if (branchRepository.existsByBranchCode(
                request.branchCode())) {

            throw new DuplicateResourceException(
                    "Branch code already exists"
            );
        }

        if (branchRepository.existsByIfscCode(
                request.ifscCode())) {

            throw new DuplicateResourceException(
                    "IFSC code already exists"
            );
        }

        Branch branch = new Branch();

        branch.setBranchCode(request.branchCode());
        branch.setBranchName(request.branchName());
        branch.setIfscCode(request.ifscCode());
        branch.setAddress(request.address());
        branch.setCity(request.city());
        branch.setState(request.state());
        branch.setPincode(request.pincode());
        branch.setPhoneNumber(request.phoneNumber());
        branch.setEmail(request.email());
        branch.setStatus(request.status());
        branch.setOpenedAt(request.openedAt());

        branch.setCreatedAt(LocalDateTime.now());
        branch.setUpdatedAt(LocalDateTime.now());

        return branchRepository.save(branch);
    }
}