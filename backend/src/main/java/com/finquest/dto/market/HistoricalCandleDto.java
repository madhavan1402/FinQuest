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
public class HistoricalCandleDto {
    private Instant timestamp;
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;
}
