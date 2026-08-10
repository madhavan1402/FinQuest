package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Reward summary awarded for passing a quiz inside the learning path.
 * <p>
 * Field names match the previous {@code Map<String,Object>} produced by
 * {@code RewardService.awardQuizPass()}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizPassRewardDto {
    private int coinsEarned;
    private int xpEarned;
    private int currentLevel;
    private List<String> badges;
    private int currentStreak;
}
