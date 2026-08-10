package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response of {@code QuizService.submitQuiz()}.
 * <p>
 * Combines the quiz outcome (score, totalQuestions, xpEarned) with the
 * gamification update (xp, level, leveledUp, xpToNextLevel, badgeAwarded).
 * Field names match the previous flattened {@code Map<String,Object>}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmitResponseDto {
    private int score;
    private int totalQuestions;
    private int xpEarned;
    private int xp;
    private int level;
    private boolean leveledUp;
    private int xpToNextLevel;
    private String badgeAwarded;
}
