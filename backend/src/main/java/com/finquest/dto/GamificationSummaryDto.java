package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GamificationSummaryDto {
    private Long userId;
    private String name;
    private int level;
    private int currentXp;
    private int xpToNextLevel;
    private int progressPercent;
    private int coins;
    private int currentStreak;
    private int longestStreak;
    private List<AchievementDto> achievements;
    private List<BadgeDto> badges;
    private int totalAchievements;
    private int totalBadges;
}
