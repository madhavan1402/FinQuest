package com.finquest.dto.trading;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * One holding entry returned by GET /api/trading/holdings and embedded in PortfolioDto.
 * Backend computes all monetary values — frontend receives final numbers only.
 */
@Data
@Builder
public class HoldingDto {
    private String symbol;
    private int quantity;
    private BigDecimal averageBuyPrice;
    private BigDecimal currentPrice;           // live Phase 6 simulated price
    private BigDecimal marketValue;            // quantity × currentPrice
    private BigDecimal costBasis;             // quantity × averageBuyPrice
    private BigDecimal unrealizedPnl;         // marketValue - costBasis
    private BigDecimal unrealizedPnlPercent;  // unrealizedPnl / costBasis × 100
    private String dataSource;               // always "MOCK"
    private boolean simulated;               // always true
}
