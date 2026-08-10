package com.finquest.dto;

import com.finquest.model.LearningTier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A single learning tier (BEGINNER / INTERMEDIATE / ADVANCED) with its levels.
 * Returned by GET /api/learning-path.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TierDto {
    private LearningTier tier;
    private String title;
    private List<LevelDto> levels;
}
