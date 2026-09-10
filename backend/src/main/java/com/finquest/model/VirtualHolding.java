package com.finquest.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Virtual stock holding — one row per (user, symbol) pair.
 * Maps to the {@code virtual_holdings} table (V9 migration).
 *
 * quantity is always >= 1; the row is deleted when it reaches zero.
 * averageBuyPrice is the weighted-average cost basis — updated on every BUY,
 * unchanged on SELL (sell realises P/L against this price).
 */
@Entity
@Table(name = "virtual_holdings")
@Data
@NoArgsConstructor
public class VirtualHolding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK → users.id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Canonical uppercase symbol, e.g. "RELIANCE"
    @Column(nullable = false, length = 20)
    private String symbol;

    // Number of shares held — always >= 1.
    @Column(nullable = false)
    private int quantity;

    // DECIMAL(15,4) — weighted average purchase price across all BUY trades.
    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal averageBuyPrice;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    private void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
