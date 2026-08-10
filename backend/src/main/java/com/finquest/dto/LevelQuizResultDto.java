package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Result of a level quiz submission. Backend computes score, percentage,
 * pass/fail, awards rewards via GamificationService, unlocks the next level,
 * and returns updated learning progress. Field names are kept compatible with
 * the existing QuizRewardDto consumed by the frontend Quiz.jsx where possible.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LevelQuizResultDto {
    private int score;
    private int totalQuestions;
    private int percentage;
    private boolean passed;
    private int xpEarned;
    private int coinsEarned;
    private int currentXp;
    private int level;
    private boolean leveledUp;
    private int xpToNextLevel;
    private int currentStreak;
    private List<AchievementDto> achievementsUnlocked;
    private List<BadgeDto> badgesAwarded;
    private String badgeAwarded;
    private boolean nextLevelUnlocked;
    private TieredLearningPathDto learningProgress;
}
