package com.finquest.service;

import com.finquest.dto.QuizQuestionDto;
import com.finquest.dto.QuizRewardDto;
import com.finquest.dto.QuizSubmitRequest;
import com.finquest.dto.QuizSubmitResponseDto;
import com.finquest.model.QuizQuestion;
import com.finquest.model.QuizResult;
import com.finquest.model.User;
import com.finquest.repository.QuizRepository;
import com.finquest.repository.QuizResultRepository;
import com.finquest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository         quizRepository;
    private final QuizResultRepository   quizResultRepository;
    private final UserRepository         userRepository;
    private final GamificationService    gamificationService;

    /** Legacy raw accessor (kept for any internal callers). */
    public List<QuizQuestion> getQuestionsByLevel(int level) {
        return quizRepository.findByLevel(level);
    }

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

        List<QuizQuestion> questions = quizRepository.findByLevel(request.getLevel());
        if (questions.isEmpty()) {
            throw new RuntimeException("No questions found for level: " + request.getLevel());
        }

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
    }
}
