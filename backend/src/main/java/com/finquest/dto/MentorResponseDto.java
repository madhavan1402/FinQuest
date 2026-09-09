package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Structured response returned to the frontend.
 * Emotion is validated and sanitized by the backend to be one of the allowed 7 states:
 * happy, encouraging, thinking, celebrating, sad, talking, idle.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorResponseDto {
    private String message;
    private String emotion;
    private String intent;
}
