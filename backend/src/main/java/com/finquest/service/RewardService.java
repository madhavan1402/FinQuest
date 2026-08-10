package com.finquest.service;

import com.finquest.model.*;
import com.finquest.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final UserRepository          users;
    private final RewardWalletRepository  wallets;
    private final RewardHistoryRepository history;
    private final UserStreakRepository    streaks;
    private final AchievementRepository  achievements;
    private final GamificationService    gamification;

    @Transactional
    public Map<String, Object> awardQuizPass(Long userId, String moduleTitle, int xp, int coins) {
        // Delegate to GamificationService — idempotent, handles level/streak/achievements
        gamification.awardXpAndCoins(userId, xp, coins, moduleTitle + " Quiz");
        gamification.updateStreak(userId);

        User user = users.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        UserStreak streak = streaks.findByUserId(userId).orElseGet(() -> {
            UserStreak s = new UserStreak(); s.setUser(user); return streaks.save(s);
        });

        List<String> earned = new ArrayList<>();
        if (!achievements.existsByUserIdAndBadgeName(userId, "First FinQuest Win")) {
            Achievement badge = new Achievement();
            badge.setUser(user);
            badge.setBadgeName("First FinQuest Win");
            achievements.save(badge);
            earned.add("First FinQuest Win");
        }

        return Map.of(
                "coinsEarned", coins, "xpEarned", xp,
                "currentLevel", user.getLevel(),
                "badges", earned,
                "currentStreak", streak.getCurrentStreak()
        );
    }

    public Map<String, Object> rewards(Long userId) {
        User user = users.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        RewardWallet w = wallets.findByUserId(userId).orElseGet(() -> {
            RewardWallet rw = new RewardWallet(); rw.setUser(user); return wallets.save(rw);
        });
        UserStreak s = streaks.findByUserId(userId).orElseGet(() -> {
            UserStreak st = new UserStreak(); st.setUser(user); return streaks.save(st);
        });
        return Map.of(
                "totalCoins", user.getCoins(),
                "totalXp", user.getXp(),
                "currentLevel", user.getLevel(),
                "currentStreak", s.getCurrentStreak(),
                "longestStreak", s.getLongestStreak(),
                "badges", achievements.findByUserId(userId).stream()
                        .map(Achievement::getBadgeName).toList()
        );
    }

    public List<RewardHistory> history(Long userId) {
        users.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return history.findByUserIdOrderByEarnedAtDesc(userId);
    }

    public Map<String, Object> monthly(Long userId, int year, int month) {
        return statistics(userId, YearMonth.of(year, month));
    }

    public Map<String, Object> yearly(Long userId, int year) {
        Map<String, Object> months = new LinkedHashMap<>();
        for (int m = 1; m <= 12; m++)
            months.put(Month.of(m).name().substring(0, 3), statistics(userId, YearMonth.of(year, m)));
        return Map.of("year", year, "months", months, "rewards", rewards(userId));
    }

    private Map<String, Object> statistics(Long id, YearMonth period) {
        List<RewardHistory> entries = history(id).stream()
                .filter(h -> YearMonth.from(h.getEarnedAt()).equals(period)).toList();
        Set<LocalDate> days = entries.stream()
                .map(h -> h.getEarnedAt().toLocalDate()).collect(Collectors.toSet());
        int xp = entries.stream().mapToInt(RewardHistory::getXp).sum();
        int coins = entries.stream().mapToInt(RewardHistory::getCoins).sum();
        return Map.of(
                "period", period.toString(),
                "daysLearned", days.size(),
                "quizzesPassed", entries.stream().filter(h -> "QUIZ_PASS".equals(h.getRewardType())).count(),
                "modulesCompleted", entries.stream().filter(h -> "XP_COINS".equals(h.getRewardType())).count(),
                "xpEarned", xp, "coinsEarned", coins,
                "learningMinutes", entries.size() * 15,
                "activityDays", days.stream().map(LocalDate::toString).toList()
        );
    }
}
