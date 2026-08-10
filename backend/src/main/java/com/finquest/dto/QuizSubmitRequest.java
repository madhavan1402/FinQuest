package com.finquest.dto;

import lombok.Data;

import java.util.Map;

// Request body for POST /api/quiz/submit
// answers is a map of questionId → chosen option ("A", "B", "C", or "D")
// Example: { "userId": 1, "level": 1, "answers": { "1": "A", "2": "C", "3": "B" } }
@Data
public class QuizSubmitRequest {
    private Long userId;
    private int level;
<<<<<<< HEAD
    // Present when this quiz belongs to the sequential learning path. Its reward
    // is awarded atomically by LearningPathService after the pass is confirmed.
    private String moduleId;
=======
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577

    // Key = question ID (as String from JSON), Value = chosen answer letter
    private Map<String, String> answers;
}
