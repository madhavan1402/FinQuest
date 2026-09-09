package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Result returned after submitting financial onboarding assessment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResultDto {
    private int financialScore;               // 0–100
    private String literacyLevel;             // BEGINNER / INTERMEDIATE / ADVANCED
    private String riskProfile;               // Conservative / Moderate / Aggressive
    private String recommendation;            // Personalized starting recommendation
    private boolean assessmentCompleted;      // true
    private String summary;                   // Detailed diagnostic summary
    private String recommendedStartingPoint;  // E.g. "Level 1: Budgeting & Emergency Fund"
    private Map<String, Integer> categoryScores; // Breakdown per category
    private LocalDateTime completedAt;
}
