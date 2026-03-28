package com.finquest.controller;

import com.finquest.dto.QuizSubmitRequest;
import com.finquest.model.QuizQuestion;
import com.finquest.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

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
        return ResponseEntity.ok(quizService.submitQuiz(request));
    }
}
