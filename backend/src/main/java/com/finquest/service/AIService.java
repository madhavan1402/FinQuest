package com.finquest.service;

import com.finquest.dto.AiRequest;
import com.finquest.dto.AiResponse;
import com.finquest.model.User;
import com.finquest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {

    private final RestTemplate   restTemplate;
    private final UserRepository userRepository;

    // Flask base URL — override in application.properties if the AI engine
    // runs on a different host or port (e.g. in Docker)
    @Value("${ai.engine.url:http://localhost:5000}")
    private String aiEngineUrl;

    // ── Predict ───────────────────────────────────────────────────────────────
    // Calls all three Flask endpoints in sequence, merges the results,
    // persists literacyLevel + recommendation onto the User row, and returns
    // a combined AiResponse to the controller.
    public AiResponse predict(AiRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

        // Shared payload for literacy + recommendation models
        Map<String, Double> scorePayload = Map.of(
                "quiz_score",       request.getQuizScore(),
                "simulation_score", request.getSimulationScore()
        );

        // 1. Call /predict-literacy
        //    RestTemplate.postForObject sends a POST, deserialises the JSON body into a Map.
        String literacyLevel = callFlask("/predict-literacy", scorePayload, "literacy_level");

        // 2. Call /recommend
        String recommendation = callFlask("/recommend", scorePayload, "recommendation");

        // 3. Call /risk-profile
        //    Risk profile uses investment_choice + loss_tolerance derived from the
        //    user's current level (proxy for investment experience) and financialScore
        //    (proxy for loss tolerance). Both are normalised to 0/1/2 buckets.
        int investmentChoice = bucketLevel(user.getLevel());       // 0=safe, 1=mixed, 2=aggressive
        int lossTolerance    = bucketScore(user.getFinancialScore()); // 0=low, 1=medium, 2=high

        Map<String, Integer> riskPayload = Map.of(
                "investment_choice", investmentChoice,
                "loss_tolerance",    lossTolerance
        );
        String riskProfile = callFlask("/risk-profile", riskPayload, "risk_profile");

        // 4. Persist AI results back onto the user row so they are available
        //    in the profile screen without calling the AI engine again
        user.setLiteracyLevel(literacyLevel);
        user.setRecommendation(recommendation);
        userRepository.save(user);

        return new AiResponse(user.getId(), literacyLevel, recommendation, riskProfile);
    }

    // ── Helper: POST to Flask and extract one field from the JSON response ────
    @SuppressWarnings("unchecked")
    private String callFlask(String path, Object payload, String responseKey) {
        String url = aiEngineUrl + path;
        // postForObject serialises payload to JSON, sends POST, deserialises response to Map
        Map<String, String> response = restTemplate.postForObject(url, payload, Map.class);
        if (response == null || !response.containsKey(responseKey)) {
            throw new RuntimeException("Invalid response from AI engine at " + path);
        }
        return response.get(responseKey);
    }

    // ── Helpers: map user level / score to 0/1/2 buckets for risk model ──────

    // Level 1–3 → 0 (safe), 4–7 → 1 (mixed), 8+ → 2 (aggressive)
    private int bucketLevel(int level) {
        if (level <= 3) return 0;
        if (level <= 7) return 1;
        return 2;
    }

    // Score 0–33 → 0 (low tolerance), 34–66 → 1 (medium), 67–100 → 2 (high)
    private int bucketScore(int score) {
        if (score <= 33) return 0;
        if (score <= 66) return 1;
        return 2;
    }
}
