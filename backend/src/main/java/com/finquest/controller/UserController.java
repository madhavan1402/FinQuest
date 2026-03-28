package com.finquest.controller;

import com.finquest.dto.AuthResponse;
import com.finquest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET /api/user/profile?userId=1
    // Returns the latest XP, level, and financialScore for the given user.
    // Called by the Dashboard on mount to replace stale localStorage data
    // with the current values from the database.
    @GetMapping("/profile")
    public ResponseEntity<AuthResponse> getProfile(@RequestParam Long userId) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }
}
