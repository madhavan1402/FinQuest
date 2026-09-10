package com.finquest.dto.trading;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Complete portfolio summary returned by GET /api/trading/portfolio.
 * All monetary values computed on the backend. Frontend must not recalculate.
 */
@Data
@Builder
public class PortfolioDto {
    private BigDecimal cashBalance;
    private BigDecimal totalMarketValue;     // sum of all holding market values
    private BigDecimal portfolioValue;       // cashBalance + totalMarketValue
    private BigDecimal totalCostBasis;       // sum of all holding cost bases
    private BigDecimal totalUnrealizedPnl;  // totalMarketValue - totalCostBasis
    private BigDecimal realizedPnl;         // sum of SELL transaction realizedProfitLoss
    private BigDecimal totalPnl;            // realizedPnl + totalUnrealizedPnl
    private List<HoldingDto> holdings;
    private boolean simulated;              // always true
    private String disclaimer;             // educational disclaimer text
}
