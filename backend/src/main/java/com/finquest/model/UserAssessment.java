package com.finquest.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_assessments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "financial_score", nullable = false)
    private int financialScore;

    @Column(name = "literacy_level", nullable = false, length = 20)
    private String literacyLevel;

    @Column(name = "risk_profile", nullable = false, length = 30)
    private String riskProfile;

    @Column(nullable = false, length = 500)
    private String recommendation;

    @Column(name = "answers_json", nullable = false, columnDefinition = "TEXT")
    private String answersJson;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt = LocalDateTime.now();
}
