package com.example.online.banking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;




        @Service
        @RequiredArgsConstructor
        public class EmailTestService {

            private final JavaMailSender mailSender;

            public void sendTestMail() {

                SimpleMailMessage message = new SimpleMailMessage();

                message.setFrom("cbank8018@gmail.com");
                message.setTo("michealantony345@gmail.com");
                message.setSubject("Online Banking Email Test");
                message.setText("Test email from Online Banking application.");

                mailSender.send(message);
            }
        }