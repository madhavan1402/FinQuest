package com.finquest.service;

<<<<<<< HEAD
import com.finquest.dto.QuizQuestionDto;
import com.finquest.dto.QuizRewardDto;
import com.finquest.dto.QuizSubmitRequest;
import com.finquest.dto.QuizSubmitResponseDto;
=======
import com.finquest.dto.QuizSubmitRequest;
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
import com.finquest.model.QuizQuestion;
import com.finquest.model.QuizResult;
import com.finquest.model.User;
import com.finquest.repository.QuizRepository;
import com.finquest.repository.QuizResultRepository;
import com.finquest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
<<<<<<< HEAD
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
=======

import java.util.HashMap;
import java.util.List;
import java.util.Map;
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577

@Service
@RequiredArgsConstructor
public class QuizService {

<<<<<<< HEAD
    private final QuizRepository         quizRepository;
    private final QuizResultRepository   quizResultRepository;
    private final UserRepository         userRepository;
    private final GamificationService    gamificationService;

    /** Legacy raw accessor (kept for any internal callers). */
=======
    private final QuizRepository       quizRepository;
    private final QuizResultRepository quizResultRepository;
    private final UserRepository       userRepository;
    private final LevelService         levelService; // for XP + level-up

    // ── Get Questions ─────────────────────────────────────────────────────────
    // Returns all questions for the requested level.
    // correctAnswer is included here; in production you'd strip it from the
    // GET response and only use it server-side during submission.
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
    public List<QuizQuestion> getQuestionsByLevel(int level) {
        return quizRepository.findByLevel(level);
    }

<<<<<<< HEAD
    /**
     * SECURE: returns only the fields the frontend needs to render the quiz.
     * correctAnswer, explanation and any internal solution are NEVER returned.
     */
    public List<QuizQuestionDto> getSecureQuestionsByLevel(int level) {
        List<QuizQuestion> questions = quizRepository.findBylevelAndActiveTrue(level);
        List<QuizQuestionDto> result = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            QuizQuestion q = questions.get(i);
            result.add(new QuizQuestionDto(
                    q.getId(), i + 1, q.getQuestion(),
                    List.of(q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()),
                    q.getDifficulty(), q.getTopic()));
        }
        return result;
    }

    @Transactional
    public QuizRewardDto submitQuiz(QuizSubmitRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

=======
    // ── Submit Quiz ───────────────────────────────────────────────────────────
    // Flow:
    //   1. Load questions for the level from DB
    //   2. Compare each submitted answer against the stored correctAnswer
    //   3. Calculate score (correct count) and XP (score × 10)
    //   4. Persist the QuizResult row
    //   5. Call LevelService.awardXp() to update user XP + trigger level-up
    //   6. Return a combined response
    public Map<String, Object> submitQuiz(QuizSubmitRequest request) {

        // 1. Load the user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

        // 2. Load all questions for this level
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
        List<QuizQuestion> questions = quizRepository.findByLevel(request.getLevel());
        if (questions.isEmpty()) {
            throw new RuntimeException("No questions found for level: " + request.getLevel());
        }

<<<<<<< HEAD
        int correctCount = 0;
        for (QuizQuestion q : questions) {
            String submitted = request.getAnswers().get(String.valueOf(q.getId()));
            if (q.getCorrectAnswer().equalsIgnoreCase(submitted)) correctCount++;
        }

        int total = questions.size();
        boolean passed = total > 0 && correctCount * 100 >= total * 70;
        boolean perfect = correctCount == total;

        // Persist quiz result
        quizResultRepository.save(new QuizResult(null, user, request.getLevel(), correctCount));

        // Delegate all rewards to GamificationService
        return gamificationService.processQuizResult(
                request.getUserId(), correctCount, total, perfect, passed, request.getModuleId());
    }

    // Backward-compat wrapper returning old DTO shape (used by LevelService callers)
    public QuizSubmitResponseDto submitQuizLegacy(QuizSubmitRequest request) {
        QuizRewardDto r = submitQuiz(request);
        return new QuizSubmitResponseDto(
                r.getScore(), r.getTotalQuestions(), r.getXpEarned(),
                r.getCurrentXp(), r.getLevel(), r.isLeveledUp(),
                r.getXpToNextLevel(), r.getBadgeAwarded());
=======
        // 3. Validate answers — iterate questions, look up the user's answer by question ID
        int correctCount = 0;
        for (QuizQuestion q : questions) {
            String submitted = request.getAnswers().get(String.valueOf(q.getId()));
            // Null-safe comparison: treat missing answer as wrong
            if (q.getCorrectAnswer().equalsIgnoreCase(submitted)) {
                correctCount++;
            }
        }

        // 4. XP formula: each correct answer = 10 XP
        //    e.g. 7 correct out of 10 → 70 XP
        int xpEarned = correctCount * 10;

        // 5. Persist the quiz result
        QuizResult quizResult = new QuizResult(null, user, request.getLevel(), correctCount);
        quizResultRepository.save(quizResult);

        // 6. Award XP via the shared LevelService — handles level-up + badges
        Map<String, Object> xpResult = levelService.awardXp(request.getUserId(), xpEarned);

        // 7. Build response combining quiz outcome + gamification update
        Map<String, Object> response = new HashMap<>();
        response.put("score",          correctCount);
        response.put("totalQuestions", questions.size());
        response.put("xpEarned",       xpEarned);
        response.putAll(xpResult); // merges: level, xp, leveledUp, xpToNextLevel, badgeAwarded
        return response;
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
    }
}
