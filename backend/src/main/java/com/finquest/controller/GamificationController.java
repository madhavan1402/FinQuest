package com.finquest.controller;

import com.finquest.dto.*;
import com.finquest.model.UserStreak;
import com.finquest.repository.UserAchievementRepository;
import com.finquest.repository.UserBadgeRepository;
import com.finquest.repository.UserStreakRepository;
import com.finquest.service.GamificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gamification")
@RequiredArgsConstructor
public class GamificationController {

    private final GamificationService        gamificationService;
    private final UserAchievementRepository  userAchievementRepo;
    private final UserBadgeRepository        userBadgeRepo;
    private final UserStreakRepository       streakRepo;

    @GetMapping("/summary")
    public ResponseEntity<GamificationSummaryDto> summary() {
        return ResponseEntity.ok(gamificationService.getSummary(currentUserId()));
    }

    @GetMapping("/achievements")
    public ResponseEntity<List<AchievementDto>> achievements() {
        List<AchievementDto> list = userAchievementRepo.findByUserId(currentUserId()).stream()
                .map(ua -> new AchievementDto(
                        ua.getDefinition().getCode(),
                        ua.getDefinition().getName(),
                        ua.getDefinition().getDescription(),
                        ua.getDefinition().getIcon(),
                        ua.getDefinition().getXpReward(),
                        ua.getDefinition().getCoinReward(),
                        ua.getUnlockedAt()))
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/badges")
    public ResponseEntity<List<BadgeDto>> badges() {
        List<BadgeDto> list = userBadgeRepo.findByUserId(currentUserId()).stream()
                .map(ub -> new BadgeDto(ub.getBadgeCode(), ub.getBadgeName(),
                        ub.getIcon(), ub.getUnlockedAt()))
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/streak")
    public ResponseEntity<Map<String, Object>> streak() {
        UserStreak s = streakRepo.findByUserId(currentUserId()).orElse(null);
        int current = s != null ? s.getCurrentStreak() : 0;
        int longest = s != null ? s.getLongestStreak() : 0;
        return ResponseEntity.ok(Map.of(
                "currentStreak", current,
                "longestStreak", longest,
                "lastActivityDate", s != null && s.getLastActivityDate() != null
                        ? s.getLastActivityDate().toString() : ""
        ));
    }

    @GetMapping("/progress")
    public ResponseEntity<Map<String, Object>> progress() {
        GamificationSummaryDto s = gamificationService.getSummary(currentUserId());
        return ResponseEntity.ok(Map.of(
                "level", s.getLevel(),
                "currentXp", s.getCurrentXp(),
                "xpToNextLevel", s.getXpToNextLevel(),
                "progressPercent", s.getProgressPercent(),
                "coins", s.getCoins()
        ));
    }

    @PostMapping("/daily-reward")
    public ResponseEntity<QuizRewardDto> dailyReward() {
        return ResponseEntity.ok(gamificationService.claimDailyReward(currentUserId()));
    }

    private Long currentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof com.finquest.security.UserPrincipal user) {
            return user.getId();
        }
        throw new com.finquest.exception.UnauthorizedException("Not authenticated");
    }
}
