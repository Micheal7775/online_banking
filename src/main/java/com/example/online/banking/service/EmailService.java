package com.example.online.banking.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(
            String email,
            String otp
    ) {

        try {

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("cbank8018@gmail.com");
            helper.setTo(email);

            helper.setSubject(
                    "C-Bank | Secure Verification Code"
            );

            String htmlContent = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport"
                              content="width=device-width, initial-scale=1.0">
                    </head>

                    <body style="
                        margin:0;
                        padding:0;
                        background-color:#f4f7fb;
                        font-family:Arial, Helvetica, sans-serif;
                    ">

                        <div style="
                            max-width:600px;
                            margin:40px auto;
                            padding:0 15px;
                        ">

                            <!-- Main Card -->
                            <div style="
                                background:#ffffff;
                                border-radius:14px;
                                overflow:hidden;
                                box-shadow:0 4px 18px rgba(0,0,0,0.08);
                                border:1px solid #e5e7eb;
                            ">

                                <!-- Header -->
                                <div style="
                                    background:#0f172a;
                                    padding:28px 30px;
                                    text-align:center;
                                ">

                                    <div style="
                                        display:inline-block;
                                        width:48px;
                                        height:48px;
                                        line-height:48px;
                                        background:#2563eb;
                                        color:#ffffff;
                                        border-radius:12px;
                                        font-size:22px;
                                        font-weight:bold;
                                    ">
                                        C
                                    </div>

                                    <h1 style="
                                        margin:12px 0 0;
                                        color:#ffffff;
                                        font-size:24px;
                                        letter-spacing:1px;
                                    ">
                                        C-Bank
                                    </h1>

                                    <p style="
                                        margin:6px 0 0;
                                        color:#cbd5e1;
                                        font-size:13px;
                                    ">
                                        Secure Online Banking
                                    </p>

                                </div>

                                <!-- Content -->
                                <div style="
                                    padding:35px 30px;
                                    text-align:center;
                                ">

                                    <h2 style="
                                        margin:0 0 12px;
                                        color:#111827;
                                        font-size:22px;
                                    ">
                                        Verification Code
                                    </h2>

                                    <p style="
                                        margin:0 auto 25px;
                                        color:#64748b;
                                        font-size:15px;
                                        line-height:1.6;
                                    ">
                                        We received a request to verify
                                        your online banking activity.
                                        Use the OTP below to continue.
                                    </p>

                                    <!-- OTP Box -->
                                    <div style="
                                        margin:25px auto;
                                        padding:20px;
                                        background:#f8fafc;
                                        border:2px dashed #2563eb;
                                        border-radius:12px;
                                        max-width:260px;
                                    ">

                                        <div style="
                                            color:#64748b;
                                            font-size:12px;
                                            text-transform:uppercase;
                                            letter-spacing:2px;
                                            margin-bottom:8px;
                                        ">
                                            One-Time Password
                                        </div>

                                        <div style="
                                            color:#0f172a;
                                            font-size:34px;
                                            font-weight:bold;
                                            letter-spacing:8px;
                                        ">
                                            %s
                                        </div>

                                    </div>

                                    <p style="
                                        color:#64748b;
                                        font-size:13px;
                                        line-height:1.6;
                                        margin:25px 0 0;
                                    ">
                                        This verification code is valid for
                                        <strong>5 minutes</strong>.
                                    </p>

                                    <!-- Security Notice -->
                                    <div style="
                                        margin-top:25px;
                                        padding:15px;
                                        background:#fff7ed;
                                        border-left:4px solid #f97316;
                                        border-radius:6px;
                                        text-align:left;
                                    ">

                                        <strong style="
                                            color:#9a3412;
                                            font-size:13px;
                                        ">
                                            Security Notice
                                        </strong>

                                        <p style="
                                            margin:6px 0 0;
                                            color:#7c2d12;
                                            font-size:12px;
                                            line-height:1.5;
                                        ">
                                            Never share this OTP with anyone.
                                            C-Bank will never ask you to share
                                            your OTP, password or PIN.
                                        </p>

                                    </div>

                                </div>

                                <!-- Footer -->
                                <div style="
                                    background:#f8fafc;
                                    padding:20px 30px;
                                    text-align:center;
                                    border-top:1px solid #e5e7eb;
                                ">

                                    <p style="
                                        margin:0;
                                        color:#64748b;
                                        font-size:12px;
                                    ">
                                        If you did not request this code,
                                        please ignore this email.
                                    </p>

                                    <p style="
                                        margin:10px 0 0;
                                        color:#94a3b8;
                                        font-size:11px;
                                    ">
                                        © 2026 C-Bank. All rights reserved.
                                    </p>

                                </div>

                            </div>

                        </div>

                    </body>
                    </html>
                    """.formatted(otp);

            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {

            throw new RuntimeException(
                    "Unable to send OTP email",
                    e
            );
        }
    }
}