package com.finquest.controller;

import com.finquest.dto.LeaderboardEntryDto;
import com.finquest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// @RestController = @Controller + @ResponseBody
// Every method return value is written directly to the HTTP response body as JSON/text
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestController {

    private final UserService userService;

    // Basic health check — no DB involved
    @GetMapping("/test")
    public String test() {
        return "Backend Working";
    }

    // Inserts a test user into MySQL and confirms the connection works
    @GetMapping("/users/test-save")
    public String testSave() {
        return userService.seedTestUser();
    }

    // Reads all users from MySQL — confirms both write and read paths.
    // Returns safe LeaderboardEntryDto objects — never raw User entities,
    // which would leak the BCrypt password hash to any client.
    @GetMapping("/users")
    public List<LeaderboardEntryDto> getAllUsers() {
        return userService.getLeaderboard();
    }
}
