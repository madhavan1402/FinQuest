package com.finquest.dto;

import com.finquest.model.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A quiz question sent to the frontend for display.
 * SECURE: does NOT include correctAnswer, explanation, or any internal solution.
 * The backend is the only place that knows the correct answers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionDto {
    private Long questionId;
    private int questionNumber;
    private String questionText;
    private List<String> options;
    private Difficulty difficulty;
    private String topic;
}
