package com.finquest.service.market;

import com.finquest.dto.market.HistoricalCandleDto;
import com.finquest.dto.market.MarketQuoteDto;
import com.finquest.dto.market.MarketStatus;
import com.finquest.dto.market.MarketStatusDto;
import com.finquest.exception.BadRequestException;
import com.finquest.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * MockMarketDataProvider generates realistic, deterministic simulated market data.
 * All responses clearly flag dataSource="MOCK" and isSimulated=true.
 */
@Component("mockMarketDataProvider")
@Slf4j
public class MockMarketDataProvider implements MarketDataProvider {

    public static final String DATA_SOURCE = "MOCK";
    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

    private record InstrumentInfo(String symbol, String displayName, String exchange, double basePrice, long baseVolume) {}

    private static final Map<String, InstrumentInfo> INSTRUMENTS = new LinkedHashMap<>();

    static {
        INSTRUMENTS.put("NIFTY50", new InstrumentInfo("NIFTY50", "NIFTY 50", "NSE", 24850.50, 245000000L));
        INSTRUMENTS.put("SENSEX", new InstrumentInfo("SENSEX", "BSE SENSEX", "BSE", 81320.75, 185000000L));
        INSTRUMENTS.put("RELIANCE", new InstrumentInfo("RELIANCE", "Reliance Industries Ltd", "NSE", 2980.40, 6800000L));
        INSTRUMENTS.put("TCS", new InstrumentInfo("TCS", "Tata Consultancy Services Ltd", "NSE", 4210.80, 2400000L));
        INSTRUMENTS.put("HDFCBANK", new InstrumentInfo("HDFCBANK", "HDFC Bank Ltd", "NSE", 1640.25, 11200000L));
        INSTRUMENTS.put("INFY", new InstrumentInfo("INFY", "Infosys Ltd", "NSE", 1875.60, 5300000L));
        INSTRUMENTS.put("ICICIBANK", new InstrumentInfo("ICICIBANK", "ICICI Bank Ltd", "NSE", 1210.15, 8900000L));
    }

    public static Set<String> getSupportedSymbols() {
        return Collections.unmodifiableSet(INSTRUMENTS.keySet());
    }

    @Override
    public String getProviderName() {
        return "MockMarketDataProvider";
    }

    @Override
    public boolean isHealthy() {
        return true;
    }

    @Override
    public MarketQuoteDto getQuote(String canonicalSymbol) {
        InstrumentInfo info = INSTRUMENTS.get(canonicalSymbol);
        if (info == null) {
            throw new ResourceNotFoundException("Instrument not found with symbol: " + canonicalSymbol);
        }

        // Deterministic offset based on symbol hash code
        int hash = Math.abs(canonicalSymbol.hashCode());
        double dayVariationPercent = ((hash % 200) - 90) / 100.0; // range approx -0.90% to +1.10%
        double change = Math.round((info.basePrice * (dayVariationPercent / 100.0)) * 100.0) / 100.0;
        double currentPrice = Math.round((info.basePrice + change) * 100.0) / 100.0;
        double previousClose = info.basePrice;
        double changePercent = Math.round(((change / previousClose) * 100.0) * 100.0) / 100.0;

        double high = Math.round((Math.max(currentPrice, previousClose) + (Math.abs(change) * 0.45) + 2.5) * 100.0) / 100.0;
        double low = Math.round((Math.min(currentPrice, previousClose) - (Math.abs(change) * 0.35) - 2.0) * 100.0) / 100.0;
        double open = Math.round((previousClose + (change * 0.3)) * 100.0) / 100.0;

        MarketStatus status = evaluateMarketStatus(ZonedDateTime.now(IST_ZONE));

        return MarketQuoteDto.builder()
                .symbol(info.symbol)
                .displayName(info.displayName)
                .exchange(info.exchange)
                .currentPrice(currentPrice)
                .previousClose(previousClose)
                .change(change)
                .changePercent(changePercent)
                .open(open)
                .high(high)
                .low(low)
                .volume(info.baseVolume)
                .currency("INR")
                .timestamp(Instant.now())
                .status(status)
                .dataSource(DATA_SOURCE)
                .isSimulated(true)
                .build();
    }

    @Override
    public List<MarketQuoteDto> getQuotes(List<String> canonicalSymbols) {
        List<MarketQuoteDto> quotes = new ArrayList<>();
        for (String sym : canonicalSymbols) {
            if (INSTRUMENTS.containsKey(sym)) {
                quotes.add(getQuote(sym));
            }
        }
        return quotes;
    }

    @Override
    public List<HistoricalCandleDto> getHistoricalData(String canonicalSymbol, String timeframe) {
        InstrumentInfo info = INSTRUMENTS.get(canonicalSymbol);
        if (info == null) {
            throw new ResourceNotFoundException("Instrument not found with symbol: " + canonicalSymbol);
        }

        String tf = (timeframe != null) ? timeframe.trim().toUpperCase() : "1D";
        int candleCount;
        Duration step;

        switch (tf) {
            case "1D" -> { candleCount = 40; step = Duration.ofMinutes(10); }
            case "5D" -> { candleCount = 35; step = Duration.ofHours(1); }
            case "1M" -> { candleCount = 30; step = Duration.ofDays(1); }
            case "6M" -> { candleCount = 26; step = Duration.ofDays(7); }
            case "1Y" -> { candleCount = 52; step = Duration.ofDays(7); }
            default -> throw new BadRequestException("Unsupported timeframe '" + timeframe + "'. Allowed: 1D, 5D, 1M, 6M, 1Y");
        }

        List<HistoricalCandleDto> candles = new ArrayList<>();
        Instant end = Instant.now().truncatedTo(ChronoUnit.MINUTES);
        Instant start = end.minus(step.multipliedBy(candleCount));

        double current = info.basePrice * 0.96;
        int symSeed = Math.abs(canonicalSymbol.hashCode());

        for (int i = 0; i < candleCount; i++) {
            Instant candleTime = start.plus(step.multipliedBy(i + 1));
            // Deterministic wave function based on index and seed
            double wave = Math.sin((i + (symSeed % 10)) * 0.35) * (info.basePrice * 0.008);
            double open = Math.round(current * 100.0) / 100.0;
            double close = Math.round((open + wave) * 100.0) / 100.0;
            double high = Math.round((Math.max(open, close) + Math.abs(wave * 0.5) + 1.0) * 100.0) / 100.0;
            double low = Math.round((Math.min(open, close) - Math.abs(wave * 0.5) - 1.0) * 100.0) / 100.0;
            long volume = info.baseVolume / 80 + ((i % 5) * 15000L);

            candles.add(HistoricalCandleDto.builder()
                    .timestamp(candleTime)
                    .open(open)
                    .high(high)
                    .low(low)
                    .close(close)
                    .volume(volume)
                    .build());

            current = close;
        }

        return candles;
    }

    @Override
    public MarketStatusDto getMarketStatus() {
        ZonedDateTime nowIst = ZonedDateTime.now(IST_ZONE);
        MarketStatus status = evaluateMarketStatus(nowIst);

        String message;
        Instant nextOpen = calculateNextOpen(nowIst);
        Instant nextClose = calculateNextClose(nowIst);

        switch (status) {
            case OPEN -> message = "Normal trading session is active (NSE/BSE 09:15 - 15:30 IST).";
            case PRE_OPEN -> message = "Pre-open order collection session is active (09:00 - 09:15 IST).";
            case CLOSED -> message = "Indian markets are currently closed. Opens at 09:15 AM IST next business day.";
            case HOLIDAY -> message = "Exchange holiday today. Regular trading resumes next trading session.";
            default -> message = "Market status indeterminate.";
        }

        return MarketStatusDto.builder()
                .exchange("NSE/BSE")
                .status(status)
                .message(message)
                .nextOpenTime(nextOpen)
                .nextCloseTime(nextClose)
                .timezone("Asia/Kolkata")
                .providerLive(true)
                .dataSource(DATA_SOURCE)
                .isSimulated(true)
                .build();
    }

    private MarketStatus evaluateMarketStatus(ZonedDateTime nowIst) {
        DayOfWeek day = nowIst.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return MarketStatus.CLOSED;
        }

        LocalTime time = nowIst.toLocalTime();
        LocalTime preOpenStart = LocalTime.of(9, 0);
        LocalTime marketOpen = LocalTime.of(9, 15);
        LocalTime marketClose = LocalTime.of(15, 30);

        if (time.isAfter(preOpenStart) && time.isBefore(marketOpen)) {
            return MarketStatus.PRE_OPEN;
        }
        if (!time.isBefore(marketOpen) && time.isBefore(marketClose)) {
            return MarketStatus.OPEN;
        }
        return MarketStatus.CLOSED;
    }

    private Instant calculateNextOpen(ZonedDateTime nowIst) {
        ZonedDateTime target = nowIst.withHour(9).withMinute(15).withSecond(0).withNano(0);
        if (nowIst.toLocalTime().isAfter(LocalTime.of(15, 30)) || nowIst.toLocalTime().equals(LocalTime.of(15, 30))) {
            target = target.plusDays(1);
        }
        while (target.getDayOfWeek() == DayOfWeek.SATURDAY || target.getDayOfWeek() == DayOfWeek.SUNDAY) {
            target = target.plusDays(1);
        }
        return target.toInstant();
    }

    private Instant calculateNextClose(ZonedDateTime nowIst) {
        ZonedDateTime target = nowIst.withHour(15).withMinute(30).withSecond(0).withNano(0);
        if (nowIst.toLocalTime().isAfter(LocalTime.of(15, 30))) {
            target = target.plusDays(1);
        }
        while (target.getDayOfWeek() == DayOfWeek.SATURDAY || target.getDayOfWeek() == DayOfWeek.SUNDAY) {
            target = target.plusDays(1);
        }
        return target.toInstant();
    }
}
