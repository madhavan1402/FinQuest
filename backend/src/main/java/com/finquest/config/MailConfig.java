package com.finquest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Builds a {@link JavaMailSender} from SMTP settings in {@code application.properties}.
 * <p>
 * In development (where no real SMTP server is available) the app documents the
 * intended behaviour and logs the would-be email (handled by {@code EmailService})
 * rather than failing on send.
 */
@Configuration
public class MailConfig {

    @Value("${app.mail.host}")
    private String host;

    @Value("${app.mail.port}")
    private int port;

    @Value("${app.mail.username}")
    private String username;

    @Value("${app.mail.password}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        String smtpHost = host == null ? "" : host;
        mailSender.setProtocol("smtp");
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth",
                (username != null && !username.isBlank()) ? "true" : "false");
        props.put("mail.smtp.starttls.enable",
                (username != null && !username.isBlank()) ? "true" : "false");
        props.putAll(getOrphanProperties(smtpHost));
        return mailSender;
    }

    // Keeps the build stable when no SMTP host is configured (dev mode).
    private Properties getOrphanProperties(String smtpHost) {
        Properties p = new Properties();
        p.put("mail.smtp.host", smtpHost.isBlank() ? "localhost" : smtpHost);
        return p;
    }
}
