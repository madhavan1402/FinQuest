package com.finquest.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity @Table(name = "user_streaks") @Data
public class UserStreak {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(optional = false) @JoinColumn(name = "user_id", unique = true) private User user;
    @Column(nullable = false) private int currentStreak = 0;
    @Column(nullable = false) private int longestStreak = 0;
    private LocalDate lastActivityDate;
}
