package com.finquest.service.market;

import com.finquest.dto.market.HistoricalCandleDto;
import com.finquest.dto.market.MarketQuoteDto;
import com.finquest.dto.market.MarketStatusDto;

import java.util.List;

/**
 * Vendor-neutral interface for market data retrieval.
 * Implementations provide quote, historical, and exchange status data.
 */
public interface MarketDataProvider {
    String getProviderName();
    boolean isHealthy();
    MarketQuoteDto getQuote(String canonicalSymbol);
    List<MarketQuoteDto> getQuotes(List<String> canonicalSymbols);
    List<HistoricalCandleDto> getHistoricalData(String canonicalSymbol, String timeframe);
    MarketStatusDto getMarketStatus();
}
