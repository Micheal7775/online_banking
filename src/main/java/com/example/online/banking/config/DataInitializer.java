package com.example.online.banking.config;

import com.example.online.banking.ENum.Role;
import com.example.online.banking.ENum.UserStatus;
import com.example.online.banking.model.User;
import com.example.online.banking.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (userRepository.existsByUsername("admin")) {

            log.info("Admin user already exists");

            return;
        }

        User admin = new User();

        admin.setUsername("admin");

        admin.setPassword(
                passwordEncoder.encode("Admin@12345")
        );

        admin.setRole(Role.ADMIN);

        admin.setStatus(UserStatus.ACTIVE);

        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());

        userRepository.save(admin);

        log.info("Initial admin user created successfully");
    }
}