package com.finquest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for POST /api/mentor/chat.
 * Only `intent` is required. `userInput`, `pageContext`, and `topicHint` are optional context hints.
 * Note: userId is NEVER accepted from the frontend; it is always derived from the authenticated JWT principal.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorChatRequest {

    @NotBlank(message = "Intent cannot be blank")
    private String intent;

    private String userInput;

    private String pageContext;

    private String topicHint;
}
