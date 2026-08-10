package com.finquest.controller;

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
@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

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
        return ResponseEntity.ok(quizService.submitQuiz(request));
    }
}
