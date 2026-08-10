package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response of {@code LearningPathService.getPath()}.
 * <p>
 * Field names match the previous {@code Map<String,Object>}: a {@code modules}
 * list plus a {@code summary} object.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathResponseDto {
    private List<ModuleNodeDto> modules;
    private LearningPathSummaryDto summary;
}
