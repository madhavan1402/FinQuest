package com.finquest.service;

<<<<<<< HEAD
import com.finquest.dto.AuthResponse;
import com.finquest.dto.LeaderboardEntryDto;
import com.finquest.dto.ProfileDto;
import com.finquest.exception.BadRequestException;
import com.finquest.exception.ResourceNotFoundException;
import com.finquest.exception.UnauthorizedException;
import com.finquest.model.Achievement;
import com.finquest.model.User;
import com.finquest.model.UserStreak;
import com.finquest.repository.AchievementRepository;
import com.finquest.repository.UserRepository;
import com.finquest.repository.UserStreakRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
=======
import com.finquest.dto.AuthRequest;
import com.finquest.dto.AuthResponse;
import com.finquest.model.User;
import com.finquest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository  userRepository;
<<<<<<< HEAD
    private final AchievementRepository achievementRepository;
    private final UserStreakRepository userStreakRepository;
    private final PasswordEncoder passwordEncoder;

    // ── Seed test user (DB connection check) ──────────────────────────────────
=======

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
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
    public String seedTestUser() {
        String testEmail = "test@finquest.com";
        if (userRepository.existsByEmail(testEmail)) {
            return "Test user already exists — DB connection is working!";
        }

        User user = new User();
        user.setName("FinQuest Tester");
        user.setEmail(testEmail);
        user.setPassword(passwordEncoder.encode("test1234"));
<<<<<<< HEAD
        user.setEmailVerified(true);   // seed user is fully verified
        user.setXp(0);
        user.setLevel(1);
        user.setFinancialScore(0);
=======
        user.setXp(0);
        user.setLevel(1);
        user.setFinancialScore(0);
        user.setLiteracyLevel("Beginner");
        user.setRiskProfile("Moderate");
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577

        userRepository.save(user);
        return "Test user saved successfully — DB connection is working!";
    }

    // ── Get all users ─────────────────────────────────────────────────────────
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

<<<<<<< HEAD
    // ── Leaderboard ───────────────────────────────────────────────────────────
    // Ranked: XP desc → level desc → userId asc (earlier account = tiebreaker)
    public List<LeaderboardEntryDto> getLeaderboard() {
        List<User> allUsers = userRepository.findAll().stream()
                .sorted(Comparator.comparingInt(User::getXp).reversed()
                        .thenComparing(Comparator.comparingInt(User::getLevel).reversed())
                        .thenComparing(Comparator.comparingLong(User::getId)))
                .toList();

        java.util.List<LeaderboardEntryDto> result = new java.util.ArrayList<>();
        int rank = 1;
        for (User u : allUsers) {
            List<String> badges = achievementRepository.findByUserId(u.getId()).stream()
                    .map(Achievement::getBadgeName).toList();
            int streak = userStreakRepository.findByUserId(u.getId())
                    .map(UserStreak::getCurrentStreak).orElse(0);
            result.add(new LeaderboardEntryDto(
                    u.getId(), u.getName(), "",
                    u.getLevel(), u.getXp(), u.getFinancialScore(),
                    rank++, badges, streak));
        }
        return result;
    }

    // ── Get profile by ID — used by GET /api/user/profile (legacy) ───────────
    // Kept for backward compatibility with the existing frontend which calls
    // ?userId= to fetch the latest XP/level/score.
    public AuthResponse getProfile(Long userId) {
        User user = findUser(userId);
        return new AuthResponse(
                "OK",
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLevel(),
                user.getXp(),
                user.getFinancialScore()
        );
    }

    // ── Current-user profile API ─────────────────────────────────────────────
    public ProfileDto getCurrentUserProfile(Long userId) {
        return ProfileDto.from(findUser(userId));
    }

    @Transactional
    public ProfileDto updateProfile(Long userId, ProfileDto request) {
        User user = findUser(userId);
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        userRepository.save(user);
        return ProfileDto.from(user);
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = findUser(userId);
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new UnauthorizedException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
=======
    // ── Get profile by ID — used by GET /api/user/profile ────────────────────
    public AuthResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        return toResponse("OK", user);
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
    }
}
