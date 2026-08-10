package com.finquest.controller;

import com.finquest.dto.AuthResponse;
<<<<<<< HEAD
import com.finquest.dto.LeaderboardEntryDto;
=======
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
import com.finquest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

<<<<<<< HEAD
import java.util.List;

=======
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
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
<<<<<<< HEAD

    // GET /api/user/leaderboard
    // Returns all users ranked by XP (descending) as safe LeaderboardEntryDto
    // objects — no password, no email. Used by the Leaderboard page.
    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardEntryDto>> getLeaderboard() {
        return ResponseEntity.ok(userService.getLeaderboard());
    }
=======
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
}
