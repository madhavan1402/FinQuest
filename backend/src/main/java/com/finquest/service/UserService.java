package com.finquest.service;

import com.finquest.dto.AuthRequest;
import com.finquest.dto.AuthResponse;
import com.finquest.model.User;
import com.finquest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository  userRepository;

    // BCryptPasswordEncoder bean declared in SecurityConfig — injected here.
    // BCrypt salts every hash automatically, so identical passwords produce
    // different hashes — safe against rainbow table attacks.
    private final PasswordEncoder passwordEncoder;

    // ── Register ──────────────────────────────────────────────────────────────
    public AuthResponse register(AuthRequest request) {

        // 1. Reject duplicate emails before hitting the DB unique constraint
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        // 2. Hash the plain-text password — NEVER store raw passwords
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // 3. Build the user with setter-based initialisation.
        //    We use new User() + setters instead of a constructor because
        //    User fields have inline defaults (xp=0, level=1, etc.) that
        //    @AllArgsConstructor would silently ignore.
        User user = new User();

        // FIX: was user.setName(name) — bare variable 'name' doesn't exist here.
        // Correct source is the AuthRequest object passed into this method.
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(hashedPassword);   // store the hashed version, never plain text

        // Gamification defaults — explicit for clarity
        user.setXp(0);
        user.setLevel(1);
        user.setFinancialScore(0);

        // AI defaults — riskProfile field now exists on User after the fix
        user.setLiteracyLevel("Beginner");
        user.setRiskProfile("Moderate");    // FIX: field was missing from User entity

        // 4. Persist — Hibernate issues INSERT INTO users (...)
        User saved = userRepository.save(user);

        return toResponse("Registered successfully", saved);
    }

    // ── Login ─────────────────────────────────────────────────────────────────
    public AuthResponse login(AuthRequest request) {

        // Look up by email — generic error message avoids leaking whether
        // the email exists (security best practice)
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // BCrypt.matches() re-hashes the plain-text input and compares —
        // it never decrypts the stored hash
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return toResponse("Login successful", user);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    // Converts a User entity to an AuthResponse DTO — password is never included
    private AuthResponse toResponse(String message, User user) {
        return new AuthResponse(
                message,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLevel(),
                user.getXp(),
                user.getFinancialScore()
        );
    }

    // ── Seed test user (DB connection check) ──────────────────────────────────
    // FIX: was using the old all-args constructor new User(null, "...", ..., 0, 1, 0)
    // which no longer exists after removing @AllArgsConstructor from User.
    // Replaced with setter-based initialisation to match the register() pattern.
    public String seedTestUser() {
        String testEmail = "test@finquest.com";
        if (userRepository.existsByEmail(testEmail)) {
            return "Test user already exists — DB connection is working!";
        }

        User user = new User();
        user.setName("FinQuest Tester");
        user.setEmail(testEmail);
        user.setPassword(passwordEncoder.encode("test1234"));
        user.setXp(0);
        user.setLevel(1);
        user.setFinancialScore(0);
        user.setLiteracyLevel("Beginner");
        user.setRiskProfile("Moderate");

        userRepository.save(user);
        return "Test user saved successfully — DB connection is working!";
    }

    // ── Get all users ─────────────────────────────────────────────────────────
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ── Get profile by ID — used by GET /api/user/profile ────────────────────
    public AuthResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        return toResponse("OK", user);
    }
}
