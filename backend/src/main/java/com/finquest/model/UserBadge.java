package com.finquest.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_badges",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "badge_code"}))
@Data
@NoArgsConstructor
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String badgeCode;

    @Column(nullable = false, length = 100)
    private String badgeName;

    @Column(nullable = false, length = 10)
    private String icon = "🏅";

    @Column(nullable = false)
    private LocalDateTime unlockedAt = LocalDateTime.now();
}
