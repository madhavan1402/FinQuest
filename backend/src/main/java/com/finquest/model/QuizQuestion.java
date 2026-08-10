package com.finquest.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Stores one multiple-choice question belonging to a specific game level.
// Questions are fetched by level so the frontend shows the right quiz per stage.
@Entity
@Table(name = "quiz_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String question;

    // Four answer options sent to the frontend as-is
    @Column(name = "option_a", nullable = false, length = 200)
    private String optionA;

    @Column(name = "option_b", nullable = false, length = 200)
    private String optionB;

    @Column(name = "option_c", nullable = false, length = 200)
    private String optionC;

    @Column(name = "option_d", nullable = false, length = 200)
    private String optionD;

    // Stores "A", "B", "C", or "D" — compared against the user's submitted answer
    @Column(name = "correct_answer", nullable = false, length = 1)
    private String correctAnswer;

<<<<<<< HEAD
// Links the question to a game level (1, 2, 3 …) — the learning level number
    @Column(nullable = false)
    private int level;

    // ── Learning Path 2.0 fields ────────────────────────────────────────────
    @Column(length = 100)
    private String topic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Difficulty difficulty = Difficulty.EASY;

    // Extensible question type — initially MCQ only; TRUE_FALSE, SCENARIO,
    // CALCULATION, MULTI_SELECT will be added in later phases.
    @Column(nullable = false, length = 20)
    private String questionType = "MCQ";

    // Explanation shown AFTER submission (never leaked to the frontend before).
    @Column(length = 500)
    private String explanation;

    @Column(nullable = false)
    private boolean active = true;
=======
    // Links the question to a game level (1, 2, 3 …)
    // GET /api/quiz/{level} filters by this field
    @Column(nullable = false)
    private int level;
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
}
