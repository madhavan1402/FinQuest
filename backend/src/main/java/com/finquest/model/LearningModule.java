package com.finquest.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "learning_modules", uniqueConstraints = @UniqueConstraint(columnNames = "moduleKey"))
@Data
public class LearningModule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String moduleKey;
    @Column(nullable = false) private String title;
    @Column(nullable = false, unique = true) private int sequenceNumber;

    // ── Learning Path 2.0 fields ────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LearningTier tier = LearningTier.BEGINNER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Difficulty difficulty = Difficulty.EASY;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private int xpReward;

    @Column(nullable = false)
    private int coinReward = 25;

    @Column(nullable = false)
    private int estimatedMinutes = 10;

    @Column(nullable = false)
    private boolean active = true;
}
