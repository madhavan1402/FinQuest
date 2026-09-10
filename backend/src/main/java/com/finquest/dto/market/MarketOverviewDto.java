package com.finquest.dto.market;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketOverviewDto {
    private MarketStatusDto marketStatus;
    private List<MarketQuoteDto> benchmarks;
    private List<MarketQuoteDto> watchlist;
    private String dataSource;
    private boolean isSimulated;
}
