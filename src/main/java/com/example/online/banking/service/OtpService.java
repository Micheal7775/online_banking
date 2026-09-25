package com.example.online.banking.service;

import com.example.online.banking.ENum.OtpPurpose;
import com.example.online.banking.ENum.OtpStatus;
import com.example.online.banking.exception.ResourceNotFoundException;
import com.example.online.banking.model.OTP;
import com.example.online.banking.model.User;
import com.example.online.banking.repo.OTPRepository;
import com.example.online.banking.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private final UserRepository userRepository;

    private final OTPRepository otpRepository;

    private final SecureRandom secureRandom =
            new SecureRandom();


    // =========================================
    // GENERATE NORMAL OTP
    // =========================================

    @Transactional
    public OTP generateOtp(
            User user,
            OtpPurpose purpose) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "User is required"
            );
        }

        // Expire previous active OTPs
        otpRepository
                .findTopByUserUserIdAndStatusOrderByCreatedAtDesc(
                        user.getUserId(),
                        OtpStatus.ACTIVE
                )
                .ifPresent(oldOtp -> {

                    oldOtp.setStatus(
                            OtpStatus.EXPIRED
                    );

                    otpRepository.save(oldOtp);
                });


        OTP otp = new OTP();

        otp.setUser(user);

        String otpCode = String.format(
                "%06d",
                secureRandom.nextInt(1_000_000)
        );

        otp.setOtpCode(otpCode);

        otp.setPurpose(purpose);

        otp.setExpiresAt(
                LocalDateTime.now()
                        .plusMinutes(5)
        );

        otp.setStatus(
                OtpStatus.ACTIVE
        );

        otp.setAttempts(0);

        otp.setCreatedAt(
                LocalDateTime.now()
        );


        OTP savedOtp =
                otpRepository.save(otp);


        // Development testing only
        log.info(
                "OTP generated: {} | User: {} | Purpose: {}",
                otpCode,
                user.getUsername(),
                purpose
        );


        return savedOtp;
    }


    // =========================================
    // VERIFY OTP
    // =========================================

    @Transactional
    public boolean verifyOtp(
            User user,
            String otpCode) {

        OTP otp = otpRepository
                .findTopByUserUserIdAndStatusOrderByCreatedAtDesc(
                        user.getUserId(),
                        OtpStatus.ACTIVE
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active OTP not found"
                        )
                );

        log.info(
                "OTP DEBUG -> DB OTP: [{}], Entered OTP: [{}], Status: {}, Purpose: {}, Expires: {}, Attempts: {}",
                otp.getOtpCode(),
                otpCode,
                otp.getStatus(),
                otp.getPurpose(),
                otp.getExpiresAt(),
                otp.getAttempts()
        );

        if (LocalDateTime.now()
                .isAfter(otp.getExpiresAt())) {

            otp.setStatus(OtpStatus.EXPIRED);
            otpRepository.save(otp);

            log.warn("OTP expired");

            return false;
        }

        if (otp.getAttempts() >= 3) {

            otp.setStatus(OtpStatus.EXPIRED);
            otpRepository.save(otp);

            log.warn("OTP maximum attempts reached");

            return false;
        }

        otp.setAttempts(
                otp.getAttempts() + 1
        );

        String enteredOtp =
                otpCode == null
                        ? ""
                        : otpCode.trim();

        if (!otp.getOtpCode().equals(enteredOtp)) {

            otpRepository.save(otp);

            log.warn(
                    "OTP mismatch -> DB: [{}], Entered: [{}]",
                    otp.getOtpCode(),
                    enteredOtp
            );

            return false;
        }

        otp.setStatus(OtpStatus.USED);

        otpRepository.save(otp);

        log.info("OTP VERIFIED SUCCESSFULLY");

        return true;
    }
    // =========================================
    // GENERATE TRANSFER OTP
    // =========================================

    @Transactional
    public OTP generateTransferOtp(
            User user,
            String toAccountNumber,
            BigDecimal amount) {

        if (toAccountNumber == null ||
                toAccountNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Receiver account number is required"
            );
        }


        if (amount == null ||
                amount.compareTo(
                        BigDecimal.ZERO
                ) <= 0) {

            throw new IllegalArgumentException(
                    "Invalid transfer amount"
            );
        }


        OTP otp = generateOtp(
                user,
                OtpPurpose.MONEY_TRANSFER
        );


        otp.setTransactionAmount(
                amount
        );

        otp.setBeneficiaryAccountNumber(
                toAccountNumber.trim()
        );


        return otpRepository.save(otp);
    }


    // =========================================
    // PASSWORD RESET OTP
    // =========================================

    @Transactional
    public OTP generatePasswordResetOtp(
            String username) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );


        return generateOtp(
                user,
                OtpPurpose.PASSWORD_RESET
        );
    }
}