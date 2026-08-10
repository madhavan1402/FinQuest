package com.finquest.controller;

<<<<<<< HEAD
import com.finquest.dto.QuizQuestionDto;
import com.finquest.dto.QuizRewardDto;
import com.finquest.dto.QuizSubmitRequest;
import com.finquest.security.UserPrincipal;
import com.finquest.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Quiz API. SECURITY: the old endpoints returned correctAnswer to the frontend
 * and trusted a userId from the request body. Both are fixed here:
 *   • GET /api/quiz/{level} now returns secure QuizQuestionDto (NO answer key)
 *   • POST /api/quiz/submit derives the user from the JWT principal
 */
=======
import com.finquest.dto.QuizSubmitRequest;
import com.finquest.model.QuizQuestion;
import com.finquest.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

<<<<<<< HEAD
    // SECURE: returns questions WITHOUT correctAnswer/explanation.
    @GetMapping("/{level}")
    public ResponseEntity<List<QuizQuestionDto>> getQuestions(@PathVariable int level) {
        return ResponseEntity.ok(quizService.getSecureQuestionsByLevel(level));
    }

    // User derived from JWT principal — never from the request body.
    @PostMapping("/submit")
    public ResponseEntity<QuizRewardDto> submitQuiz(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody QuizSubmitRequest request) {
        request.setUserId(principal.getId());
=======
    // GET /api/quiz/{level}
    // Returns all questions for the given level.
    // Frontend renders the quiz UI from this response.
    @GetMapping("/{level}")
    public ResponseEntity<List<QuizQuestion>> getQuestions(@PathVariable int level) {
        return ResponseEntity.ok(quizService.getQuestionsByLevel(level));
    }

    // POST /api/quiz/submit
    // Body: { "userId": 1, "level": 1, "answers": { "1": "A", "2": "C" } }
    // Validates answers, stores result, awards XP, returns score + gamification update.
    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitQuiz(@RequestBody QuizSubmitRequest request) {
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
        return ResponseEntity.ok(quizService.submitQuiz(request));
    }
}
