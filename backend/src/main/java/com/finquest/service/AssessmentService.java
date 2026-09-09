package com.finquest.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finquest.dto.AssessmentQuestionDto;
import com.finquest.dto.AssessmentResultDto;
import com.finquest.dto.AssessmentSubmitRequest;
import com.finquest.exception.BadRequestException;
import com.finquest.exception.ResourceNotFoundException;
import com.finquest.model.AssessmentQuestion;
import com.finquest.model.User;
import com.finquest.model.UserAssessment;
import com.finquest.repository.AssessmentQuestionRepository;
import com.finquest.repository.UserAssessmentRepository;
import com.finquest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssessmentService {

    private final AssessmentQuestionRepository questionRepository;
    private final UserAssessmentRepository     userAssessmentRepository;
    private final UserRepository               userRepository;
    private final ObjectMapper                 objectMapper;

    /**
     * Retrieves all active assessment questions for onboarding.
     * SECURE: Strips all scoring weights and answer evaluation logic.
     */
    public List<AssessmentQuestionDto> getQuestions() {
        List<AssessmentQuestion> questions = questionRepository.findAllByActiveTrueOrderByQuestionNumberAsc();
        List<AssessmentQuestionDto> result = new ArrayList<>();

        for (AssessmentQuestion q : questions) {
            List<AssessmentQuestionDto.OptionDto> options = List.of(
                    new AssessmentQuestionDto.OptionDto("A", q.getOptionA()),
                    new AssessmentQuestionDto.OptionDto("B", q.getOptionB()),
                    new AssessmentQuestionDto.OptionDto("C", q.getOptionC()),
                    new AssessmentQuestionDto.OptionDto("D", q.getOptionD())
            );
            result.add(new AssessmentQuestionDto(
                    q.getId(),
                    q.getQuestionNumber(),
                    q.getCategory(),
                    q.getQuestionText(),
                    options
            ));
        }
        return result;
    }

    /**
     * Submits and deterministically scores the onboarding assessment.
     * Updates user's financialScore, literacyLevel, riskProfile, and assessmentCompleted.
     * Stores the historical attempt in user_assessments.
     */
    @Transactional
    public AssessmentResultDto submitAssessment(Long userId, AssessmentSubmitRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Map<Long, String> answers = request.getAnswers();
        if (answers == null || answers.isEmpty()) {
            throw new BadRequestException("Assessment answers cannot be empty");
        }

        List<AssessmentQuestion> questions = questionRepository.findAllByActiveTrueOrderByQuestionNumberAsc();
        if (questions.isEmpty()) {
            throw new BadRequestException("No active assessment questions configured");
        }

        // Validate all questions are answered
        for (AssessmentQuestion q : questions) {
            if (!answers.containsKey(q.getId())) {
                throw new BadRequestException("Missing answer for question #" + q.getQuestionNumber() + " (" + q.getCategory() + ")");
            }
            String selected = answers.get(q.getId());
            if (selected == null || !selected.trim().matches("(?i)[A-D]")) {
                throw new BadRequestException("Invalid option selected for question #" + q.getQuestionNumber() + ": " + selected);
            }
        }

        // Deterministic backend calculation
        int rawScore = 0;
        Map<String, Integer> categoryScores = new LinkedHashMap<>();
        String riskProfile = "Moderate";

        for (AssessmentQuestion q : questions) {
            String selectedOption = answers.get(q.getId()).toUpperCase().trim();
            int points = q.getScoreForOption(selectedOption);
            rawScore += points;
            categoryScores.merge(q.getCategory(), points, Integer::sum);

            // Separate financial literacy scoring from risk profiling (Requirement 2)
            if (q.isRiskQuestion()) {
                riskProfile = switch (selectedOption) {
                    case "A" -> "Conservative";
                    case "B" -> "Moderately Conservative";
                    case "C" -> "Moderate";
                    case "D" -> "Aggressive";
                    default  -> "Moderate";
                };
            }
        }

        int financialScore = Math.max(0, Math.min(100, rawScore));

        // Initial classification (Requirement: 0-39 = BEGINNER, 40-69 = INTERMEDIATE, 70-100 = ADVANCED)
        String literacyLevel;
        String recommendedStartingPoint;
        String recommendation;
        String summary;

        if (financialScore <= 39) {
            literacyLevel = "BEGINNER";
            recommendedStartingPoint = "Level 1: Budgeting & Cash Flow Foundations";
            recommendation = "Build an emergency fund, track monthly spending using a 50/30/20 budget, and eliminate high-interest debt.";
            summary = "Your financial health is in the foundational stage. Prioritizing consistent savings habits and basic expense tracking will jumpstart your financial journey.";
        } else if (financialScore <= 69) {
            literacyLevel = "INTERMEDIATE";
            recommendedStartingPoint = "Level 3: Strategic Saving & Asset Allocation";
            recommendation = "Expand emergency savings to 3–6 months, optimize debt repayment, and begin automated index investing.";
            summary = "You have solid financial fundamentals! You are ready to accelerate wealth building through disciplined budgeting and broad-market investing.";
        } else {
            literacyLevel = "ADVANCED";
            recommendedStartingPoint = "Level 5: Advanced Wealth Building & Market Dynamics";
            recommendation = "Focus on asset allocation, tax-efficient investing, and long-term compound growth across diversified holdings.";
            summary = "Excellent financial literacy and disciplined habits! You demonstrate strong mastery of budgeting, risk management, and compound growth principles.";
        }

        // Serialize answers for historical audit
        String answersJson;
        try {
            answersJson = objectMapper.writeValueAsString(answers);
        } catch (JsonProcessingException e) {
            log.warn("Could not serialize answers map to JSON: {}", e.getMessage());
            answersJson = answers.toString();
        }

        // Preserve assessment history (Requirement 3 & 4)
        UserAssessment assessmentRecord = new UserAssessment();
        assessmentRecord.setUser(user);
        assessmentRecord.setFinancialScore(financialScore);
        assessmentRecord.setLiteracyLevel(literacyLevel);
        assessmentRecord.setRiskProfile(riskProfile);
        assessmentRecord.setRecommendation(recommendation);
        assessmentRecord.setAnswersJson(answersJson);
        assessmentRecord.setCompletedAt(LocalDateTime.now());
        userAssessmentRepository.save(assessmentRecord);

        // Update current user state
        user.setFinancialScore(financialScore);
        user.setLiteracyLevel(literacyLevel);
        user.setRiskProfile(riskProfile);
        user.setRecommendation(recommendation);
        user.setAssessmentCompleted(true);
        userRepository.save(user);

        log.info("Completed financial onboarding for user id {}: score={}, level={}, risk={}",
                user.getId(), financialScore, literacyLevel, riskProfile);

        return AssessmentResultDto.builder()
                .financialScore(financialScore)
                .literacyLevel(literacyLevel)
                .riskProfile(riskProfile)
                .recommendation(recommendation)
                .assessmentCompleted(true)
                .summary(summary)
                .recommendedStartingPoint(recommendedStartingPoint)
                .categoryScores(categoryScores)
                .completedAt(assessmentRecord.getCompletedAt())
                .build();
    }

    /**
     * Returns the latest assessment result for the authenticated user.
     */
    public AssessmentResultDto getLatestResult(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Optional<UserAssessment> latest = userAssessmentRepository.findTopByUserIdOrderByCompletedAtDesc(userId);

        if (latest.isPresent()) {
            UserAssessment a = latest.get();
            String summary;
            String startingPoint;
            if (a.getFinancialScore() <= 39) {
                summary = "Your financial health is in the foundational stage. Prioritizing consistent savings habits and basic expense tracking will jumpstart your financial journey.";
                startingPoint = "Level 1: Budgeting & Cash Flow Foundations";
            } else if (a.getFinancialScore() <= 69) {
                summary = "You have solid financial fundamentals! You are ready to accelerate wealth building through disciplined budgeting and broad-market investing.";
                startingPoint = "Level 3: Strategic Saving & Asset Allocation";
            } else {
                summary = "Excellent financial literacy and disciplined habits! You demonstrate strong mastery of budgeting, risk management, and compound growth principles.";
                startingPoint = "Level 5: Advanced Wealth Building & Market Dynamics";
            }

            return AssessmentResultDto.builder()
                    .financialScore(a.getFinancialScore())
                    .literacyLevel(a.getLiteracyLevel())
                    .riskProfile(a.getRiskProfile())
                    .recommendation(a.getRecommendation())
                    .assessmentCompleted(true)
                    .summary(summary)
                    .recommendedStartingPoint(startingPoint)
                    .categoryScores(Collections.emptyMap())
                    .completedAt(a.getCompletedAt())
                    .build();
        }

        // If user has assessmentCompleted flag from seed/update but no history record
        if (user.isAssessmentCompleted()) {
            return AssessmentResultDto.builder()
                    .financialScore(user.getFinancialScore())
                    .literacyLevel(user.getLiteracyLevel())
                    .riskProfile(user.getRiskProfile())
                    .recommendation(user.getRecommendation())
                    .assessmentCompleted(true)
                    .summary("Current financial profile based on your latest evaluation.")
                    .recommendedStartingPoint("Level 1")
                    .categoryScores(Collections.emptyMap())
                    .completedAt(LocalDateTime.now())
                    .build();
        }

        throw new ResourceNotFoundException("No assessment result found for user id " + userId);
    }
}
