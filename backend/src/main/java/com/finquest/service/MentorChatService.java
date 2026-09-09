package com.finquest.service;

import com.finquest.dto.FinanceBrainContext;
import com.finquest.dto.LearningRecommendationDto;
import com.finquest.dto.MentorChatRequest;
import com.finquest.dto.MentorResponseDto;
import com.finquest.model.LearningModule;
import com.finquest.model.User;
import com.finquest.model.UserAssessment;
import com.finquest.model.UserProgress;
import com.finquest.repository.LearningModuleRepository;
import com.finquest.repository.UserAssessmentRepository;
import com.finquest.repository.UserProgressRepository;
import com.finquest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * MentorChatService — coordinates the retrieval of authoritative user context from Spring Data JPA
 * repositories and services, builds the safe FinanceBrainContext, and delegates response generation
 * to FinanceBrainService.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MentorChatService {

    private final UserRepository userRepository;
    private final UserAssessmentRepository assessmentRepository;
    private final UserProgressRepository progressRepository;
    private final LearningModuleRepository moduleRepository;
    private final LearningPathService learningPathService;
    private final FinanceBrainService financeBrainService;

    @Transactional(readOnly = true)
    public MentorResponseDto processChat(Long userId, MentorChatRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        FinanceBrainContext context = buildContext(user);
        return financeBrainService.generateResponse(request, context);
    }

    private FinanceBrainContext buildContext(User user) {
        // Retrieve learning path recommendation
        String recLevelTitle = null;
        String recReason = null;
        try {
            LearningRecommendationDto rec = learningPathService.getRecommendation(user.getId());
            if (rec != null) {
                recLevelTitle = "Level " + rec.getRecommendedLevelNumber() + ": " + rec.getRecommendedModuleTitle();
                recReason = rec.getRecommendationReason();
            }
        } catch (Exception e) {
            log.debug("Could not fetch recommendation for user {}: {}", user.getId(), e.getMessage());
        }

        // Retrieve current active/latest in-progress module
        String currentModuleTitle = null;
        int currentModuleBestScore = 0;
        try {
            List<UserProgress> userProgressList = progressRepository.findByUserId(user.getId());
            Optional<UserProgress> inProgress = userProgressList.stream()
                    .filter(p -> p.isUnlocked() && !p.isCompleted())
                    .findFirst();

            if (inProgress.isPresent()) {
                LearningModule m = inProgress.get().getLearningModule();
                currentModuleTitle = "Level " + m.getSequenceNumber() + ": " + m.getTitle();
                currentModuleBestScore = inProgress.get().getBestScore();
            }
        } catch (Exception e) {
            log.debug("Could not determine current module for user {}: {}", user.getId(), e.getMessage());
        }

        // Latest assessment summary if present
        Optional<UserAssessment> latestAssessment = assessmentRepository.findTopByUserIdOrderByCompletedAtDesc(user.getId());
        String literacy = latestAssessment.map(UserAssessment::getLiteracyLevel).orElse(user.getLiteracyLevel());
        String risk = latestAssessment.map(UserAssessment::getRiskProfile).orElse(user.getRiskProfile());

        return FinanceBrainContext.builder()
                .userName(user.getName())
                .literacyLevel(literacy != null ? literacy : "Beginner")
                .riskProfile(risk != null ? risk : "Moderate")
                .financialScore(user.getFinancialScore())
                .currentLevel(user.getLevel())
                .xp(user.getXp())
                .coins(user.getCoins())
                .learningStreak(user.getLearningStreak())
                .assessmentCompleted(user.isAssessmentCompleted())
                .currentModuleTitle(currentModuleTitle)
                .currentModuleBestScore(currentModuleBestScore)
                .recommendedNextTitle(recLevelTitle)
                .recommendedReason(recReason)
                .build();
    }
}
