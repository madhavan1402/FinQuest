package com.finquest.dto.trading;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request body for POST /api/trading/buy and POST /api/trading/sell.
 *
 * userId is intentionally absent — it is always derived from the JWT principal.
 * The frontend MUST NOT supply a userId.
 */
@Data
public class TradeRequest {

    @NotBlank(message = "Symbol is required")
    private String symbol;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}
