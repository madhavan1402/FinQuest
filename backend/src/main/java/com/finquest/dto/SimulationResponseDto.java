package com.finquest.dto;

import com.finquest.model.SimulationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response of the simulation endpoints (budget, stock, tax).
 * <p>
 * Combines the simulation outcome (simulationType, result, score) with the
 * gamification update. Field names match the previous flattened
 * {@code Map<String,Object>}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulationResponseDto {
    private SimulationType simulationType;
    private String result;
    private int score;
    private int xp;
    private int level;
    private boolean leveledUp;
    private int xpToNextLevel;
    private String badgeAwarded;
}
