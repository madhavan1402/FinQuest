package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Monthly/yearly reward statistics for a user.
 * <p>
 * Field names match the previous {@code Map<String,Object>} produced by
 * {@code RewardService.statistics()}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardStatisticsDto {
    private String period;
    private int daysLearned;
    private long quizzesPassed;
    private long modulesCompleted;
    private int xpEarned;
    private int coinsEarned;
    private int learningMinutes;
    private List<String> activityDays;
}
