package com.finquest.dto.trading;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response for GET /api/trading/wallet.
 * No userId, no internal fields exposed.
 */
@Data
@Builder
public class WalletDto {
    private BigDecimal cashBalance;
    private BigDecimal initialBalance;
    private LocalDateTime updatedAt;
}
