package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A single node in the learning path (one module).
 * <p>
 * Field names match the previous {@code Map<String,Object>} produced by
 * {@code LearningPathService.node()}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleNodeDto {
    private String id;
    private String title;
    private int level;
    private int xp;
    private int coins;
    private String status;
}
