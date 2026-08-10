package com.finquest.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
<<<<<<< HEAD
import java.time.LocalDate;
import java.time.LocalDateTime;
=======
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577

// @Entity  — marks this class as a JPA-managed database table
// @Table   — maps to the "users" table in MySQL
// @Data    — Lombok generates getters, setters, toString, equals, hashCode
// @NoArgsConstructor — Lombok generates the no-arg constructor JPA requires
//
// NOTE: @AllArgsConstructor is intentionally removed.
// When fields have inline defaults (xp = 0, level = 1, etc.), Lombok's
// @AllArgsConstructor generates a constructor that ignores those defaults,
// causing "no suitable constructor" errors when the field count changes.
// We use setter-based initialisation in services instead.
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    // Primary key — auto-incremented by MySQL
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    // Unique constraint prevents two accounts sharing the same email
    @Column(nullable = false, unique = true, length = 150)
    private String email;

<<<<<<< HEAD
// BCrypt-hashed password — plain text is NEVER stored
    @Column(nullable = false)
    private String password;

    // ── Authentication & authorization fields ─────────────────────────────────

    // Role-based access control: USER (default) or ADMIN
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;

    // Email-verification status. New accounts start unverified (false) and
    // must confirm via the emailed link before they can log in.
    @Column(nullable = false)
    private boolean emailVerified = false;

    // Random code embedded in the verification link. Null once verified.
    @Column(length = 64)
    private String verificationCode;

    // Expiry of the verification link (e.g. 24h from issue).
    private LocalDateTime verificationExpiry;

    // Random code used in "forgot password" reset links. Null when no reset pending.
    @Column(length = 64)
    private String resetCode;

    // Expiry of the password-reset link (e.g. 30 min from issue).
    private LocalDateTime resetExpiry;

=======
    // BCrypt-hashed password — plain text is NEVER stored
    @Column(nullable = false)
    private String password;

>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
    // ── Gamification fields ───────────────────────────────────────────────────

    // Total XP accumulated across quizzes and simulations
    @Column(nullable = false)
    private int xp = 0;

    // Current level — starts at 1, increases when XP threshold is crossed
    @Column(nullable = false)
    private int level = 1;

    // Composite financial health score (0–100)
    @Column(nullable = false)
    private int financialScore = 0;

<<<<<<< HEAD
    @Column(nullable = false)
    private int coins = 0;

    @Column(nullable = false)
    private int learningStreak = 0;

    private LocalDate lastLearningDate;

    // ── AI fields ─────────────────────────────────────────────────────────────

=======
    // ── AI fields ─────────────────────────────────────────────────────────────

    // Predicted by the literacy model: Beginner / Intermediate / Advanced
    @Column(length = 20)
    private String literacyLevel = "Beginner";

    // Latest recommendation from the AI recommendation model
    @Column(length = 300)
    private String recommendation = "Complete your first quiz to get started";

    // Predicted by the risk model: Conservative / Moderate / Aggressive
    // Required by AIService.predict() — was missing, causing setRiskProfile() error
    @Column(length = 20)
    private String riskProfile = "Moderate";
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
}
