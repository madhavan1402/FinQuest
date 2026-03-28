package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Sent by the frontend to POST /api/ai/predict.
// Spring Boot forwards quiz_score + simulation_score to the Flask AI engine.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiRequest {
    private Long   userId;
    private double quizScore;
    private double simulationScore;
}
