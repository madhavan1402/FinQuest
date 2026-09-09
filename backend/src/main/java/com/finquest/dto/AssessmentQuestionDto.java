package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Question sent to client for Financial Onboarding.
 * SECURE: Never leaks scoring weights, answers, or points to frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentQuestionDto {
    private Long id;
    private int questionNumber;
    private String category;
    private String questionText;
    private List<OptionDto> options;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionDto {
        private String key;   // "A", "B", "C", "D"
        private String text;  // Description text
    }
}
