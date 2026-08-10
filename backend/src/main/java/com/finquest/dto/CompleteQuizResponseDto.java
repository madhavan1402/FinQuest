package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response of {@code LearningPathService.completeQuiz()}.
 * <p>
 * Extends {@link LearningPathResponseDto} with the quiz outcome fields
 * {@code passed}, {@code passingMark}, {@code reward} and {@code nextUnlocked},
 * matching the previous flattened {@code Map<String,Object>}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompleteQuizResponseDto extends LearningPathResponseDto {
    private boolean passed;
    private int passingMark;
    private QuizPassRewardDto reward;
    private boolean nextUnlocked;

    public CompleteQuizResponseDto(List<ModuleNodeDto> modules, LearningPathSummaryDto summary,
                                   boolean passed, int passingMark, QuizPassRewardDto reward,
                                   boolean nextUnlocked) {
        super(modules, summary);
        this.passed = passed;
        this.passingMark = passingMark;
        this.reward = reward;
        this.nextUnlocked = nextUnlocked;
    }
}
