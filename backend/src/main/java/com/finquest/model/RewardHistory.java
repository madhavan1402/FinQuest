package com.finquest.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity @Table(name = "reward_history") @Data
public class RewardHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "user_id") private User user;
    @Column(nullable = false, length = 40) private String rewardType;
    @Column(nullable = false, length = 180) private String description;
    @Column(nullable = false) private int coins;
    @Column(nullable = false) private int xp;
    @Column(nullable = false) private LocalDateTime earnedAt = LocalDateTime.now();
}
