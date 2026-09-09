package com.finquest.dto;

import com.finquest.model.Difficulty;
import com.finquest.model.LevelStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A lightweight level summary shown in the learning path tiers.
 * Status is computed by the backend — never by the frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LevelDto {
    private int levelNumber;
    private String title;
    private String description;
    private Difficulty difficulty;
    private int xpReward;
    private int coinReward;
    private int estimatedMinutes;
    private LevelStatus status;
    private int bestScore;
    private boolean completed;
    private boolean recommended;
    private String adaptiveDifficulty;

    public LevelDto(int levelNumber, String title, String description, Difficulty difficulty,
                    int xpReward, int coinReward, int estimatedMinutes, LevelStatus status,
                    int bestScore, boolean completed) {
        this(levelNumber, title, description, difficulty, xpReward, coinReward,
                estimatedMinutes, status, bestScore, completed, false, "STANDARD");
    }
}
