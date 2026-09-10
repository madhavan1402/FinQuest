package com.finquest.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Virtual cash wallet — one per user, created lazily on first trading request.
 * Maps to the {@code virtual_wallets} table (V9 migration).
 *
 * cashBalance  — available virtual cash, adjusted on every trade.
 * initialBalance — always ₹100,000.00, displayed for reference.
 */
@Entity
@Table(name = "virtual_wallets")
@Data
@NoArgsConstructor
public class VirtualWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK → users.id — enforced at DB level; loaded lazily to avoid unnecessary joins.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // DECIMAL(15,2) — current available cash after all trades.
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal cashBalance = new BigDecimal("100000.00");

    // DECIMAL(15,2) — always the starting balance (₹100,000). Never changes.
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal initialBalance = new BigDecimal("100000.00");

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
