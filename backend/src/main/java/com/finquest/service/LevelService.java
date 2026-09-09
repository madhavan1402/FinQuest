package com.finquest.service;

import com.finquest.dto.LevelCompleteDto;
import com.finquest.dto.LevelInfoDto;
import com.finquest.dto.XpResultDto;
import com.finquest.model.Achievement;
import com.finquest.model.User;
import com.finquest.repository.AchievementRepository;
import com.finquest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LevelService {

    private final UserRepository         userRepository;
    private final AchievementRepository  achievementRepository;
    private final GamificationService    gamificationService;

    // ── Award XP — delegates to GamificationService ──────────────────────────
    public XpResultDto awardXp(Long userId, int xpToAdd) {
        gamificationService.awardXpAndCoins(userId, xpToAdd, 0, "XP award");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        int totalXp = user.getXp();
        return new XpResultDto(
                totalXp,
                user.getLevel(),
                false,
                xpToAdd,
                GamificationService.xpToNextLevel(totalXp),
                null
        );
    }

    // ── Complete Level ────────────────────────────────────────────────────────
    public LevelCompleteDto completeLevel(Long userId) {
        GamificationService.LevelUpResult result = gamificationService.awardXpAndCoins(
                userId, GamificationService.XP_LESSON_COMPLETE,
                GamificationService.COINS_QUIZ_PASS, "Level complete");
        gamificationService.updateStreak(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        int totalXp = user.getXp();

        // Legacy badge check via old Achievement table
        String badge = checkLegacyBadge(user);

        return new LevelCompleteDto(
                userId,
                totalXp,
                user.getLevel(),
                result.leveledUp(),
                GamificationService.xpToNextLevel(totalXp),
                badge
        );
    }

    // ── Level Info ────────────────────────────────────────────────────────────
    public List<LevelInfoDto> getLevelInfo() {
        return List.of(
            levelEntry(1,  "Beginner",        "Start your financial journey"),
            levelEntry(2,  "Saver",           "You completed your first lesson"),
            levelEntry(5,  "Rising Investor", "Building strong money habits"),
            levelEntry(10, "Finance Guru",    "Advanced financial knowledge")
        );
    }

    private LevelInfoDto levelEntry(int level, String title, String description) {
        return new LevelInfoDto(level, title, description, GamificationService.xpForLevel(level));
    }

    // ── User Achievements (legacy) ────────────────────────────────────────────
    public List<Achievement> getUserAchievements(Long userId) {
        return achievementRepository.findByUserId(userId);
    }

    // ── Legacy badge check ────────────────────────────────────────────────────
    private String checkLegacyBadge(User user) {
        String badge = null;
        if (user.getLevel() == 2)          badge = "First Steps";
        else if (user.getLevel() == 5)     badge = "Rising Investor";
        else if (user.getLevel() == 10)    badge = "Finance Guru";
        else if (user.getLevel() % 5 == 0) badge = "Level " + user.getLevel() + " Master";

        if (badge != null && !achievementRepository.existsByUserIdAndBadgeName(user.getId(), badge)) {
            Achievement a = new Achievement();
            a.setUser(user);
            a.setBadgeName(badge);
            achievementRepository.save(a);
        } else {
            badge = null;
        }
        return badge;
    }
}
