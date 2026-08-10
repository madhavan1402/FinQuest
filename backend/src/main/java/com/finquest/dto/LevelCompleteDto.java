package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of completing a level ({@code LevelService.completeLevel()}).
 * <p>
 * Field names match the old {@code Map<String,Object>} keys, including the
 * {@code userId} that {@code completeLevel} includes but {@code awardXp} does not.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LevelCompleteDto {
    private Long userId;
    private int xp;
    private int level;
    private boolean leveledUp;
    private int xpToNextLevel;
    private String badgeAwarded;
}
