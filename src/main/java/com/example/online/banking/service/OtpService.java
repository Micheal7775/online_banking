package com.example.online.banking.service;

import com.example.online.banking.ENum.OtpPurpose;
import com.example.online.banking.ENum.OtpStatus;
import com.example.online.banking.exception.ResourceNotFoundException;
import com.example.online.banking.model.Customer;
import com.example.online.banking.model.OTP;
import com.example.online.banking.model.User;
import com.example.online.banking.repo.CustomerRepository;
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

    private final EmailService emailService;

    private final CustomerRepository customerRepository;


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

        // Expire previous active OTP
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


        // =========================================
        // CREATE OTP
        // =========================================

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


        // =========================================
        // SAVE OTP
        // =========================================

        OTP savedOtp =
                otpRepository.save(otp);


        log.info(
                "OTP generated for User: {} | Purpose: {}",
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

        if (user == null) {

            throw new IllegalArgumentException(
                    "User is required"
            );
        }


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


        // =========================================
        // NULL / EMPTY OTP CHECK
        // =========================================

        String enteredOtp =
                otpCode == null
                        ? ""
                        : otpCode.trim();


        // =========================================
        // EXPIRY CHECK
        // =========================================

        if (otp.getExpiresAt() == null ||
                LocalDateTime.now()
                        .isAfter(otp.getExpiresAt())) {

            otp.setStatus(
                    OtpStatus.EXPIRED
            );

            otpRepository.save(otp);

            log.warn(
                    "OTP expired for user: {}",
                    user.getUsername()
            );

            return false;
        }


        // =========================================
        // MAXIMUM ATTEMPTS CHECK
        // =========================================

        if (otp.getAttempts() >= 3) {

            otp.setStatus(
                    OtpStatus.EXPIRED
            );

            otpRepository.save(otp);

            log.warn(
                    "OTP maximum attempts reached for user: {}",
                    user.getUsername()
            );

            return false;
        }


        // =========================================
        // INCREMENT ATTEMPT
        // =========================================

        otp.setAttempts(
                otp.getAttempts() + 1
        );


        // =========================================
        // OTP MATCH CHECK
        // =========================================

        if (!otp.getOtpCode()
                .equals(enteredOtp)) {

            otpRepository.save(otp);

            log.warn(
                    "Invalid OTP entered for user: {}",
                    user.getUsername()
            );

            return false;
        }


        // =========================================
        // OTP SUCCESS
        // =========================================

        otp.setStatus(
                OtpStatus.USED
        );

        otpRepository.save(otp);

        log.info(
                "OTP verified successfully for user: {}",
                user.getUsername()
        );

        return true;
    }


    // =========================================
    // GENERATE TRANSFER OTP
    // =========================================

    @Transactional
    public OTP generateTransferOtp(
            Customer customer,
            String toAccountNumber,
            BigDecimal amount) {

        // =========================================
        // CUSTOMER CHECK
        // =========================================

        if (customer == null) {

            throw new IllegalArgumentException(
                    "Customer is required"
            );
        }


        // =========================================
        // USER CHECK
        // =========================================

        if (customer.getUser() == null) {

            throw new IllegalStateException(
                    "Customer user is not available"
            );
        }


        // =========================================
        // RECEIVER ACCOUNT CHECK
        // =========================================

        if (toAccountNumber == null ||
                toAccountNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Receiver account number is required"
            );
        }


        // =========================================
        // AMOUNT CHECK
        // =========================================

        if (amount == null ||
                amount.compareTo(
                        BigDecimal.ZERO
                ) <= 0) {

            throw new IllegalArgumentException(
                    "Invalid transfer amount"
            );
        }


        // =========================================
        // EMAIL CHECK
        // =========================================

        if (customer.getEmail() == null ||
                customer.getEmail().isBlank()) {

            throw new IllegalStateException(
                    "Customer email address is not available"
            );
        }


        // =========================================
        // GENERATE OTP
        // =========================================

        OTP otp = generateOtp(
                customer.getUser(),
                OtpPurpose.MONEY_TRANSFER
        );


        // =========================================
        // SAVE TRANSFER DETAILS
        // =========================================

        otp.setTransactionAmount(
                amount
        );

        otp.setBeneficiaryAccountNumber(
                toAccountNumber.trim()
        );


        OTP savedOtp =
                otpRepository.save(otp);


        // =========================================
        // SEND OTP TO CUSTOMER EMAIL
        // =========================================

        emailService.sendOtpEmail(
                customer.getEmail(),
                otp.getOtpCode()
        );


        log.info(
                "Transfer OTP email sent to customer: {}",
                customer.getCustomerNumber()
        );


        return savedOtp;
    }


    // =========================================
    // PASSWORD RESET OTP
    // =========================================
// =========================================
// PASSWORD RESET OTP
// =========================================

    @Transactional
    public OTP generatePasswordResetOtp(String username) {

        // Find user
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        // Find customer linked with this user
        Customer customer = customerRepository
                .findByUserUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer details not found"
                        )
                );


        if (customer == null) {
            throw new ResourceNotFoundException(
                    "Customer details not found for this user"
            );
        }

        // Check customer email
        if (customer.getEmail() == null ||
                customer.getEmail().isBlank()) {

            throw new IllegalStateException(
                    "Customer email address is not available"
            );
        }

        // Generate OTP
        OTP otp = generateOtp(
                user,
                OtpPurpose.PASSWORD_RESET
        );

        // Send OTP to customer email
        emailService.sendOtpEmail(
                customer.getEmail(),
                otp.getOtpCode()
        );

        log.info(
                "Password reset OTP email sent to customer: {}",
                customer.getCustomerNumber()
        );

        return otp;
    }
}