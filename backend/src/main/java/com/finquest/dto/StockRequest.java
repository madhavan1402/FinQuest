package com.finquest.dto;

import lombok.Data;
import java.util.List;

// Request body for POST /api/simulation/stock
// decisions = ordered list of "BUY" or "SELL" actions on virtual stock
// { "userId": 1, "decisions": ["BUY", "BUY", "SELL", "BUY", "SELL"] }
@Data
public class StockRequest {
    private Long userId;
    private List<String> decisions;
}
