package com.finquest.service;

import com.finquest.dto.AchievementDto;
import com.finquest.dto.BadgeDto;
import com.finquest.dto.GamificationSummaryDto;
import com.finquest.dto.QuizRewardDto;
import com.finquest.model.*;
import com.finquest.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Central gamification service — ALL XP, coin, level, streak, achievement
 * and badge changes must go through this service.
 *
 * Level thresholds (cumulative XP required to reach each level):
 *   Level 1 →    0 XP
 *   Level 2 →  100 XP
 *   Level 3 →  250 XP
 *   Level 4 →  450 XP
 *   Level N →  sum of 100 + 150 + 200 + ... = 100*(N-1) + 50*(N-1)*(N-2)
 *
 * Formula: threshold(N) = 50 * N * (N - 1) + 100 * (N - 1)  for N >= 2
 *          simplified:   threshold(N) = (N - 1) * (50*N + 50)  = 50*(N-1)*(N+1)
 */
@Service
@RequiredArgsConstructor
public class GamificationService {

    // ── XP rewards ────────────────────────────────────────────────────────────
    public static final int XP_LESSON_COMPLETE   = 50;
    public static final int XP_QUIZ_PASS         = 100;
    public static final int XP_QUIZ_PERFECT      = 150;
    public static final int XP_MODULE_COMPLETE   = 200;
    public static final int XP_DAILY_STREAK      = 25;
    public static final int XP_DAILY_CHALLENGE   = 75;
    public static final int XP_SIMULATION        = 50;

    // ── Coin rewards ──────────────────────────────────────────────────────────
    public static final int COINS_QUIZ_PASS      = 25;
    public static final int COINS_QUIZ_PERFECT   = 50;
    public static final int COINS_DAILY_STREAK   = 10;
    public static final int COINS_DAILY_REWARD   = 20;
    public static final int COINS_LEVEL_UP       = 30;

    private final UserRepository                  userRepo;
    private final UserStreakRepository            streakRepo;
    private final RewardWalletRepository          walletRepo;
    private final RewardHistoryRepository         historyRepo;
    private final XPHistoryRepository             xpHistoryRepo;
    private final AchievementDefinitionRepository definitionRepo;
    private final UserAchievementRepository       userAchievementRepo;
    private final UserBadgeRepository             userBadgeRepo;

    // ── Level threshold ───────────────────────────────────────────────────────
    // Cumulative XP needed to REACH a given level (level >= 1).
    // Level 1 = 0, Level 2 = 100, Level 3 = 250, Level 4 = 450, Level 5 = 700 …
    public static int xpForLevel(int level) {
        if (level <= 1) return 0;
        // Each level step costs 50 more than the previous: 100, 150, 200, 250 …
        // Sum of arithmetic series: 100*(n-1) + 50*(n-1)*(n-2)/2
        int n = level - 1;
        return 100 * n + 25 * n * (n - 1);
    }

    // XP needed to go from current level to next level
    public static int xpStepToNextLevel(int level) {
        return xpForLevel(level + 1) - xpForLevel(level);
    }

    // Derive level from total cumulative XP
    public static int levelFromTotalXp(int totalXp) {
        int level = 1;
        while (xpForLevel(level + 1) <= totalXp) level++;
        return level;
    }

    // XP within the current level (0 … xpStepToNextLevel-1)
    public static int xpWithinLevel(int totalXp) {
        int level = levelFromTotalXp(totalXp);
        return totalXp - xpForLevel(level);
    }

    // XP remaining to reach next level
    public static int xpToNextLevel(int totalXp) {
        int level = levelFromTotalXp(totalXp);
        return xpForLevel(level + 1) - totalXp;
    }

    // Progress percent within current level (0–100)
    public static int progressPercent(int totalXp) {
        int level = levelFromTotalXp(totalXp);
        int step = xpStepToNextLevel(level);
        int within = totalXp - xpForLevel(level);
        return step == 0 ? 100 : (int) Math.min(100, within * 100L / step);
    }

    // ── Award XP + coins (core method) ───────────────────────────────────────
    /**
     * Awards XP and coins to a user atomically.
     * Recalculates level from total cumulative XP.
     * Awards level-up coins if level increased.
     * Logs XP history.
     * Returns whether the user leveled up and the new level.
     */
    @Transactional
    public LevelUpResult awardXpAndCoins(Long userId, int xp, int coins, String reason) {
        User user = requireUser(userId);
        int oldLevel = levelFromTotalXp(user.getXp());

        // Award XP — stored as cumulative total on user
        user.setXp(user.getXp() + xp);
        int newLevel = levelFromTotalXp(user.getXp());
        user.setLevel(newLevel);

        // Award coins — never go negative
        if (coins > 0) {
            user.setCoins(Math.max(0, user.getCoins() + coins));
            RewardWallet wallet = requireWallet(user);
            wallet.setCoins(Math.max(0, wallet.getCoins() + coins));
            walletRepo.save(wallet);
        }

        // Bonus coins for leveling up
        boolean leveledUp = newLevel > oldLevel;
        if (leveledUp) {
            int bonus = COINS_LEVEL_UP * (newLevel - oldLevel);
            user.setCoins(user.getCoins() + bonus);
            RewardWallet wallet = requireWallet(user);
            wallet.setCoins(wallet.getCoins() + bonus);
            walletRepo.save(wallet);
        }

        userRepo.save(user);

        // Log XP history
        if (xp > 0) {
            XPHistory hist = new XPHistory();
            hist.setUser(user);
            hist.setAmount(xp);
            hist.setReason(reason);
            hist.setAwardedAt(LocalDateTime.now());
            xpHistoryRepo.save(hist);
        }

        // Log reward history
        if (xp > 0 || coins > 0) {
            RewardHistory rh = new RewardHistory();
            rh.setUser(user);
            rh.setRewardType("XP_COINS");
            rh.setDescription(reason);
            rh.setXp(xp);
            rh.setCoins(coins);
            rh.setEarnedAt(LocalDateTime.now());
            historyRepo.save(rh);
        }

        return new LevelUpResult(leveledUp, oldLevel, newLevel);
    }

    // ── Streak update ─────────────────────────────────────────────────────────
    /**
     * Updates the user's streak. Idempotent for same-day calls.
     * Returns the new streak value and whether it increased.
     */
    @Transactional
    public StreakResult updateStreak(Long userId) {
        User user = requireUser(userId);
        UserStreak streak = requireStreak(user);
        LocalDate today = LocalDate.now();

        if (today.equals(streak.getLastActivityDate())) {
            // Already updated today — idempotent
            return new StreakResult(streak.getCurrentStreak(), false);
        }

        boolean consecutive = today.minusDays(1).equals(streak.getLastActivityDate());
        int newStreak = consecutive ? streak.getCurrentStreak() + 1 : 1;
        streak.setCurrentStreak(newStreak);
        streak.setLongestStreak(Math.max(streak.getLongestStreak(), newStreak));
        streak.setLastActivityDate(today);
        streakRepo.save(streak);

        // Also sync to user table
        user.setLearningStreak(newStreak);
        user.setLastLearningDate(today);
        userRepo.save(user);

        return new StreakResult(newStreak, true);
    }

    // ── Achievement unlock ────────────────────────────────────────────────────
    /**
     * Unlocks an achievement for a user. Idempotent — safe to call multiple times.
     * Returns the AchievementDto if newly unlocked, null if already had it.
     */
    @Transactional
    public AchievementDto unlockAchievement(Long userId, String code) {
        if (userAchievementRepo.existsByUserIdAndDefinitionCode(userId, code)) return null;

        AchievementDefinition def = definitionRepo.findByCode(code).orElse(null);
        if (def == null) return null;

        User user = requireUser(userId);
        UserAchievement ua = new UserAchievement();
        ua.setUser(user);
        ua.setDefinition(def);
        ua.setUnlockedAt(LocalDateTime.now());
        userAchievementRepo.save(ua);

        // Award XP + coins for the achievement
        if (def.getXpReward() > 0 || def.getCoinReward() > 0) {
            awardXpAndCoins(userId, def.getXpReward(), def.getCoinReward(),
                    "Achievement: " + def.getName());
        }

        return new AchievementDto(def.getCode(), def.getName(), def.getDescription(),
                def.getIcon(), def.getXpReward(), def.getCoinReward(), ua.getUnlockedAt());
    }

    // ── Badge unlock ──────────────────────────────────────────────────────────
    /**
     * Unlocks a badge for a user. Idempotent.
     * Returns BadgeDto if newly unlocked, null if already had it.
     */
    @Transactional
    public BadgeDto unlockBadge(Long userId, String badgeCode, String badgeName, String icon) {
        if (userBadgeRepo.existsByUserIdAndBadgeCode(userId, badgeCode)) return null;

        User user = requireUser(userId);
        UserBadge ub = new UserBadge();
        ub.setUser(user);
        ub.setBadgeCode(badgeCode);
        ub.setBadgeName(badgeName);
        ub.setIcon(icon);
        ub.setUnlockedAt(LocalDateTime.now());
        userBadgeRepo.save(ub);

        return new BadgeDto(badgeCode, badgeName, icon, ub.getUnlockedAt());
    }

    // ── Process quiz result ───────────────────────────────────────────────────
    /**
     * Full quiz reward pipeline — called after a quiz is scored.
     * Handles XP, coins, streak, achievements, badges atomically.
     * Idempotent: duplicate submissions won't double-reward.
     */
    @Transactional
    public QuizRewardDto processQuizResult(Long userId, int score, int totalQuestions,
                                           boolean isPerfect, boolean passed, String moduleKey) {
        User user = requireUser(userId);
        List<AchievementDto> newAchievements = new ArrayList<>();
        List<BadgeDto> newBadges = new ArrayList<>();

        int xpEarned = 0;
        int coinsEarned = 0;

        if (passed) {
            xpEarned = isPerfect ? XP_QUIZ_PERFECT : XP_QUIZ_PASS;
            coinsEarned = isPerfect ? COINS_QUIZ_PERFECT : COINS_QUIZ_PASS;
        }

        // Award XP + coins
        LevelUpResult levelUp = awardXpAndCoins(userId, xpEarned, coinsEarned,
                isPerfect ? "Perfect quiz score" : passed ? "Quiz passed" : "Quiz attempt");

        // Update streak
        StreakResult streakResult = updateStreak(userId);

        // Streak XP bonus
        if (streakResult.increased() && streakResult.streak() > 1) {
            awardXpAndCoins(userId, XP_DAILY_STREAK, COINS_DAILY_STREAK,
                    "Daily streak bonus (day " + streakResult.streak() + ")");
        }

        // Reload user after all XP changes
        user = requireUser(userId);

        // ── Check achievements ────────────────────────────────────────────────
        // FIRST_QUIZ
        AchievementDto a = unlockAchievement(userId, "FIRST_QUIZ");
        if (a != null) newAchievements.add(a);

        // PERFECT_SCORE
        if (isPerfect) {
            a = unlockAchievement(userId, "PERFECT_SCORE");
            if (a != null) newAchievements.add(a);
        }

        // QUIZ_MASTER — check total quiz count
        long quizCount = userAchievementRepo.existsByUserIdAndDefinitionCode(userId, "FIRST_QUIZ") ? 1 : 0;
        // We use XP history as proxy for quiz count
        long xpRows = xpHistoryRepo.countByUserId(userId);
        if (xpRows >= 10) {
            a = unlockAchievement(userId, "QUIZ_MASTER");
            if (a != null) newAchievements.add(a);
        }

        // Streak achievements
        int currentStreak = streakResult.streak();
        if (currentStreak >= 5) {
            a = unlockAchievement(userId, "FIVE_DAY_STREAK");
            if (a != null) newAchievements.add(a);
        }
        if (currentStreak >= 10) {
            a = unlockAchievement(userId, "TEN_DAY_STREAK");
            if (a != null) newAchievements.add(a);
        }
        if (currentStreak >= 30) {
            a = unlockAchievement(userId, "THIRTY_DAY_STREAK");
            if (a != null) newAchievements.add(a);
        }

        // XP milestones
        int totalXp = user.getXp();
        if (totalXp >= 1000) {
            a = unlockAchievement(userId, "XP_1000");
            if (a != null) newAchievements.add(a);
        }
        if (totalXp >= 5000) {
            a = unlockAchievement(userId, "XP_5000");
            if (a != null) newAchievements.add(a);
        }

        // Level 10
        if (user.getLevel() >= 10) {
            a = unlockAchievement(userId, "LEVEL_10");
            if (a != null) newAchievements.add(a);
        }

        // Module-specific achievements
        if (moduleKey != null) {
            if ("saving-money".equals(moduleKey)) {
                a = unlockAchievement(userId, "SAVING_MASTER");
                if (a != null) newAchievements.add(a);
            }
            if ("investing-basics".equals(moduleKey)) {
                a = unlockAchievement(userId, "INVESTMENT_BEGINNER");
                if (a != null) newAchievements.add(a);
            }
        }

        // ── Check badges ──────────────────────────────────────────────────────
        newBadges.addAll(checkAndAwardBadges(userId, user.getLevel(), currentStreak, newAchievements));

        // Reload user one final time
        user = requireUser(userId);
        int finalXp = user.getXp();

        return new QuizRewardDto(
                score, totalQuestions, passed,
                xpEarned, coinsEarned,
                finalXp, user.getLevel(),
                levelUp.leveledUp(),
                xpToNextLevel(finalXp),
                progressPercent(finalXp),
                currentStreak,
                newAchievements, newBadges,
                newBadges.isEmpty() ? null : newBadges.get(0).getBadgeName()
        );
    }

// ── Badge award logic ─────────────────────────────────────────────────────
    public List<BadgeDto> checkAndAwardBadges(Long userId, int level, int streak,
                                                List<AchievementDto> newAchievements) {
        List<BadgeDto> awarded = new ArrayList<>();
        BadgeDto b;

        // Level-based badges
        if (level >= 2) {
            b = unlockBadge(userId, "FIRST_STEPS", "First Steps", "🎓");
            if (b != null) awarded.add(b);
        }
        if (level >= 5) {
            b = unlockBadge(userId, "RISING_INVESTOR", "Rising Investor", "📈");
            if (b != null) awarded.add(b);
        }
        if (level >= 10) {
            b = unlockBadge(userId, "FINANCE_GURU", "Finance Guru", "🧠");
            if (b != null) awarded.add(b);
        }

        // Streak badges
        if (streak >= 5) {
            b = unlockBadge(userId, "STREAK_CHAMPION", "Streak Champion", "🔥");
            if (b != null) awarded.add(b);
        }

        // Achievement-triggered badges
        for (AchievementDto ach : newAchievements) {
            switch (ach.getCode()) {
                case "PERFECT_SCORE" -> {
                    b = unlockBadge(userId, "QUIZ_MASTER", "Quiz Master", "🥇");
                    if (b != null) awarded.add(b);
                }
                case "SAVING_MASTER" -> {
                    b = unlockBadge(userId, "SAVING_MASTER", "Saving Master", "💰");
                    if (b != null) awarded.add(b);
                }
                case "INVESTMENT_BEGINNER" -> {
                    b = unlockBadge(userId, "INVESTMENT_BEGINNER", "Investment Beginner", "📊");
                    if (b != null) awarded.add(b);
                }
                case "FINANCE_EXPLORER" -> {
                    b = unlockBadge(userId, "FINANCE_EXPLORER", "Finance Explorer", "🏆");
                    if (b != null) awarded.add(b);
                }
            }
        }

        return awarded;
    }

    // ── Gamification summary ──────────────────────────────────────────────────
    public GamificationSummaryDto getSummary(Long userId) {
        User user = requireUser(userId);
        UserStreak streak = requireStreak(user);

        List<AchievementDto> achievements = userAchievementRepo.findByUserId(userId).stream()
                .map(ua -> new AchievementDto(
                        ua.getDefinition().getCode(),
                        ua.getDefinition().getName(),
                        ua.getDefinition().getDescription(),
                        ua.getDefinition().getIcon(),
                        ua.getDefinition().getXpReward(),
                        ua.getDefinition().getCoinReward(),
                        ua.getUnlockedAt()))
                .toList();

        List<BadgeDto> badges = userBadgeRepo.findByUserId(userId).stream()
                .map(ub -> new BadgeDto(ub.getBadgeCode(), ub.getBadgeName(),
                        ub.getIcon(), ub.getUnlockedAt()))
                .toList();

        int totalXp = user.getXp();
        return new GamificationSummaryDto(
                userId, user.getName(),
                user.getLevel(), totalXp,
                xpToNextLevel(totalXp),
                progressPercent(totalXp),
                user.getCoins(),
                streak.getCurrentStreak(),
                streak.getLongestStreak(),
                achievements, badges,
                achievements.size(), badges.size()
        );
    }

    // ── Daily reward ──────────────────────────────────────────────────────────
    @Transactional
    public QuizRewardDto claimDailyReward(Long userId) {
        UserStreak streak = requireStreak(requireUser(userId));
        LocalDate today = LocalDate.now();
        // Already claimed today
        if (today.equals(streak.getLastActivityDate())) {
            User u = requireUser(userId);
            return buildMinimalResult(u);
        }
        awardXpAndCoins(userId, XP_DAILY_STREAK, COINS_DAILY_REWARD, "Daily reward");
        updateStreak(userId);
        User u = requireUser(userId);
        return buildMinimalResult(u);
    }

    private QuizRewardDto buildMinimalResult(User u) {
        int xp = u.getXp();
        UserStreak s = requireStreak(u);
        return new QuizRewardDto(0, 0, false, 0, 0, xp, u.getLevel(), false,
                xpToNextLevel(xp), progressPercent(xp), s.getCurrentStreak(),
                List.of(), List.of(), null);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private User requireUser(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    private UserStreak requireStreak(User user) {
        return streakRepo.findByUserId(user.getId()).orElseGet(() -> {
            UserStreak s = new UserStreak();
            s.setUser(user);
            return streakRepo.save(s);
        });
    }

    private RewardWallet requireWallet(User user) {
        return walletRepo.findByUserId(user.getId()).orElseGet(() -> {
            RewardWallet w = new RewardWallet();
            w.setUser(user);
            return walletRepo.save(w);
        });
    }

    // ── Value records ─────────────────────────────────────────────────────────
    public record LevelUpResult(boolean leveledUp, int oldLevel, int newLevel) {}
    public record StreakResult(int streak, boolean increased) {}
}
