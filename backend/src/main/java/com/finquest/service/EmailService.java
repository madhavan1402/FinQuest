package com.finquest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Sends transactional emails (email verification, password reset).
 * <p>
 * When no real SMTP username is configured (typical local dev), the email is
 * not sent over the network — instead the full body is logged so a developer
 * can copy the verification/reset link from the console and test the flow.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Sends an HTML-capable plain-text email.
     *
     * @param markdownDevMode if true, only logs the message (dev mode).
     */
    public void send(String to, String subject, String body, boolean markdownDevMode) {
        if (markdownDevMode) {
            log.info("\n=== [EMAIL NOT SENT — dev mode] ===\nTo: {}\nSubject: {}\n{}\n===================================",
                    to, subject, body);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception ex) {
            log.error("Failed to send email to {}: {}", to, ex.getMessage());
        }
    }
}
