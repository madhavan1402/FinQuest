package com.finquest.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "achievements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many achievements can belong to one user
    // @ManyToOne creates a foreign key column 'user_id' in the achievements table
    // FetchType.LAZY = user data is only loaded from DB when explicitly accessed (better performance)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // The name of the badge awarded, e.g. "First Steps", "Level 5 Master"
    @Column(name = "badge_name", nullable = false, length = 100)
    private String badgeName;
}
