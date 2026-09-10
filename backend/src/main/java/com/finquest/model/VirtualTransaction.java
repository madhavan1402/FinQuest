package com.finquest.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Immutable ledger entry for every executed virtual trade.
 * Maps to the {@code virtual_transactions} table (V9 migration).
 *
 * BUY rows:  realizedProfitLoss is null.
 * SELL rows: realizedProfitLoss = (executionPrice - holdingAvgBuyPrice) × quantity
 */
@Entity
@Table(name = "virtual_transactions")
@Data
@NoArgsConstructor
public class VirtualTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK → users.id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Canonical uppercase symbol, e.g. "TCS"
    @Column(nullable = false, length = 20)
    private String symbol;

    // "BUY" or "SELL" — 4 chars maximum.
    @Column(nullable = false, length = 4)
    private String transactionType;

    // Number of shares in this trade.
    @Column(nullable = false)
    private int quantity;

    // Phase 6 market price at time of execution — DECIMAL(15,4).
    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal executionPrice;

    // quantity × executionPrice — DECIMAL(15,2).
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalValue;

    // Populated for SELL: (executionPrice - avgBuyPrice) × quantity.
    // Null for BUY.
    @Column(precision = 15, scale = 2)
    private BigDecimal realizedProfitLoss;

    // Timestamp of the trade — set at persist time.
    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    private void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
