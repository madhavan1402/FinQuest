package com.finquest.service;

import com.finquest.dto.AuthResponse;
import com.finquest.dto.LoginRequest;
import com.finquest.dto.RegisterRequest;
import com.finquest.exception.BadRequestException;
import com.finquest.exception.ResourceNotFoundException;
import com.finquest.exception.UnauthorizedException;
import com.finquest.model.RefreshToken;
import com.finquest.model.Role;
import com.finquest.model.User;
import com.finquest.repository.RefreshTokenRepository;
import com.finquest.repository.UserRepository;
import com.finquest.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;

/**
 * Central authentication service: registration, login, refresh-token rotation,
 * secure logout, email verification, and password reset.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    // 24h verification window.
    private static final long VERIFICATION_HOURS = 24;

    // 30-minute reset window.
    private static final long RESET_MINUTES = 30;

    // Lifetime of a persisted refresh token (e.g. 7 days).
    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshMs;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;

    // ── Register ──────────────────────────────────────────────────────────────
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);              // all sign-ups are regular users
        user.setEmailVerified(false);

        // Gamification defaults (mirror existing behaviour).
        user.setXp(0);
        user.setLevel(1);
        user.setFinancialScore(0);
        user.setCoins(0);

        // Issue a verification code.
        String code = generateRandomCode(32);
        user.setVerificationCode(code);
        user.setVerificationExpiry(LocalDateTime.now().plusHours(VERIFICATION_HOURS));

        userRepository.save(user);

        // Send a verification email (logs the link in dev mode).
        String frontendBase = "http://localhost:5173";
        emailService.send(
            user.getEmail(),
            "Verify your FinQuest account",
            "Hi " + user.getName() + ",\n\n"
                + "Click the link below to verify your email:\n"
                + frontendBase + "/verify?id=" + user.getId() + "&code=" + code + "\n\n"
                + "This link expires in " + VERIFICATION_HOURS + " hours.\n\n"
                + "— FinQuest Team",
            devMode());

        // Do not auto-login — the user must verify first.
        return toResponse("Registration successful. Please verify your email before logging in.",
                user, null, null);
    }

    // ── Login ────────────────────────────────────────────────────────────────
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().toLowerCase().trim();

        // AuthenticationManager validates credentials + loads UserPrincipal.
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword()));
        } catch (Exception ex) {
            throw new UnauthorizedException("Invalid email or password");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!user.isEmailVerified()) {
            throw new BadRequestException("Please verify your email before logging in");
        }

        // Issue a fresh access + refresh token pair.
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = createRefreshToken(user);

        return toResponse("Login successful", user, accessToken, refreshToken);
    }

    // ── Refresh token (rotation) ─────────────────────────────────────────────
    @Transactional
    public AuthResponse refresh(String refreshTokenRaw) throws BadRequestException {
        // 1. Validate the raw token is a well-formed JWT with a real user.
        if (refreshTokenRaw == null || refreshTokenRaw.isBlank()) {
            throw new BadRequestException("Refresh token is required");
        }
        Long userId;
        try {
            userId = jwtService.extractUserId(refreshTokenRaw);
        } catch (Exception ex) {
            throw new BadRequestException("Invalid refresh token");
        }

        // 2. Compare by SHA-256 hash — tokens are stored hashed.
        String hash = sha256(refreshTokenRaw);
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (stored.isRevoked() || stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token has expired or been revoked");
        }

        // 3. Rotate — revoke the old one, persist a fresh one, issue a new pair.
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        User user = stored.getUser();
        if (user == null) {
            throw new BadRequestException("Invalid refresh token");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = createRefreshToken(user);
        return toResponse("Token refreshed", user, accessToken, newRefreshToken);
    }

    // ── Secure logout ────────────────────────────────────────────────────────
    @Transactional
    public void logout(String refreshTokenRaw) {
        if (refreshTokenRaw == null || refreshTokenRaw.isBlank()) {
            return; // nothing to revoke
        }
        refreshTokenRepository.findByTokenHash(sha256(refreshTokenRaw))
            .ifPresent(t -> {
                t.setRevoked(true);
                refreshTokenRepository.save(t);
            });
        // Alternatively, a "logout all devices" could revoke every token:
        // (kept simple here — logout revokes the presented token).
    }

    // ── Email verification ───────────────────────────────────────────────────
    @Transactional
    public void verifyEmail(Long userId, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (user.isEmailVerified()) {
            throw new BadRequestException("Email is already verified");
        }

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(code)) {
            throw new BadRequestException("Invalid verification code");
        }

        if (user.getVerificationExpiry() == null
                || user.getVerificationExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Verification link has expired");
        }

        user.setEmailVerified(true);
        user.setVerificationCode(null);
        user.setVerificationExpiry(null);
        userRepository.save(user);
    }

    // ── Forgot password ──────────────────────────────────────────────────────
    @Transactional
    public void forgotPassword(String email) {
        String normalized = email == null ? "" : email.toLowerCase().trim();
        // Always return success to avoid leaking which emails exist.
        userRepository.findByEmail(normalized).ifPresent(user -> {
            String code = generateRandomCode(32);
            user.setResetCode(code);
            user.setResetExpiry(LocalDateTime.now().plusMinutes(RESET_MINUTES));
            userRepository.save(user);

            String frontendBase = "http://localhost:5173";
            emailService.send(
                user.getEmail(),
                "Reset your FinQuest password",
                "Hi " + user.getName() + ",\n\n"
                    + "Click the link below to reset your password:\n"
                    + frontendBase + "/reset-password?code=" + code + "\n\n"
                    + "This link expires in " + RESET_MINUTES + " minutes.\n\n"
                    + "If you did not request this, please ignore this email.\n\n"
                    + "— FinQuest Team",
                devMode());
        });
    }

    // ── Reset password ───────────────────────────────────────────────────────
    @Transactional
    public void resetPassword(String code, String newPassword) {
        User user = userRepository.findByResetCode(code)
                .orElseThrow(() -> new BadRequestException("Invalid reset code"));

        if (user.getResetExpiry() == null || user.getResetExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Reset link has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetCode(null);
        user.setResetExpiry(null);
        userRepository.save(user);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private boolean devMode() {
        return true; // production would read a flag; logging link in dev is desired
    }

    /** Persists a hashed refresh token for the user and returns the raw JWT. */
    private String createRefreshToken(User user) {
        String raw = jwtService.generateRefreshToken(user);
        RefreshToken token = new RefreshToken();
        token.setTokenHash(sha256(raw));
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plus(refreshMs, ChronoUnit.MILLIS));
        token.setCreatedAt(LocalDateTime.now());
        token.setRevoked(false);
        refreshTokenRepository.save(token);
        return raw;
    }

    private AuthResponse toResponse(String message, User user, String accessToken, String refreshToken) {
        return new AuthResponse(
                message,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLevel(),
                user.getXp(),
                user.getFinancialScore(),
                user.getRole().name(),
                user.isEmailVerified(),
                accessToken,
                refreshToken
        );
    }

    private String generateRandomCode(int lengthBytes) {
        byte[] bytes = new byte[lengthBytes];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash token", e);
        }
    }
}
