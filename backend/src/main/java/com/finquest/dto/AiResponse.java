package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Returned to the frontend after all three AI models have been called.
// Also reflects what was persisted back onto the User row.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiResponse {
    private Long   userId;
    private String literacyLevel;   // Beginner / Intermediate / Advanced
    private String recommendation;  // next topic or level to tackle
    private String riskProfile;     // Conservative / Moderate / Aggressive
}
