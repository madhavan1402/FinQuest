package com.finquest.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_progress", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "learning_module_id"}))
@Data
public class UserProgress {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "user_id") private User user;
    @ManyToOne(optional = false) @JoinColumn(name = "learning_module_id") private LearningModule learningModule;
    @Column(nullable = false) private boolean unlocked;
    @Column(nullable = false) private boolean completed;

    // ── Learning Path 2.0 fields ────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LevelStatus status = LevelStatus.LOCKED;

    @Column(nullable = false)
    private int attempts = 0;

    @Column(nullable = false)
    private int bestScore = 0;

    @Column(nullable = false)
    private int lastScore = 0;

    @Column(nullable = false)
    private int correctAnswers = 0;

    @Column(nullable = false)
    private int questionsAnswered = 0;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime lastAttemptAt;
}
