package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Authoritative user context assembled from verified database tables and services.
 * Contains only educational & progress state; never passwords, tokens, or unnecessary secrets.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinanceBrainContext {
    private String userName;
    private String literacyLevel;
    private String riskProfile;
    private int financialScore;
    private int currentLevel;
    private int xp;
    private int coins;
    private int learningStreak;
    private boolean assessmentCompleted;
    private String currentModuleTitle;
    private int currentModuleBestScore;
    private String recommendedNextLevel;
    private String recommendedNextTitle;
    private String recommendedReason;
    private Map<String, Integer> categoryScores;
}
