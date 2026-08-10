package com.finquest.model;

import jakarta.persistence.*;
<<<<<<< HEAD
=======
import lombok.AllArgsConstructor;
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "achievements")
@Data
@NoArgsConstructor
<<<<<<< HEAD
=======
@AllArgsConstructor
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<<<<<<< HEAD
=======
    // Many achievements can belong to one user
    // @ManyToOne creates a foreign key column 'user_id' in the achievements table
    // FetchType.LAZY = user data is only loaded from DB when explicitly accessed (better performance)
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

<<<<<<< HEAD
=======
    // The name of the badge awarded, e.g. "First Steps", "Level 5 Master"
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
    @Column(name = "badge_name", nullable = false, length = 100)
    private String badgeName;
}
