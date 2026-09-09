package com.finquest.controller;
import com.finquest.dto.*;
import com.finquest.security.UserPrincipal;
import com.finquest.service.LearningPathService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Learning Path 2.0 API.
 * All endpoints require authentication and derive the user from the JWT
 * principal — the userId is NEVER taken from the client, so User A cannot
 * read or modify User B's progress.
 */
@RestController
@RequestMapping("/api/learning-path")
@RequiredArgsConstructor
public class LearningPathController {

    private final LearningPathService service;

    // GET /api/learning-path — full tiered learning path + summary
    @GetMapping
    public ResponseEntity<TieredLearningPathDto> getPath(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(service.getPath(principal.getId()));
    }

    // GET /api/learning-path/levels/{levelNumber} — level detail
    @GetMapping("/levels/{levelNumber}")
    public ResponseEntity<LevelDetailDto> getLevelDetail(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable int levelNumber) {
        return ResponseEntity.ok(service.getLevelDetail(principal.getId(), levelNumber));
    }

    // GET /api/learning-path/levels/{levelNumber}/quiz — SECURE questions
    // (no correctAnswer, no explanation, no internal solution)
    @GetMapping("/levels/{levelNumber}/quiz")
    public ResponseEntity<List<QuizQuestionDto>> getLevelQuiz(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable int levelNumber) {
        return ResponseEntity.ok(service.getLevelQuiz(principal.getId(), levelNumber));
    }

    // POST /api/learning-path/levels/{levelNumber}/submit — backend-scored
    @PostMapping("/levels/{levelNumber}/submit")
    public ResponseEntity<LevelQuizResultDto> submitLevelQuiz(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable int levelNumber,
            @RequestBody LevelQuizSubmitRequest request) {
        return ResponseEntity.ok(service.submitLevelQuiz(
                principal.getId(), levelNumber, request.getAnswers()));
    }

    // Backward-compat alias for the old /learning-path/progress shape
    @GetMapping("/progress")
    public ResponseEntity<TieredLearningPathDto> getProgress(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(service.getPath(principal.getId()));
    }

    // GET /api/learning-path/recommendation — personalized next lesson recommendation
    @GetMapping("/recommendation")
    public ResponseEntity<LearningRecommendationDto> getRecommendation(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(service.getRecommendation(principal.getId()));
    }

    // Reset learning progress only (does NOT touch XP/coins)
    @PostMapping("/reset")
    public ResponseEntity<TieredLearningPathDto> reset(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(service.reset(principal.getId()));
    }
}
