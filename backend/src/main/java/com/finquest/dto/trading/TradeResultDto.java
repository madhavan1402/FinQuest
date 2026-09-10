package com.finquest.dto.trading;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Result returned upon successful execution of POST /api/trading/buy or POST /api/trading/sell.
 */
@Data
@Builder
public class TradeResultDto {
    private String status;
    private String symbol;
    private int quantity;
    private BigDecimal executionPrice;
    private BigDecimal totalValue;
    private BigDecimal cashBalanceAfter;
    private BigDecimal realizedProfitLoss; // populated on SELL only
    private boolean simulated;
    private String disclaimer;
}
