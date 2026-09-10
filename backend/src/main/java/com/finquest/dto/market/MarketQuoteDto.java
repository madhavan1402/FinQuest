package com.finquest.dto.market;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketQuoteDto {
    private String symbol;
    private String displayName;
    private String exchange;
    private double currentPrice;
    private double previousClose;
    private double change;
    private double changePercent;
    private double open;
    private double high;
    private double low;
    private long volume;
    private String currency;
    private Instant timestamp;
    private MarketStatus status;
    private String dataSource;
    private boolean isSimulated;
}
