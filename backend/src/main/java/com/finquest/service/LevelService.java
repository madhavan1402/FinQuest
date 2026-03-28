package com.finquest.service;

import com.finquest.model.Achievement;
import com.finquest.model.User;
import com.finquest.repository.AchievementRepository;
import com.finquest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LevelService {

    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;

    // ── XP Configuration ────────────────────────────────────────────────────
    // Each completed level awards this many XP points
    private static final int XP_PER_LEVEL = 100;

    // Formula: XP needed to reach the NEXT level = current level * XP_PER_LEVEL
    // Level 1 → 2 requires 100 XP
    // Level 2 → 3 requires 200 XP
    // Level 3 → 4 requires 300 XP  ... and so on
    private static int xpThreshold(int currentLevel) {
        return currentLevel * XP_PER_LEVEL;
    }

    // ── Award XP (shared) ────────────────────────────────────────────────────
    // Central method called by QuizService and SimulationService to credit XP.
    // Returns a summary map so every caller gets a consistent response shape.
    public Map<String, Object> awardXp(Long userId, int xpToAdd) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        user.setXp(user.getXp() + xpToAdd);

        boolean leveledUp = false;
        while (user.getXp() >= xpThreshold(user.getLevel())) {
            user.setXp(user.getXp() - xpThreshold(user.getLevel()));
            user.setLevel(user.getLevel() + 1);
            leveledUp = true;
        }

        userRepository.save(user);
        String badge = checkAndAwardBadge(user);

        Map<String, Object> result = new HashMap<>();
        result.put("xp",           user.getXp());
        result.put("level",        user.getLevel());
        result.put("leveledUp",    leveledUp);
        result.put("xpEarned",     xpToAdd);
        result.put("xpToNextLevel", xpThreshold(user.getLevel()) - user.getXp());
        result.put("badgeAwarded", badge);
        return result;
    }

    // ── Complete Level ───────────────────────────────────────────────────────
    // Called when a user finishes a financial lesson/quiz.
    // Awards XP, checks for level-up, unlocks milestone badges.
    public Map<String, Object> completeLevel(Long userId) {
        // 1. Load the user or throw if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // 2. Award XP for completing the level
        user.setXp(user.getXp() + XP_PER_LEVEL);

        // 3. Check if accumulated XP crosses the threshold for the next level
        //    Loop handles edge case where a user gains enough XP to skip multiple levels
        boolean leveledUp = false;
        while (user.getXp() >= xpThreshold(user.getLevel())) {
            // Subtract the threshold cost and increment the level
            user.setXp(user.getXp() - xpThreshold(user.getLevel()));
            user.setLevel(user.getLevel() + 1);
            leveledUp = true;
        }

        // 4. Persist the updated XP and level back to the DB
        userRepository.save(user);

        // 5. Check and award any badges the user has now unlocked
        String badge = checkAndAwardBadge(user);

        // 6. Build the response payload
        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("xp", user.getXp());
        result.put("level", user.getLevel());
        result.put("leveledUp", leveledUp);
        result.put("xpToNextLevel", xpThreshold(user.getLevel()) - user.getXp());
        result.put("badgeAwarded", badge); // null if no new badge
        return result;
    }

    // ── Badge Logic ──────────────────────────────────────────────────────────
    // Awards a badge when the user hits a level milestone (every 5 levels).
    // existsByUserIdAndBadgeName ensures the same badge is never awarded twice.
    private String checkAndAwardBadge(User user) {
        String badge = null;

        if (user.getLevel() == 2) badge = "First Steps";
        else if (user.getLevel() == 5) badge = "Rising Investor";
        else if (user.getLevel() == 10) badge = "Finance Guru";
        else if (user.getLevel() % 5 == 0) badge = "Level " + user.getLevel() + " Master";

        if (badge != null && !achievementRepository.existsByUserIdAndBadgeName(user.getId(), badge)) {
            achievementRepository.save(new Achievement(null, user, badge));
        } else {
            badge = null; // badge already existed, don't report it as newly awarded
        }

        return badge;
    }

    // ── Level Info ───────────────────────────────────────────────────────────
    // Returns a summary of all level thresholds so the frontend can render
    // a progress bar or level map without hardcoding values.
    public List<Map<String, Object>> getLevelInfo() {
        return List.of(
            levelEntry(1, "Beginner",        "Start your financial journey"),
            levelEntry(2, "Saver",           "You completed your first lesson"),
            levelEntry(5, "Rising Investor", "Building strong money habits"),
            levelEntry(10, "Finance Guru",   "Advanced financial knowledge")
        );
    }

    private Map<String, Object> levelEntry(int level, String title, String description) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("level", level);
        entry.put("title", title);
        entry.put("description", description);
        entry.put("xpRequired", xpThreshold(level));
        return entry;
    }

    // ── User Achievements ────────────────────────────────────────────────────
    public List<Achievement> getUserAchievements(Long userId) {
        return achievementRepository.findByUserId(userId);
    }
}
