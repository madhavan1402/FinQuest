package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizRewardDto {
    private int score;
    private int totalQuestions;
    private boolean passed;
    private int xpEarned;
    private int coinsEarned;
    private int currentXp;
    private int level;
    private boolean leveledUp;
    private int xpToNextLevel;
    private int progressPercent;
    private int currentStreak;
    private List<AchievementDto> achievementsUnlocked;
    private List<BadgeDto> badgesAwarded;
    // kept for backward compat with existing frontend
    private String badgeAwarded;
}
