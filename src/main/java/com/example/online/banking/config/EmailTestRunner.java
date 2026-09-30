package com.example.online.banking.config;

import com.example.online.banking.service.EmailTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailTestRunner implements CommandLineRunner {

    private final EmailTestService emailTestService;

    @Override
    public void run(String... args) {

        System.out.println("===== EMAIL TEST START =====");

        emailTestService.sendTestMail();

        System.out.println("===== EMAIL TEST END =====");
    }
}