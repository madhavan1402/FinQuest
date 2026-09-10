package com.finquest.controller;

import com.finquest.dto.market.HistoricalCandleDto;
import com.finquest.dto.market.MarketOverviewDto;
import com.finquest.dto.market.MarketQuoteDto;
import com.finquest.dto.market.MarketStatusDto;
import com.finquest.service.market.MarketDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
public class MarketController {

    private final MarketDataService marketDataService;

    @GetMapping("/overview")
    public ResponseEntity<MarketOverviewDto> getOverview() {
        return ResponseEntity.ok(marketDataService.getOverview());
    }

    @GetMapping("/quotes/{symbol}")
    public ResponseEntity<MarketQuoteDto> getQuote(@PathVariable String symbol) {
        return ResponseEntity.ok(marketDataService.getQuote(symbol));
    }

    @GetMapping("/history/{symbol}")
    public ResponseEntity<List<HistoricalCandleDto>> getHistory(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "1D") String timeframe
    ) {
        return ResponseEntity.ok(marketDataService.getHistoricalData(symbol, timeframe));
    }

    @GetMapping("/status")
    public ResponseEntity<MarketStatusDto> getStatus() {
        return ResponseEntity.ok(marketDataService.getMarketStatus());
    }
}
