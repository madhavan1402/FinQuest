package com.finquest.controller;

import com.finquest.dto.AiRequest;
import com.finquest.dto.AiResponse;
import com.finquest.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    // POST /api/ai/predict
    // Body: { "userId": 1, "quizScore": 75, "simulationScore": 60 }
    //
    // Calls the Flask AI engine for all three models, stores the results
    // on the user row, and returns the combined prediction.
    @PostMapping("/predict")
    public ResponseEntity<AiResponse> predict(@RequestBody AiRequest request) {
        return ResponseEntity.ok(aiService.predict(request));
    }

    // GET /api/ai/health
    // Quick check that the AI controller is reachable (does not call Flask)
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "AI Controller reachable"));
    }
}
