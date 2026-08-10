package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of awarding XP to a user (used by {@code LevelService.awardXp()}).
 * <p>
 * Field names match the old {@code Map<String,Object>} keys so the response
 * contract for consumers (QuizService/SimulationService) is unchanged.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class XpResultDto {
    private int xp;
    private int level;
    private boolean leveledUp;
    private int xpEarned;
    private int xpToNextLevel;
    private String badgeAwarded;
}
