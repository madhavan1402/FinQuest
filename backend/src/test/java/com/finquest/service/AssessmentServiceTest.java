package com.finquest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finquest.dto.AssessmentResultDto;
import com.finquest.dto.AssessmentSubmitRequest;
import com.finquest.exception.BadRequestException;
import com.finquest.model.AssessmentQuestion;
import com.finquest.model.User;
import com.finquest.repository.AssessmentQuestionRepository;
import com.finquest.repository.UserAssessmentRepository;
import com.finquest.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentServiceTest {

    @Mock
    private AssessmentQuestionRepository questionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAssessmentRepository userAssessmentRepository;

    private AssessmentService assessmentService;

    private User testUser;
    private List<AssessmentQuestion> mockQuestions;

    @BeforeEach
    void setUp() {
        assessmentService = new AssessmentService(
                questionRepository,
                userAssessmentRepository,
                userRepository,
                new ObjectMapper()
        );

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("tester@finquest.com");
        testUser.setName("Tester");

        mockQuestions = new ArrayList<>();

        // 10 mock questions
        for (int i = 1; i <= 10; i++) {
            AssessmentQuestion q = new AssessmentQuestion();
            q.setId((long) i);
            q.setQuestionNumber(i);
            q.setQuestionText("Question " + i);
            q.setCategory("CATEGORY_" + i);
            q.setRiskQuestion(i == 7);
            q.setActive(true);

            q.setOptionA("Opt A");
            q.setOptionB("Opt B");
            q.setOptionC("Opt C");
            q.setOptionD("Opt D");

            q.setScoreA(10);
            q.setScoreB(7);
            q.setScoreC(4);
            q.setScoreD(1);

            mockQuestions.add(q);
        }
    }

    @Test
    @DisplayName("Submit assessment calculates score, level, and risk correctly for top scores")
    void testSubmitAssessment_AdvancedLevel() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(questionRepository.findAllByActiveTrueOrderByQuestionNumberAsc()).thenReturn(mockQuestions);
        when(userAssessmentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Map<Long, String> answers = new HashMap<>();
        for (long i = 1; i <= 10; i++) {
            answers.put(i, "A"); // each gets 10 -> total 100
        }
        AssessmentSubmitRequest req = new AssessmentSubmitRequest();
        req.setAnswers(answers);

        AssessmentResultDto result = assessmentService.submitAssessment(1L, req);

        assertThat(result.getFinancialScore()).isEqualTo(100);
        assertThat(result.getLiteracyLevel()).isEqualTo("ADVANCED");
        // Question 7 option A mapped to Conservative
        assertThat(result.getRiskProfile()).isEqualTo("Conservative");
        assertThat(result.isAssessmentCompleted()).isTrue();
        assertThat(testUser.isAssessmentCompleted()).isTrue();
        assertThat(testUser.getFinancialScore()).isEqualTo(100);
    }

    @Test
    @DisplayName("Boundary test: Score of 39 should be BEGINNER, Score of 40 should be INTERMEDIATE")
    void testBoundaryScores_BeginnerVsIntermediate() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(questionRepository.findAllByActiveTrueOrderByQuestionNumberAsc()).thenReturn(mockQuestions);
        when(userAssessmentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Let's create an answer set that yields 39:
        // Q1=10, Q2=10, Q3=10 (30 pts). Q4..Q10 has 7 questions.
        // If 6 questions give 1 pt (6 pts) + 1 question gives 3 pt: 30 + 6 + 3 = 39 pts.
        // Let's configure mockQuestions so Q10 option C is 3 pts:
        mockQuestions.get(9).setScoreC(3);

        Map<Long, String> answers39 = new HashMap<>();
        answers39.put(1L, "A"); // 10
        answers39.put(2L, "A"); // 10
        answers39.put(3L, "A"); // 10
        answers39.put(4L, "D"); // 1
        answers39.put(5L, "D"); // 1
        answers39.put(6L, "D"); // 1
        answers39.put(7L, "D"); // 1 (Risk Question D = Aggressive)
        answers39.put(8L, "D"); // 1
        answers39.put(9L, "D"); // 1
        answers39.put(10L, "C"); // 3
        // Total = 10+10+10+1+1+1+1+1+1+3 = 39

        AssessmentSubmitRequest req39 = new AssessmentSubmitRequest();
        req39.setAnswers(answers39);
        AssessmentResultDto res39 = assessmentService.submitAssessment(1L, req39);

        assertThat(res39.getFinancialScore()).isEqualTo(39);
        assertThat(res39.getLiteracyLevel()).isEqualTo("BEGINNER");
        assertThat(res39.getRiskProfile()).isEqualTo("Aggressive"); // Q7 is D -> Aggressive

        // Change Q10 to B (7) -> Total = 30 + 6 + 7 = 43, or let's make total exactly 40:
        // Q10 was C(4), if Q9 is B(7) instead of D(1): 39 - 1 + 7 = 45.
        // To get 40: Q1=A(10), Q2=A(10), Q3=A(10), Q4=B(7), Q5..Q10=D(1) -> 30 + 7 + 6*1 = 43.
        // Let's adjust mock scores or use a question score to hit exactly 40:
        // 4 questions with A(10) and 6 questions with 0:
        mockQuestions.get(3).setScoreD(0);
        mockQuestions.get(4).setScoreD(0);
        mockQuestions.get(5).setScoreD(0);
        mockQuestions.get(6).setScoreD(0);
        mockQuestions.get(7).setScoreD(0);
        mockQuestions.get(8).setScoreD(0);
        mockQuestions.get(9).setScoreD(0);
        // Q1..Q4=A(10), Q5..Q10=D(0) -> exactly 40
        Map<Long, String> answers40 = new HashMap<>();
        answers40.put(1L, "A");
        answers40.put(2L, "A");
        answers40.put(3L, "A");
        answers40.put(4L, "A");
        answers40.put(5L, "D");
        answers40.put(6L, "D");
        answers40.put(7L, "D");
        answers40.put(8L, "D");
        answers40.put(9L, "D");
        answers40.put(10L, "D");

        AssessmentSubmitRequest req40 = new AssessmentSubmitRequest();
        req40.setAnswers(answers40);
        AssessmentResultDto res40 = assessmentService.submitAssessment(1L, req40);

        assertThat(res40.getFinancialScore()).isEqualTo(40);
        assertThat(res40.getLiteracyLevel()).isEqualTo("INTERMEDIATE");
    }

    @Test
    @DisplayName("Boundary test: Score of 69 should be INTERMEDIATE, Score of 70 should be ADVANCED")
    void testBoundaryScores_IntermediateVsAdvanced() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(questionRepository.findAllByActiveTrueOrderByQuestionNumberAsc()).thenReturn(mockQuestions);
        when(userAssessmentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // 6 questions with 10, 1 question with 9, 3 with 0 -> 69
        mockQuestions.get(6).setScoreB(9);
        for (int i = 7; i < 10; i++) {
            mockQuestions.get(i).setScoreD(0);
        }

        Map<Long, String> answers69 = new HashMap<>();
        for (long i = 1; i <= 6; i++) answers69.put(i, "A"); // 60
        answers69.put(7L, "B"); // 9
        answers69.put(8L, "D"); // 0
        answers69.put(9L, "D"); // 0
        answers69.put(10L, "D"); // 0

        AssessmentSubmitRequest req69 = new AssessmentSubmitRequest();
        req69.setAnswers(answers69);
        AssessmentResultDto res69 = assessmentService.submitAssessment(1L, req69);

        assertThat(res69.getFinancialScore()).isEqualTo(69);
        assertThat(res69.getLiteracyLevel()).isEqualTo("INTERMEDIATE");

        // 7 questions with 10, 3 with 0 -> 70
        Map<Long, String> answers70 = new HashMap<>(answers69);
        answers70.put(7L, "A"); // 10 instead of 9 -> Total 70

        AssessmentSubmitRequest req70 = new AssessmentSubmitRequest();
        req70.setAnswers(answers70);
        AssessmentResultDto res70 = assessmentService.submitAssessment(1L, req70);

        assertThat(res70.getFinancialScore()).isEqualTo(70);
        assertThat(res70.getLiteracyLevel()).isEqualTo("ADVANCED");
    }

    @Test
    @DisplayName("Submitting incomplete answers throws BadRequestException")
    void testSubmitAssessment_MissingAnswers_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(questionRepository.findAllByActiveTrueOrderByQuestionNumberAsc()).thenReturn(mockQuestions);

        Map<Long, String> incompleteAnswers = new HashMap<>();
        incompleteAnswers.put(1L, "A");
        incompleteAnswers.put(2L, "B");
        // missing questions 3 through 10

        AssessmentSubmitRequest req = new AssessmentSubmitRequest();
        req.setAnswers(incompleteAnswers);

        assertThatThrownBy(() -> assessmentService.submitAssessment(1L, req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Missing answer for question #3");
    }

    @Test
    @DisplayName("Submitting invalid option key throws BadRequestException")
    void testSubmitAssessment_InvalidOptionKey_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(questionRepository.findAllByActiveTrueOrderByQuestionNumberAsc()).thenReturn(mockQuestions);

        Map<Long, String> answers = new HashMap<>();
        for (long i = 1; i <= 9; i++) {
            answers.put(i, "A");
        }
        answers.put(10L, "Z"); // Invalid option

        AssessmentSubmitRequest req = new AssessmentSubmitRequest();
        req.setAnswers(answers);

        assertThatThrownBy(() -> assessmentService.submitAssessment(1L, req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid option selected");
    }
}
