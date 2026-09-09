package com.finquest.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "assessment_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_number", nullable = false, unique = true)
    private int questionNumber;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(name = "question_text", nullable = false, length = 500)
    private String questionText;

    @Column(name = "option_a", nullable = false, length = 255)
    private String optionA;

    @Column(name = "option_b", nullable = false, length = 255)
    private String optionB;

    @Column(name = "option_c", nullable = false, length = 255)
    private String optionC;

    @Column(name = "option_d", nullable = false, length = 255)
    private String optionD;

    @Column(name = "score_a", nullable = false)
    private int scoreA;

    @Column(name = "score_b", nullable = false)
    private int scoreB;

    @Column(name = "score_c", nullable = false)
    private int scoreC;

    @Column(name = "score_d", nullable = false)
    private int scoreD;

    @Column(name = "is_risk_question", nullable = false)
    private boolean isRiskQuestion = false;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public int getScoreForOption(String option) {
        if (option == null) return 0;
        return switch (option.toUpperCase().trim()) {
            case "A" -> scoreA;
            case "B" -> scoreB;
            case "C" -> scoreC;
            case "D" -> scoreD;
            default -> 0;
        };
    }
}
