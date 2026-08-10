package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Full learning-path response: three tiers, each containing its levels.
 * Also carries the user summary (XP, coins, streak, gamification level) for the
 * LearningPath page header.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TieredLearningPathDto {
    private List<TierDto> tiers;
    private LearningPathSummaryDto summary;
}
