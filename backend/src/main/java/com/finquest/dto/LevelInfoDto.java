package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A single level milestone shown on the level map.
 * <p>
 * Replaces the previous {@code Map<String,Object>} produced by
 * {@code LevelService.getLevelInfo()}. Field names match the old JSON keys
 * exactly so the frontend contract is unchanged.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LevelInfoDto {
    private int level;
    private String title;
    private String description;
    private int xpRequired;
}
