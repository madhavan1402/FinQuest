package com.finquest.service;

import com.finquest.dto.market.HistoricalCandleDto;
import com.finquest.dto.market.MarketOverviewDto;
import com.finquest.dto.market.MarketQuoteDto;
import com.finquest.dto.market.MarketStatusDto;
import com.finquest.exception.BadRequestException;
import com.finquest.exception.ResourceNotFoundException;
import com.finquest.service.market.MarketDataService;
import com.finquest.service.market.MockMarketDataProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MarketDataServiceTest {

    private MarketDataService marketDataService;
    private MockMarketDataProvider mockProvider;

    @BeforeEach
    void setUp() {
        mockProvider = new MockMarketDataProvider();
        marketDataService = new MarketDataService(mockProvider);
        marketDataService.clearCache();
    }

    @Test
    void testGetValidQuote() {
        MarketQuoteDto quote = marketDataService.getQuote("nifty50");
        assertNotNull(quote);
        assertEquals("NIFTY50", quote.getSymbol());
        assertEquals("NIFTY 50", quote.getDisplayName());
        assertEquals("NSE", quote.getExchange());
        assertTrue(quote.getCurrentPrice() > 0);
        assertTrue(quote.isSimulated());
        assertEquals("MOCK", quote.getDataSource());
        assertNotNull(quote.getStatus());
    }

    @Test
    void testSymbolNormalization() {
        MarketQuoteDto quote = marketDataService.getQuote("  reliance  ");
        assertNotNull(quote);
        assertEquals("RELIANCE", quote.getSymbol());
    }

    @Test
    void testInvalidSymbolThrowsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> marketDataService.getQuote("INVALID_TICKER"));
    }

    @Test
    void testBlankOrMalformedSymbolThrowsBadRequest() {
        assertThrows(BadRequestException.class, () -> marketDataService.getQuote("   "));
        assertThrows(BadRequestException.class, () -> marketDataService.getQuote("BAD$YMBOL!"));
    }

    @Test
    void testGetOverview() {
        MarketOverviewDto overview = marketDataService.getOverview();
        assertNotNull(overview);
        assertNotNull(overview.getMarketStatus());
        assertNotNull(overview.getBenchmarks());
        assertFalse(overview.getBenchmarks().isEmpty());
        assertNotNull(overview.getWatchlist());
        assertFalse(overview.getWatchlist().isEmpty());
        assertTrue(overview.isSimulated());
        assertEquals("MOCK", overview.getDataSource());
    }

    @Test
    void testGetHistoricalDataValid() {
        List<HistoricalCandleDto> candles = marketDataService.getHistoricalData("SENSEX", "1D");
        assertNotNull(candles);
        assertFalse(candles.isEmpty());
        HistoricalCandleDto first = candles.get(0);
        assertTrue(first.getOpen() > 0);
        assertTrue(first.getHigh() >= first.getLow());
    }

    @Test
    void testGetHistoricalDataInvalidTimeframe() {
        assertThrows(BadRequestException.class, () -> marketDataService.getHistoricalData("NIFTY50", "99Y"));
    }

    @Test
    void testMarketStatusCalculation() {
        MarketStatusDto status = marketDataService.getMarketStatus();
        assertNotNull(status);
        assertEquals("Asia/Kolkata", status.getTimezone());
        assertTrue(status.isProviderLive());
        assertTrue(status.isSimulated());
        assertEquals("MOCK", status.getDataSource());
        assertNotNull(status.getStatus());
    }

    @Test
    void testCachingBehavior() {
        MarketQuoteDto firstCall = marketDataService.getQuote("TCS");
        MarketQuoteDto secondCall = marketDataService.getQuote("TCS");
        assertSame(firstCall, secondCall, "Second call should return cached object within TTL");
    }
}
