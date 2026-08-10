package com.finquest.controller;

import com.finquest.dto.AuthResponse;
import com.finquest.dto.ForgotPasswordRequest;
import com.finquest.dto.LoginRequest;
import com.finquest.dto.MessageResponse;
import com.finquest.dto.RefreshTokenRequest;
import com.finquest.dto.RegisterRequest;
import com.finquest.dto.ResetPasswordRequest;
import com.finquest.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // POST /api/auth/refresh — rotate an existing refresh token
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
    }

    // POST /api/auth/logout — revoke the presented refresh token
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@RequestBody(required = false) RefreshTokenRequest request) {
        authService.logout(request != null ? request.getRefreshToken() : null);
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }

    // GET /api/auth/verify-email?id=1&code=abc
    @GetMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(@RequestParam Long id, @RequestParam String code) {
        authService.verifyEmail(id, code);
        return ResponseEntity.ok(new MessageResponse("Email verified. You can now log in."));
    }

    // POST /api/auth/forgot-password
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        // Always 200 so we don't leak whether the email exists.
        return ResponseEntity.ok(new MessageResponse("If that email exists, a reset link has been sent."));
    }

    // POST /api/auth/reset-password
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getCode(), request.getNewPassword());
        return ResponseEntity.ok(new MessageResponse("Password reset successfully. You can now log in."));
    }
}
