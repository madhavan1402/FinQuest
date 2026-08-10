package com.finquest.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "achievement_definitions")
@Data
@NoArgsConstructor
public class AchievementDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false, length = 10)
    private String icon = "🏅";

    @Column(nullable = false)
    private int xpReward = 0;

    @Column(nullable = false)
    private int coinReward = 0;
}
