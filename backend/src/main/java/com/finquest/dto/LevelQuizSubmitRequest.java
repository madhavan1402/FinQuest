package com.finquest.dto;

import lombok.Data;

import java.util.Map;

/**
 * Request body for POST /api/learning-path/levels/{levelNumber}/submit.
 * The userId is NOT taken from the client — the authenticated JWT principal
 * identifies the user. answers maps questionId → chosen option letter.
 */
@Data
public class LevelQuizSubmitRequest {
    private Map<String, String> answers;
}
