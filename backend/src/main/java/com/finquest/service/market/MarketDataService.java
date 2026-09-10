package com.finquest.service.market;

import com.finquest.dto.market.*;
import com.finquest.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * MarketDataService coordinates market data caching, canonical symbol normalization,
 * and retrieval from the active MarketDataProvider.
 */
@Service
@Slf4j
public class MarketDataService {

    private static final Pattern SYMBOL_PATTERN = Pattern.compile("^[A-Z0-9_]{1,20}$");
    private static final List<String> DEFAULT_BENCHMARKS = List.of("NIFTY50", "SENSEX");
    private static final List<String> DEFAULT_WATCHLIST = List.of("RELIANCE", "TCS", "HDFCBANK", "INFY", "ICICIBANK");

    private final MarketDataProvider provider;

    @Value("${market.data.enabled:true}")
    private boolean enabled;

    @Value("${market.data.cache-ttl-seconds:30}")
    private int cacheTtlSeconds;

    // In-memory cache structures with TTL timestamps
    private record CacheEntry<T>(T data, Instant expiresAt) {
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }

    private final Map<String, CacheEntry<MarketQuoteDto>> quoteCache = new ConcurrentHashMap<>();
    private final Map<String, CacheEntry<List<HistoricalCandleDto>>> historyCache = new ConcurrentHashMap<>();
    private volatile CacheEntry<MarketStatusDto> statusCache;

    @Autowired
    public MarketDataService(@Qualifier("mockMarketDataProvider") MarketDataProvider provider) {
        this.provider = provider;
    }

    public String normalizeSymbol(String rawSymbol) {
        if (rawSymbol == null || rawSymbol.trim().isBlank()) {
            throw new BadRequestException("Symbol parameter is required and cannot be blank.");
        }
        String normalized = rawSymbol.trim().toUpperCase();
        if (!SYMBOL_PATTERN.matcher(normalized).matches()) {
            throw new BadRequestException("Invalid symbol format: " + rawSymbol + ". Allowed alphanumeric up to 20 chars.");
        }
        return normalized;
    }

    public MarketQuoteDto getQuote(String rawSymbol) {
        String symbol = normalizeSymbol(rawSymbol);

        CacheEntry<MarketQuoteDto> cached = quoteCache.get(symbol);
        if (cached != null && !cached.isExpired()) {
            return cached.data();
        }

        try {
            MarketQuoteDto quote = provider.getQuote(symbol);
            quoteCache.put(symbol, new CacheEntry<>(quote, Instant.now().plusSeconds(cacheTtlSeconds)));
            return quote;
        } catch (Exception e) {
            log.warn("Error fetching quote for symbol {}: {}", symbol, e.getMessage());
            if (cached != null) {
                log.info("Serving stale cached quote for {}", symbol);
                return cached.data();
            }
            throw e;
        }
    }

    public List<MarketQuoteDto> getQuotes(List<String> rawSymbols) {
        if (rawSymbols == null || rawSymbols.isEmpty()) {
            return Collections.emptyList();
        }
        List<MarketQuoteDto> results = new ArrayList<>();
        for (String raw : rawSymbols) {
            try {
                results.add(getQuote(raw));
            } catch (Exception e) {
                log.debug("Skipping symbol {} due to: {}", raw, e.getMessage());
            }
        }
        return results;
    }

    public List<HistoricalCandleDto> getHistoricalData(String rawSymbol, String timeframe) {
        String symbol = normalizeSymbol(rawSymbol);
        String tf = (timeframe != null) ? timeframe.trim().toUpperCase() : "1D";
        String cacheKey = symbol + ":" + tf;

        CacheEntry<List<HistoricalCandleDto>> cached = historyCache.get(cacheKey);
        // Historical data cached for 15 minutes (or 10x base TTL)
        int historyTtl = Math.max(cacheTtlSeconds * 10, 900);
        if (cached != null && !cached.isExpired()) {
            return cached.data();
        }

        try {
            List<HistoricalCandleDto> history = provider.getHistoricalData(symbol, tf);
            historyCache.put(cacheKey, new CacheEntry<>(history, Instant.now().plusSeconds(historyTtl)));
            return history;
        } catch (Exception e) {
            log.warn("Error fetching history for {} [{}]: {}", symbol, tf, e.getMessage());
            if (cached != null) {
                return cached.data();
            }
            throw e;
        }
    }

    public MarketStatusDto getMarketStatus() {
        CacheEntry<MarketStatusDto> cached = statusCache;
        if (cached != null && !cached.isExpired()) {
            return cached.data();
        }

        try {
            MarketStatusDto status = provider.getMarketStatus();
            statusCache = new CacheEntry<>(status, Instant.now().plusSeconds(60));
            return status;
        } catch (Exception e) {
            log.warn("Error fetching market status: {}", e.getMessage());
            if (cached != null) {
                return cached.data();
            }
            throw e;
        }
    }

    public MarketOverviewDto getOverview() {
        MarketStatusDto status = getMarketStatus();
        List<MarketQuoteDto> benchmarks = getQuotes(DEFAULT_BENCHMARKS);
        List<MarketQuoteDto> watchlist = getQuotes(DEFAULT_WATCHLIST);

        boolean isSimulated = benchmarks.stream().anyMatch(MarketQuoteDto::isSimulated);
        String dataSource = benchmarks.isEmpty() ? "MOCK" : benchmarks.get(0).getDataSource();

        return MarketOverviewDto.builder()
                .marketStatus(status)
                .benchmarks(benchmarks)
                .watchlist(watchlist)
                .dataSource(dataSource)
                .isSimulated(isSimulated)
                .build();
    }

    public void clearCache() {
        quoteCache.clear();
        historyCache.clear();
        statusCache = null;
    }
}
