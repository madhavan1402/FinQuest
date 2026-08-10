package com.finquest.dto;

import com.finquest.model.Difficulty;
import com.finquest.model.LevelStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Detailed information for a single learning level.
 * Returned by GET /api/learning-path/levels/{levelNumber}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LevelDetailDto {
    private int levelNumber;
    private String moduleKey;
    private String title;
    private String description;
    private String tier;
    private Difficulty difficulty;
    private int questionCount;
    private int xpReward;
    private int coinReward;
    private int estimatedMinutes;
    private LevelStatus status;
    private int attempts;
    private int bestScore;
}
