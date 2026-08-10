package com.finquest.service;

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
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository  userRepository;
    private final AchievementRepository achievementRepository;
    private final UserStreakRepository userStreakRepository;
    private final PasswordEncoder passwordEncoder;

    // ── Seed test user (DB connection check) ──────────────────────────────────
    public String seedTestUser() {
        String testEmail = "test@finquest.com";
        if (userRepository.existsByEmail(testEmail)) {
            return "Test user already exists — DB connection is working!";
        }

        User user = new User();
        user.setName("FinQuest Tester");
        user.setEmail(testEmail);
        user.setPassword(passwordEncoder.encode("test1234"));
        user.setEmailVerified(true);   // seed user is fully verified
        user.setXp(0);
        user.setLevel(1);
        user.setFinancialScore(0);

        userRepository.save(user);
        return "Test user saved successfully — DB connection is working!";
    }

    // ── Get all users ─────────────────────────────────────────────────────────
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

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
    }
}
