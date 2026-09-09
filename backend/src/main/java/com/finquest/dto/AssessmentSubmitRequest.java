package com.finquest.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Payload submitted by user containing answers for assessment questions.
 * Key: questionId (Long) -> Value: selectedOption ("A", "B", "C", "D")
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentSubmitRequest {

    @NotEmpty(message = "Answers map cannot be empty")
    private Map<Long, String> answers;
}
