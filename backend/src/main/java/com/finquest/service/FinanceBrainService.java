package com.finquest.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finquest.dto.FinanceBrainContext;
import com.finquest.dto.MentorChatRequest;
import com.finquest.dto.MentorResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * FinanceBrainService — communicates with the external LLM provider (e.g. Gemini).
 * Responsible for prompt synthesis, calling LLM API, response validation, emotion sanitization,
 * response length caps, and falling back safely to deterministic copy on errors/timeouts/disabled state.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FinanceBrainService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${ai.brain.enabled:false}")
    private boolean enabled;

    @Value("${ai.brain.provider:gemini}")
    private String provider;

    @Value("${ai.brain.gemini.api-key:}")
    private String geminiApiKey;

    @Value("${ai.brain.gemini.model:gemini-1.5-flash}")
    private String geminiModel;

    @Value("${ai.brain.gemini.endpoint:https://generativelanguage.googleapis.com/v1beta/models/}")
    private String geminiEndpoint;

    @Value("${ai.brain.response.max-tokens:512}")
    private int maxTokens;

    @Value("${ai.brain.timeout-ms:8000}")
    private int timeoutMs;

    private static final Set<String> ALLOWED_EMOTIONS = Set.of(
            "happy", "encouraging", "thinking", "celebrating", "sad", "talking", "idle"
    );

    public boolean isEnabled() {
        return enabled;
    }

    public String getProviderName() {
        return provider;
    }

    /**
     * Generates a mentor response using the LLM. If disabled, misconfigured, or if an error occurs,
     * it falls back gracefully to a deterministic response.
     */
    public MentorResponseDto generateResponse(MentorChatRequest request, FinanceBrainContext context) {
        String intent = (request.getIntent() != null) ? request.getIntent().trim().toLowerCase() : "chat";
        String expectedEmotion = mapEmotionForIntent(intent);

        if (!enabled || geminiApiKey == null || geminiApiKey.isBlank()) {
            log.debug("AI Finance Brain disabled or missing API key. Using deterministic fallback.");
            return buildFallbackResponse(intent, context, request);
        }

        try {
            String systemPrompt = buildSystemPrompt(context, request);
            String userPrompt = buildUserPrompt(intent, context, request);

            String rawAnswer = callGemini(systemPrompt, userPrompt);
            if (rawAnswer == null || rawAnswer.isBlank()) {
                log.warn("Empty response from AI Brain provider. Using fallback.");
                return buildFallbackResponse(intent, context, request);
            }

            ParsedResponse parsed = parseAndSanitizeResponse(rawAnswer, expectedEmotion);
            return MentorResponseDto.builder()
                    .message(parsed.message)
                    .emotion(parsed.emotion)
                    .intent(intent)
                    .build();

        } catch (Exception e) {
            log.warn("AI Finance Brain call failed ({}). Gracefully falling back to deterministic mentor.", e.getMessage());
            return buildFallbackResponse(intent, context, request);
        }
    }

    private record ParsedResponse(String message, String emotion) {}

    private ParsedResponse parseAndSanitizeResponse(String rawAnswer, String defaultEmotion) {
        String cleaned = rawAnswer.trim();
        // Check if answer is formatted as JSON
        if (cleaned.startsWith("{") && cleaned.endsWith("}")) {
            try {
                JsonNode root = objectMapper.readTree(cleaned);
                String msg = root.has("message") ? root.get("message").asText("") : "";
                String emo = root.has("emotion") ? root.get("emotion").asText("").toLowerCase().trim() : defaultEmotion;
                if (!ALLOWED_EMOTIONS.contains(emo)) {
                    emo = defaultEmotion;
                }
                if (!msg.isBlank()) {
                    return new ParsedResponse(sanitizeMessage(msg), emo);
                }
            } catch (Exception ignored) {
                // fall through to text sanitization
            }
        }

        // Clean markdown code fence if present
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substring(0, cleaned.length() - 3);
            }
            cleaned = cleaned.trim();
            try {
                JsonNode root = objectMapper.readTree(cleaned);
                String msg = root.has("message") ? root.get("message").asText("") : "";
                String emo = root.has("emotion") ? root.get("emotion").asText("").toLowerCase().trim() : defaultEmotion;
                if (!ALLOWED_EMOTIONS.contains(emo)) {
                    emo = defaultEmotion;
                }
                if (!msg.isBlank()) {
                    return new ParsedResponse(sanitizeMessage(msg), emo);
                }
            } catch (Exception ignored) {}
        }

        // If plain text, strip quotes or extraneous preamble
        cleaned = sanitizeMessage(cleaned);
        return new ParsedResponse(cleaned, defaultEmotion);
    }

    private String sanitizeMessage(String text) {
        String s = text.trim();
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() > 2) {
            s = s.substring(1, s.length() - 1).trim();
        }
        // Limit length to ~500 characters to ensure concise voice dialogue
        if (s.length() > 500) {
            int cut = s.lastIndexOf('.', 480);
            if (cut > 200) {
                s = s.substring(0, cut + 1);
            } else {
                s = s.substring(0, 500) + "...";
            }
        }
        return s;
    }

    private String callGemini(String systemInstruction, String userText) throws Exception {
        // Construct Gemini REST API payload:
        // POST {geminiEndpoint}{geminiModel}:generateContent?key={geminiApiKey}
        String endpoint = geminiEndpoint.endsWith("/") ? geminiEndpoint : geminiEndpoint + "/";
        String url = endpoint + geminiModel + ":generateContent?key=" + geminiApiKey;

        Map<String, Object> systemPart = Map.of("text", systemInstruction);
        Map<String, Object> systemInstructionObj = Map.of("parts", List.of(systemPart));

        Map<String, Object> userPart = Map.of("text", userText);
        Map<String, Object> contentObj = Map.of(
                "role", "user",
                "parts", List.of(userPart)
        );

        Map<String, Object> generationConfig = Map.of(
                "temperature", 0.7,
                "maxOutputTokens", maxTokens,
                "responseMimeType", "application/json"
        );

        Map<String, Object> body = new HashMap<>();
        body.put("contents", List.of(contentObj));
        body.put("systemInstruction", systemInstructionObj);
        body.put("generationConfig", generationConfig);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Gemini HTTP error: " + response.getStatusCode());
        }

        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode candidates = root.path("candidates");
        if (candidates.isArray() && !candidates.isEmpty()) {
            JsonNode first = candidates.get(0);
            JsonNode parts = first.path("content").path("parts");
            if (parts.isArray() && !parts.isEmpty()) {
                return parts.get(0).path("text").asText("");
            }
        }
        return null;
    }

    private String buildSystemPrompt(FinanceBrainContext context, MentorChatRequest req) {
        return """
        You are a friendly, encouraging, and knowledgeable personal finance mentor in the FinQuest educational platform.
        
        PERSONA & TONE:
        - Educational, supportive, concise, and engaging.
        - Talk directly to the user in 1 to 3 short sentences.
        - Never sound like a generic robotic disclaimer.
        - Output JSON only in the following format:
          {"message": "string (1-3 sentences)", "emotion": "happy|encouraging|thinking|celebrating|sad|talking|idle"}
        
        FINANCIAL SAFETY & COMPLIANCE RULES:
        - You are an educational tool. Never give real-world certified financial advice, guarantee returns, or suggest individual stock trades.
        - Never fabricate user balances, market quotes, or transaction executions.
        - Base guidance strictly on verified user metrics provided in the prompt.
        - If the user's literacy level is Beginner, use simple real-life analogies.
        - If Intermediate or Advanced, discuss compound interest, risk diversification, and asset allocation comfortably.
        """;
    }

    private String buildUserPrompt(String intent, FinanceBrainContext ctx, MentorChatRequest req) {
        StringBuilder sb = new StringBuilder();
        sb.append("VERIFIED USER CONTEXT (from FinQuest DB):\n");
        sb.append("- Name: ").append(ctx.getUserName() != null ? ctx.getUserName() : "friend").append("\n");
        sb.append("- Financial Literacy Level: ").append(ctx.getLiteracyLevel()).append("\n");
        sb.append("- Risk Profile: ").append(ctx.getRiskProfile()).append("\n");
        sb.append("- Financial Health Score: ").append(ctx.getFinancialScore()).append("/100\n");
        sb.append("- Current Level: ").append(ctx.getCurrentLevel()).append(" (XP: ").append(ctx.getXp()).append(", Coins: ").append(ctx.getCoins()).append(")\n");
        sb.append("- Learning Streak: ").append(ctx.getLearningStreak()).append(" days\n");
        if (ctx.getCurrentModuleTitle() != null) {
            sb.append("- Current Active Module: ").append(ctx.getCurrentModuleTitle());
            if (ctx.getCurrentModuleBestScore() > 0) {
                sb.append(" (Best Score: ").append(ctx.getCurrentModuleBestScore()).append("%)");
            }
            sb.append("\n");
        }
        if (ctx.getRecommendedNextTitle() != null) {
            sb.append("- Recommended Next Module: ").append(ctx.getRecommendedNextTitle())
              .append(" (").append(ctx.getRecommendedReason() != null ? ctx.getRecommendedReason() : "Adaptive next step").append(")\n");
        }

        sb.append("\nCURRENT INTERACTION:\n");
        sb.append("- Intent: ").append(intent).append("\n");
        if (req.getPageContext() != null && !req.getPageContext().isBlank()) {
            sb.append("- Screen/Page Context: ").append(req.getPageContext()).append("\n");
        }
        if (req.getTopicHint() != null && !req.getTopicHint().isBlank()) {
            sb.append("- Topic / Lesson Hint: ").append(req.getTopicHint()).append("\n");
        }
        if (req.getUserInput() != null && !req.getUserInput().isBlank()) {
            sb.append("- User Query/Message: \"").append(req.getUserInput()).append("\"\n");
        }

        sb.append("\nPlease provide a personalized, encouraging mentor dialogue response (1 to 3 sentences) in the required JSON format.");
        return sb.toString();
    }

    public String mapEmotionForIntent(String intent) {
        if (intent == null) return "talking";
        return switch (intent.toLowerCase()) {
            case "greet" -> "happy";
            case "celebrate" -> "celebrating";
            case "react_sad", "fail" -> "sad";
            case "think" -> "thinking";
            case "encourage", "recommend", "assessment", "assessment_guidance" -> "encouraging";
            case "explain", "tip", "chat" -> "talking";
            default -> "talking";
        };
    }

    public MentorResponseDto buildFallbackResponse(String intent, FinanceBrainContext context, MentorChatRequest req) {
        String name = context.getUserName() != null ? context.getUserName().split(" ")[0] : "friend";
        String emotion = mapEmotionForIntent(intent);
        String message;

        switch (intent.toLowerCase()) {
            case "greet" -> message = "Hello " + name + "! Ready to build your financial intelligence today? Let's take another smart step!";
            case "celebrate" -> {
                String topic = req.getTopicHint() != null ? req.getTopicHint() : "the lesson";
                message = "Outstanding performance, " + name + "! You've mastered " + topic + "! Keep this momentum going!";
                emotion = "celebrating";
            }
            case "react_sad", "fail" -> {
                message = "Don't be discouraged, " + name + "! Financial mastery is built through practice and learning from mistakes. Let's try again!";
                emotion = "sad";
            }
            case "think" -> {
                String topic = req.getTopicHint() != null ? req.getTopicHint() : "this concept";
                message = "Take your time to analyze " + topic + ". Consider the trade-off between risk, liquidity, and return.";
                emotion = "thinking";
            }
            case "encourage" -> {
                String topic = req.getTopicHint() != null ? req.getTopicHint() : "this topic";
                message = "You've got this! Mastering " + topic + " is a foundational pillar for your financial freedom.";
                emotion = "encouraging";
            }
            case "recommend" -> {
                String next = context.getRecommendedNextTitle() != null ? context.getRecommendedNextTitle() : "the next module";
                message = "Recommended Next: " + next + ". Tailored to advance your current financial learning path!";
                emotion = "encouraging";
            }
            case "assessment", "assessment_guidance" -> {
                message = "Welcome to the Onboarding Assessment! There are no wrong answers here — this helps us calibrate your personalized starting point.";
                emotion = "encouraging";
            }
            default -> {
                String tip = req.getTopicHint() != null && !req.getTopicHint().isBlank() ? req.getTopicHint() :
                        "Always ensure your emergency fund covers 3 to 6 months of living expenses before taking higher investment risks.";
                message = tip;
                emotion = "talking";
            }
        }

        return MentorResponseDto.builder()
                .message(message)
                .emotion(emotion)
                .intent(intent)
                .build();
    }
}
