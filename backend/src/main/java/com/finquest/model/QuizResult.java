package com.finquest.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Records the outcome of one quiz attempt by a user.
// Stored so history, leaderboards, and progress tracking can be built later.
@Entity
@Table(name = "quiz_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign key → users.id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Which level's quiz was attempted
    @Column(nullable = false)
    private int level;

    // Number of correct answers out of total questions
    @Column(nullable = false)
    private int score;
}
