package com.finquest.dto.trading;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction history item returned by GET /api/trading/transactions.
 */
@Data
@Builder
public class TransactionDto {
    private Long id;
    private String symbol;
    private String transactionType; // "BUY" | "SELL"
    private int quantity;
    private BigDecimal executionPrice;
    private BigDecimal totalValue;
    private BigDecimal realizedProfitLoss; // null for BUY
    private LocalDateTime timestamp;
}
