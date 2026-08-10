package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Summary statistics for a learning path.
 * <p>
 * Field names match the previous {@code Map<String,Object>} produced by
 * {@code LearningPathService.summary()}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathSummaryDto {
    private long completionPercent;
    private int xp;
    private int level;
    private int coins;
    private int streak;
    private long completedModules;
    private long remainingModules;
}
