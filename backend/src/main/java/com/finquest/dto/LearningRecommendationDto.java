package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningRecommendationDto {
    private int recommendedLevelNumber;
    private String recommendedModuleTitle;
    private String recommendationReason;
    private String adaptiveDifficulty;
    private boolean pathMastered;
    private int startingLevel;
}
