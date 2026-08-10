package com.finquest.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity @Table(name = "xp_history") @Data
public class XPHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) private User user;
    @Column(nullable = false) private int amount;
    @Column(nullable = false) private String reason;
    @Column(nullable = false) private LocalDateTime awardedAt = LocalDateTime.now();
}
