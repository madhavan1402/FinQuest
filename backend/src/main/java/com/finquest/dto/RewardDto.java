package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response of {@code RewardService.rewards()} — the user's reward wallet summary.
 * <p>
 * Field names match the previous {@code Map<String,Object>}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardDto {
    private int totalCoins;
    private int totalXp;
    private int currentLevel;
    private int currentStreak;
    private int longestStreak;
    private List<String> badges;
}
