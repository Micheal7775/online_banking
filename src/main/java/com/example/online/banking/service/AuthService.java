package com.example.online.banking.service;

import com.example.online.banking.dto.ChangePasswordRequest;
import com.example.online.banking.dto.LoginRequest;
import com.example.online.banking.dto.RegisterRequest;
import com.example.online.banking.exception.DuplicateResourceException;
import com.example.online.banking.exception.ResourceNotFoundException;
import com.example.online.banking.model.User;
import com.example.online.banking.repo.UserRepository;
import com.example.online.banking.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final LoginHistoryService loginHistoryService;

    private final OtpService otpService;


    public User register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException(
                    "Username already exists"
            );
        }

        User user = new User();

        user.setUsername(request.username());

        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        user.setRole(request.role());

        user.setStatus(
                com.example.online.banking.ENum.UserStatus.ACTIVE
        );

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }


    public Map<String, Object> login(
            LoginRequest request,
            String ipAddress,
            String deviceInfo) {

        log.info("====================================");
        log.info("LOGIN ATTEMPT");
        log.info("Username : {}", request.username());
        log.info("IP       : {}", ipAddress);
        log.info("Device   : {}", deviceInfo);

        try {

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.username(),
                                    request.password()
                            )
                    );

            User user =
                    userRepository
                            .findByUsername(authentication.getName())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "User not found"
                                    )
                            );

            String token =
                    jwtService.generateToken(
                            user.getUsername(),
                            user.getRole().name()
                    );

            user.setLastLogin(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            userRepository.save(user);

            loginHistoryService.recordLogin(
                    user,
                    ipAddress,
                    deviceInfo
            );

            log.info("LOGIN SUCCESS");
            log.info("Username : {}", user.getUsername());
            log.info("Role     : {}", user.getRole());
            log.info("====================================");

            return Map.of(
                    "message", "Login successful",
                    "username", user.getUsername(),
                    "role", user.getRole().name(),
                    "token", token
            );

        } catch (BadCredentialsException e) {

            log.warn(
                    "LOGIN FAILED - Username: {}, IP: {}",
                    request.username(),
                    ipAddress
            );

            throw e;
        }
    }


    @Transactional
    public void changePassword(
            String username,
            ChangePasswordRequest request) {

        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        if (!passwordEncoder.matches(
                request.currentPassword(),
                user.getPassword())) {

            throw new IllegalStateException(
                    "Current password is incorrect"
            );
        }

        if (passwordEncoder.matches(
                request.newPassword(),
                user.getPassword())) {

            throw new IllegalStateException(
                    "New password must be different from current password"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.newPassword()
                )
        );

        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        log.info(
                "Password changed successfully for user: {}",
                username
        );
    }
    @Transactional
    public void forgotPassword(String username) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        otpService.generateOtp(
                user,
                com.example.online.banking.ENum.OtpPurpose.PASSWORD_RESET
        );

        log.info(
                "Password reset OTP generated for user: {}",
                username
        );
    }


    @Transactional
    public void resetPassword(
            String username,
            String otpCode,
            String newPassword) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        boolean verified =
                otpService.verifyOtp(user, otpCode);

        if (!verified) {
            throw new IllegalStateException(
                    "Invalid or expired OTP"
            );
        }

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        log.info(
                "Password reset successfully for user: {}",
                username
        );
    }
}