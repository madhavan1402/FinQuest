package com.finquest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Registers BCryptPasswordEncoder as a Spring bean so it can be injected
    // anywhere via PasswordEncoder. BCrypt uses a cost factor (default=10) that
    // makes brute-force attacks computationally expensive.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF — not needed for stateless REST APIs
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Auth endpoints are public — no token required to register or login
                .requestMatchers("/api/auth/**").permitAll()
                // All other endpoints are open for now; restrict per-route as features are added
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
