package com.example.online.banking.model;

import com.example.online.banking.ENum.BranchStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "branches",
        indexes = {
                @Index(name = "idx_branch_code", columnList = "branch_code"),
                @Index(name = "idx_ifsc_code", columnList = "ifsc_code"),
                @Index(name = "idx_branch_city", columnList = "city"),
                @Index(name = "idx_branch_state", columnList = "state")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long branchId;

    @Column(nullable = false, unique = true)
    private String branchCode;

    @Column(nullable = false)
    private String branchName;

    @Column(nullable = false, unique = true)
    private String ifscCode;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String pincode;

    private String phoneNumber;

    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BranchStatus status;

    private LocalDate openedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}