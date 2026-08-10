package com.finquest.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Persists the outcome of every simulation run (budget, stock, or tax).
// One row per simulation attempt — allows history and trend analysis later.
@Entity
@Table(name = "simulations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Simulation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign key → users.id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // BUDGET | STOCK | TAX — stored as readable string, not an integer
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SimulationType type;

    // Human-readable summary of the outcome, e.g. "Savings: ₹15000, Score: 75"
    @Column(nullable = false, length = 500)
    private String result;

    // Numeric score (0–100) used to calculate XP awarded
    @Column(nullable = false)
    private int score;
}
