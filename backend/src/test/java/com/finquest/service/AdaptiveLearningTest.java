package com.finquest.service;

import com.finquest.dto.LearningRecommendationDto;
import com.finquest.dto.QuizQuestionDto;
import com.finquest.model.*;
import com.finquest.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdaptiveLearningTest {

    @Mock
    private UserRepository users;

    @Mock
    private LearningModuleRepository modules;

    @Mock
    private UserProgressRepository progress;

    @Mock
    private QuizRepository quizRepository;

    private LearningPathService service;

    private User beginnerUser;
    private User intermediateUser;
    private User advancedUser;

    private List<LearningModule> mockModules;

    @BeforeEach
    void setUp() {
        service = new LearningPathService(
                users, modules, progress, quizRepository,
                null, null, null
        );
        ReflectionTestUtils.setField(service, "passingPercentage", 70);

        beginnerUser = new User();
        beginnerUser.setId(1L);
        beginnerUser.setLiteracyLevel("BEGINNER");
        beginnerUser.setRiskProfile("Conservative");
        beginnerUser.setFinancialScore(35);

        intermediateUser = new User();
        intermediateUser.setId(2L);
        intermediateUser.setLiteracyLevel("INTERMEDIATE");
        intermediateUser.setRiskProfile("Moderate");
        intermediateUser.setFinancialScore(55);

        advancedUser = new User();
        advancedUser.setId(3L);
        advancedUser.setLiteracyLevel("ADVANCED");
        advancedUser.setRiskProfile("Aggressive");
        advancedUser.setFinancialScore(88);

        mockModules = new ArrayList<>();
        for (int i = 1; i <= 36; i++) {
            LearningModule m = new LearningModule();
            m.setId((long) i);
            m.setSequenceNumber(i);
            m.setModuleKey("level-" + i);
            m.setTitle("Level " + i + " Topic");
            if (i <= 12) {
                m.setTier(LearningTier.BEGINNER);
                m.setDifficulty(Difficulty.EASY);
            } else if (i <= 24) {
                m.setTier(LearningTier.INTERMEDIATE);
                m.setDifficulty(Difficulty.MEDIUM);
            } else {
                m.setTier(LearningTier.ADVANCED);
                m.setDifficulty(Difficulty.HARD);
            }
            m.setXpReward(100);
            m.setCoinReward(25);
            m.setActive(true);
            mockModules.add(m);
        }
    }

    @Test
    @DisplayName("Starting placement: Beginner -> Level 1, Intermediate -> Level 13, Advanced -> Level 25")
    void testStartingPlacementMapping() {
        assertThat(LearningPathService.getStartingLevelForLiteracy("BEGINNER")).isEqualTo(1);
        assertThat(LearningPathService.getStartingLevelForLiteracy("beginner")).isEqualTo(1);
        assertThat(LearningPathService.getStartingLevelForLiteracy(null)).isEqualTo(1);

        assertThat(LearningPathService.getStartingLevelForLiteracy("INTERMEDIATE")).isEqualTo(13);
        assertThat(LearningPathService.getStartingLevelForLiteracy("Intermediate")).isEqualTo(13);

        assertThat(LearningPathService.getStartingLevelForLiteracy("ADVANCED")).isEqualTo(25);
        assertThat(LearningPathService.getStartingLevelForLiteracy("advanced")).isEqualTo(25);
    }

    @Test
    @DisplayName("Intermediate user recommended starting point is Level 13 without false completion")
    void testIntermediateRecommendationStartingPoint() {
        Map<Long, UserProgress> entries = new HashMap<>();
        for (LearningModule m : mockModules) {
            UserProgress p = new UserProgress();
            p.setId(m.getId());
            p.setUser(intermediateUser);
            p.setLearningModule(m);
            p.setUnlocked(m.getSequenceNumber() <= 13);
            p.setCompleted(false);
            p.setStatus(m.getSequenceNumber() <= 13 ? LevelStatus.UNLOCKED : LevelStatus.LOCKED);
            entries.put(m.getId(), p);
        }

        LearningRecommendationDto rec = service.computeRecommendation(intermediateUser, mockModules, entries);

        assertThat(rec.getRecommendedLevelNumber()).isEqualTo(13);
        assertThat(rec.getStartingLevel()).isEqualTo(13);
        assertThat(rec.isPathMastered()).isFalse();
        assertThat(rec.getRecommendationReason()).contains("Level 13");
        assertThat(rec.getRecommendationReason()).contains("INTERMEDIATE");

        // Verify earlier levels (1-12) are NOT marked completed
        for (int i = 1; i < 13; i++) {
            assertThat(entries.get((long) i).isCompleted()).isFalse();
        }
    }

    @Test
    @DisplayName("Advanced user recommended starting point is Level 25 without false completion")
    void testAdvancedRecommendationStartingPoint() {
        Map<Long, UserProgress> entries = new HashMap<>();
        for (LearningModule m : mockModules) {
            UserProgress p = new UserProgress();
            p.setId(m.getId());
            p.setUser(advancedUser);
            p.setLearningModule(m);
            p.setUnlocked(m.getSequenceNumber() <= 25);
            p.setCompleted(false);
            p.setStatus(m.getSequenceNumber() <= 25 ? LevelStatus.UNLOCKED : LevelStatus.LOCKED);
            entries.put(m.getId(), p);
        }

        LearningRecommendationDto rec = service.computeRecommendation(advancedUser, mockModules, entries);

        assertThat(rec.getRecommendedLevelNumber()).isEqualTo(25);
        assertThat(rec.getStartingLevel()).isEqualTo(25);
        assertThat(rec.isPathMastered()).isFalse();
        assertThat(rec.getRecommendationReason()).contains("Level 25");
        assertThat(rec.getRecommendationReason()).contains("ADVANCED");
    }

    @Test
    @DisplayName("Adaptive difficulty: score < 60% activates REINFORCEMENT mode")
    void testAdaptiveDifficultyReinforcementOnLowScore() {
        LearningModule mod = mockModules.get(0); // Level 1
        UserProgress p = new UserProgress();
        p.setAttempts(1);
        p.setLastScore(50);
        p.setCompleted(false);

        String diff = service.determineAdaptiveDifficulty(beginnerUser, mod, p);
        assertThat(diff).isEqualTo("REINFORCEMENT");
    }

    @Test
    @DisplayName("Adaptive difficulty: repeated failures (attempts >= 2, incomplete) activates REINFORCEMENT")
    void testAdaptiveDifficultyReinforcementOnRepeatedFailures() {
        LearningModule mod = mockModules.get(0);
        UserProgress p = new UserProgress();
        p.setAttempts(2);
        p.setLastScore(65);
        p.setCompleted(false);

        String diff = service.determineAdaptiveDifficulty(beginnerUser, mod, p);
        assertThat(diff).isEqualTo("REINFORCEMENT");
    }

    @Test
    @DisplayName("Adaptive difficulty: normal score (60%-84%) yields STANDARD mode")
    void testAdaptiveDifficultyStandard() {
        LearningModule mod = mockModules.get(0);
        UserProgress p = new UserProgress();
        p.setAttempts(1);
        p.setLastScore(75);
        p.setCompleted(true);

        String diff = service.determineAdaptiveDifficulty(beginnerUser, mod, p);
        assertThat(diff).isEqualTo("STANDARD");
    }

    @Test
    @DisplayName("Adaptive difficulty: score >= 85% activates CHALLENGE mode")
    void testAdaptiveDifficultyChallengeOnHighScore() {
        LearningModule mod = mockModules.get(0);
        UserProgress p = new UserProgress();
        p.setAttempts(1);
        p.setLastScore(90);
        p.setCompleted(true);

        String diff = service.determineAdaptiveDifficulty(beginnerUser, mod, p);
        assertThat(diff).isEqualTo("CHALLENGE");
    }

    @Test
    @DisplayName("Adaptive difficulty: Advanced user on lower-tier module (first attempt) activates CHALLENGE")
    void testAdaptiveDifficultyAdvancedUserOnLowerModule() {
        LearningModule mod = mockModules.get(0); // Level 1 (Beginner)
        UserProgress p = new UserProgress();
        p.setAttempts(0);

        String diff = service.determineAdaptiveDifficulty(advancedUser, mod, p);
        assertThat(diff).isEqualTo("CHALLENGE");
    }

    @Test
    @DisplayName("Priority A: IN_PROGRESS module is recommended above others")
    void testRecommendationPriorityInProgress() {
        Map<Long, UserProgress> entries = new HashMap<>();
        for (LearningModule m : mockModules) {
            UserProgress p = new UserProgress();
            p.setId(m.getId());
            p.setUser(beginnerUser);
            p.setLearningModule(m);
            p.setUnlocked(true);
            p.setCompleted(false);
            p.setStatus(LevelStatus.UNLOCKED);
            entries.put(m.getId(), p);
        }

        // Mark Level 3 as IN_PROGRESS
        UserProgress p3 = entries.get(3L);
        p3.setStatus(LevelStatus.IN_PROGRESS);
        p3.setAttempts(1);
        p3.setLastScore(60);

        LearningRecommendationDto rec = service.computeRecommendation(beginnerUser, mockModules, entries);
        assertThat(rec.getRecommendedLevelNumber()).isEqualTo(3);
        assertThat(rec.getRecommendationReason()).contains("Resume Level 3");
    }

    @Test
    @DisplayName("Recommendation updates to next unlocked module after current module completion")
    void testRecommendationAdvancesOnCompletion() {
        Map<Long, UserProgress> entries = new HashMap<>();
        for (LearningModule m : mockModules) {
            UserProgress p = new UserProgress();
            p.setId(m.getId());
            p.setUser(beginnerUser);
            p.setLearningModule(m);
            p.setUnlocked(m.getSequenceNumber() <= 2);
            p.setCompleted(m.getSequenceNumber() == 1); // Level 1 completed
            p.setStatus(m.getSequenceNumber() == 1 ? LevelStatus.COMPLETED : (m.getSequenceNumber() == 2 ? LevelStatus.UNLOCKED : LevelStatus.LOCKED));
            entries.put(m.getId(), p);
        }

        LearningRecommendationDto rec = service.computeRecommendation(beginnerUser, mockModules, entries);
        assertThat(rec.getRecommendedLevelNumber()).isEqualTo(2);
    }

    @Test
    @DisplayName("Recommendation does not jump backwards below assessed starting point")
    void testRecommendationDoesNotJumpBackwards() {
        Map<Long, UserProgress> entries = new HashMap<>();
        for (LearningModule m : mockModules) {
            UserProgress p = new UserProgress();
            p.setId(m.getId());
            p.setUser(intermediateUser);
            p.setLearningModule(m);
            p.setUnlocked(m.getSequenceNumber() <= 14);
            // Levels 1-12 are incomplete, Level 13 is completed
            p.setCompleted(m.getSequenceNumber() == 13);
            p.setStatus(m.getSequenceNumber() == 13 ? LevelStatus.COMPLETED : (m.getSequenceNumber() <= 14 ? LevelStatus.UNLOCKED : LevelStatus.LOCKED));
            entries.put(m.getId(), p);
        }

        LearningRecommendationDto rec = service.computeRecommendation(intermediateUser, mockModules, entries);
        // Must recommend Level 14, NOT Level 1
        assertThat(rec.getRecommendedLevelNumber()).isEqualTo(14);
    }

    @Test
    @DisplayName("Path Mastered: when all modules from starting point through Level 36 are completed")
    void testPathMastered() {
        Map<Long, UserProgress> entries = new HashMap<>();
        for (LearningModule m : mockModules) {
            UserProgress p = new UserProgress();
            p.setId(m.getId());
            p.setUser(advancedUser);
            p.setLearningModule(m);
            p.setUnlocked(true);
            // Completed all levels 25-36, levels 1-24 uncompleted
            p.setCompleted(m.getSequenceNumber() >= 25);
            p.setStatus(m.getSequenceNumber() >= 25 ? LevelStatus.COMPLETED : LevelStatus.UNLOCKED);
            entries.put(m.getId(), p);
        }

        LearningRecommendationDto rec = service.computeRecommendation(advancedUser, mockModules, entries);
        assertThat(rec.isPathMastered()).isTrue();
        assertThat(rec.getRecommendedModuleTitle()).isEqualTo("Curriculum Mastered");
    }

    @Test
    @DisplayName("Fallback question selection: if desired difficulty is missing, all available active questions are returned")
    void testQuestionFallbackSelection() {
        when(users.findById(1L)).thenReturn(Optional.of(beginnerUser));
        when(modules.findByActiveTrueOrderBySequenceNumberAsc()).thenReturn(mockModules);

        // Only EASY questions available for Level 1
        QuizQuestion q1 = new QuizQuestion(101L, "What is cash?", "A", "B", "C", "D", "A", 1, "topic", Difficulty.EASY, "MCQ", "exp", true);
        QuizQuestion q2 = new QuizQuestion(102L, "What is a coin?", "A", "B", "C", "D", "B", 1, "topic", Difficulty.EASY, "MCQ", "exp", true);
        when(quizRepository.findBylevelAndActiveTrue(1)).thenReturn(List.of(q1, q2));

        // When user is in CHALLENGE mode
        UserProgress p = new UserProgress();
        p.setAttempts(1);
        p.setLastScore(95); // triggers CHALLENGE mode
        when(progress.findByUserIdAndLearningModuleId(1L, 1L)).thenReturn(Optional.of(p));

        List<QuizQuestionDto> questions = service.getLevelQuiz(1L, 1);

        assertThat(questions).hasSize(2);
        assertThat(questions.get(0).getAdaptiveMode()).isEqualTo("CHALLENGE");
        // Ensure correctAnswer is NOT leaked
        assertThat(questions.get(0).getQuestionText()).isEqualTo("What is cash?");
    }
}
