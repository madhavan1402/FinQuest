package com.finquest.controller;

import com.finquest.dto.AuthRequest;
import com.finquest.dto.AuthResponse;
import com.finquest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // POST /api/auth/register
    // Accepts: { "name": "...", "email": "...", "password": "..." }
    // Returns: 201 Created + AuthResponse (no password in response)
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        AuthResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // POST /api/auth/login
    // Accepts: { "email": "...", "password": "..." }
    // Returns: 200 OK + AuthResponse on success, 401 on bad credentials
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            AuthResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Return 401 with a generic message — don't expose whether email exists
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
