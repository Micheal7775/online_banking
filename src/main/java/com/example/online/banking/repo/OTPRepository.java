package com.example.online.banking.repo;

import com.example.online.banking.model.OTP;
import com.example.online.banking.ENum.OtpStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OTPRepository extends JpaRepository<OTP, Long> {

    Optional<OTP> findTopByUserUserIdAndStatusOrderByCreatedAtDesc(
            Long userId,
            OtpStatus status
    );
}