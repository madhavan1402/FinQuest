package com.finquest.service;

import com.finquest.dto.market.MarketQuoteDto;
import com.finquest.dto.trading.*;
import com.finquest.exception.BadRequestException;
import com.finquest.exception.BusinessRuleException;
import com.finquest.exception.ResourceNotFoundException;
import com.finquest.model.User;
import com.finquest.model.VirtualHolding;
import com.finquest.model.VirtualTransaction;
import com.finquest.model.VirtualWallet;
import com.finquest.repository.UserRepository;
import com.finquest.repository.VirtualHoldingRepository;
import com.finquest.repository.VirtualTransactionRepository;
import com.finquest.repository.VirtualWalletRepository;
import com.finquest.service.market.MarketDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class TradingService {

    public static final Set<String> TRADEABLE_SYMBOLS = Set.of(
            "RELIANCE", "TCS", "HDFCBANK", "INFY", "ICICIBANK"
    );

    public static final String EDUCATIONAL_DISCLAIMER =
            "Virtual Trading — Educational simulation only. No real money is involved.";

    private final VirtualWalletRepository walletRepository;
    private final VirtualHoldingRepository holdingRepository;
    private final VirtualTransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final MarketDataService marketDataService;

    /**
     * Initializes or gets the virtual wallet in a concurrency-safe manner.
     */
    @Transactional
    public VirtualWallet getOrCreateWallet(Long userId) {
        // Attempt atomic insert ignore in case it doesn't exist
        walletRepository.insertIgnoreForUser(userId);
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found or wallet creation failed for id: " + userId));
    }

    /**
     * View of the wallet (creates lazily on first access if missing).
     */
    @Transactional
    public WalletDto getWallet(Long userId) {
        VirtualWallet wallet = getOrCreateWallet(userId);
        return WalletDto.builder()
                .cashBalance(wallet.getCashBalance().setScale(2, RoundingMode.HALF_UP))
                .initialBalance(wallet.getInitialBalance().setScale(2, RoundingMode.HALF_UP))
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }

    /**
     * Execute a simulated stock BUY order.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TradeResultDto buy(Long userId, TradeRequest request) {
        String symbol = validateAndNormalizeTradeableSymbol(request.getSymbol());
        int quantity = request.getQuantity();
        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero.");
        }

        // 1. Fetch current price once from Phase 6 MarketDataService
        BigDecimal currentPrice = fetchMarketPrice(symbol);

        // 2. Ensure wallet exists and lock it pessimistically
        getOrCreateWallet(userId);
        VirtualWallet wallet = walletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user id: " + userId));

        // 3. Calculate total cost = quantity * currentPrice
        BigDecimal totalCost = currentPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);

        // 4. Verify sufficient cash
        if (wallet.getCashBalance().compareTo(totalCost) < 0) {
            throw new BusinessRuleException(String.format(
                    "Insufficient funds: Required ₹%s, Available ₹%s",
                    totalCost.toPlainString(), wallet.getCashBalance().setScale(2, RoundingMode.HALF_UP).toPlainString()
            ));
        }

        // 5. Deduct cash
        wallet.setCashBalance(wallet.getCashBalance().subtract(totalCost).setScale(2, RoundingMode.HALF_UP));
        walletRepository.save(wallet);

        // 6. Update or create holding
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        VirtualHolding holding = holdingRepository.findByUserIdAndSymbol(userId, symbol).orElse(null);
        if (holding == null) {
            holding = new VirtualHolding();
            holding.setUser(user);
            holding.setSymbol(symbol);
            holding.setQuantity(quantity);
            holding.setAverageBuyPrice(currentPrice.setScale(4, RoundingMode.HALF_UP));
        } else {
            int oldQty = holding.getQuantity();
            BigDecimal oldAvg = holding.getAverageBuyPrice();
            int newQty = oldQty + quantity;

            // Weighted average: (oldQty * oldAvg + buyQty * currentPrice) / newQty
            BigDecimal totalOldBasis = oldAvg.multiply(BigDecimal.valueOf(oldQty));
            BigDecimal totalNewBasis = currentPrice.multiply(BigDecimal.valueOf(quantity));
            BigDecimal newAvg = totalOldBasis.add(totalNewBasis)
                    .divide(BigDecimal.valueOf(newQty), 4, RoundingMode.HALF_UP);

            holding.setQuantity(newQty);
            holding.setAverageBuyPrice(newAvg);
        }
        holdingRepository.save(holding);

        // 7. Record immutable BUY transaction
        VirtualTransaction tx = new VirtualTransaction();
        tx.setUser(user);
        tx.setSymbol(symbol);
        tx.setTransactionType("BUY");
        tx.setQuantity(quantity);
        tx.setExecutionPrice(currentPrice.setScale(4, RoundingMode.HALF_UP));
        tx.setTotalValue(totalCost);
        tx.setRealizedProfitLoss(null);
        transactionRepository.save(tx);

        return TradeResultDto.builder()
                .status("BUY_EXECUTED")
                .symbol(symbol)
                .quantity(quantity)
                .executionPrice(currentPrice.setScale(4, RoundingMode.HALF_UP))
                .totalValue(totalCost)
                .cashBalanceAfter(wallet.getCashBalance())
                .realizedProfitLoss(null)
                .simulated(true)
                .disclaimer(EDUCATIONAL_DISCLAIMER)
                .build();
    }

    /**
     * Execute a simulated stock SELL order.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TradeResultDto sell(Long userId, TradeRequest request) {
        String symbol = validateAndNormalizeTradeableSymbol(request.getSymbol());
        int quantity = request.getQuantity();
        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero.");
        }

        // 1. Fetch current price once from Phase 6 MarketDataService
        BigDecimal currentPrice = fetchMarketPrice(symbol);

        // 2. Ensure wallet exists and lock it pessimistically
        getOrCreateWallet(userId);
        VirtualWallet wallet = walletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user id: " + userId));

        // 3. Verify holding exists and has sufficient quantity
        VirtualHolding holding = holdingRepository.findByUserIdAndSymbol(userId, symbol)
                .orElseThrow(() -> new BusinessRuleException("No holding found for symbol: " + symbol));

        if (holding.getQuantity() < quantity) {
            throw new BusinessRuleException(String.format(
                    "Insufficient holdings: Owned %d shares of %s, attempted to sell %d",
                    holding.getQuantity(), symbol, quantity
            ));
        }

        // 4. Calculate proceeds = quantity * currentPrice
        BigDecimal proceeds = currentPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);

        // 5. Calculate realized P/L = (currentPrice - avgBuyPrice) * quantity
        BigDecimal realizedPnl = currentPrice.subtract(holding.getAverageBuyPrice())
                .multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);

        // 6. Increase cash balance
        wallet.setCashBalance(wallet.getCashBalance().add(proceeds).setScale(2, RoundingMode.HALF_UP));
        walletRepository.save(wallet);

        // 7. Update holding (holding.averageBuyPrice remains unchanged on SELL)
        int remainingQty = holding.getQuantity() - quantity;
        if (remainingQty == 0) {
            holdingRepository.delete(holding);
        } else {
            holding.setQuantity(remainingQty);
            holdingRepository.save(holding);
        }

        // 8. Record immutable SELL transaction
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        VirtualTransaction tx = new VirtualTransaction();
        tx.setUser(user);
        tx.setSymbol(symbol);
        tx.setTransactionType("SELL");
        tx.setQuantity(quantity);
        tx.setExecutionPrice(currentPrice.setScale(4, RoundingMode.HALF_UP));
        tx.setTotalValue(proceeds);
        tx.setRealizedProfitLoss(realizedPnl);
        transactionRepository.save(tx);

        return TradeResultDto.builder()
                .status("SELL_EXECUTED")
                .symbol(symbol)
                .quantity(quantity)
                .executionPrice(currentPrice.setScale(4, RoundingMode.HALF_UP))
                .totalValue(proceeds)
                .cashBalanceAfter(wallet.getCashBalance())
                .realizedProfitLoss(realizedPnl)
                .simulated(true)
                .disclaimer(EDUCATIONAL_DISCLAIMER)
                .build();
    }

    /**
     * Get user's current holdings with live market valuation and unrealized P/L.
     */
    @Transactional(readOnly = true)
    public List<HoldingDto> getHoldings(Long userId) {
        List<VirtualHolding> holdings = holdingRepository.findByUserId(userId);
        List<HoldingDto> result = new ArrayList<>();

        for (VirtualHolding h : holdings) {
            BigDecimal currentPrice;
            try {
                currentPrice = fetchMarketPrice(h.getSymbol());
            } catch (Exception e) {
                currentPrice = h.getAverageBuyPrice(); // fallback to cost basis if quote unavailable
            }

            BigDecimal marketValue = currentPrice.multiply(BigDecimal.valueOf(h.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal costBasis = h.getAverageBuyPrice().multiply(BigDecimal.valueOf(h.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal unrealizedPnl = marketValue.subtract(costBasis).setScale(2, RoundingMode.HALF_UP);

            BigDecimal unrealizedPnlPercent = BigDecimal.ZERO;
            if (costBasis.compareTo(BigDecimal.ZERO) > 0) {
                unrealizedPnlPercent = unrealizedPnl.divide(costBasis, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
            }

            result.add(HoldingDto.builder()
                    .symbol(h.getSymbol())
                    .quantity(h.getQuantity())
                    .averageBuyPrice(h.getAverageBuyPrice().setScale(4, RoundingMode.HALF_UP))
                    .currentPrice(currentPrice.setScale(4, RoundingMode.HALF_UP))
                    .marketValue(marketValue)
                    .costBasis(costBasis)
                    .unrealizedPnl(unrealizedPnl)
                    .unrealizedPnlPercent(unrealizedPnlPercent)
                    .dataSource("MOCK")
                    .simulated(true)
                    .build());
        }
        return result;
    }

    /**
     * Get complete portfolio overview with cash, market values, and all P/L metrics.
     */
    @Transactional
    public PortfolioDto getPortfolio(Long userId) {
        VirtualWallet wallet = getOrCreateWallet(userId);
        BigDecimal cash = wallet.getCashBalance().setScale(2, RoundingMode.HALF_UP);

        List<HoldingDto> holdings = getHoldings(userId);

        BigDecimal totalMarketValue = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalCostBasis = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalUnrealizedPnl = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        for (HoldingDto h : holdings) {
            totalMarketValue = totalMarketValue.add(h.getMarketValue());
            totalCostBasis = totalCostBasis.add(h.getCostBasis());
            totalUnrealizedPnl = totalUnrealizedPnl.add(h.getUnrealizedPnl());
        }

        BigDecimal portfolioValue = cash.add(totalMarketValue).setScale(2, RoundingMode.HALF_UP);
        BigDecimal realizedPnl = transactionRepository.sumRealizedPnlByUserId(userId);
        if (realizedPnl == null) {
            realizedPnl = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        } else {
            realizedPnl = realizedPnl.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal totalPnl = realizedPnl.add(totalUnrealizedPnl).setScale(2, RoundingMode.HALF_UP);

        return PortfolioDto.builder()
                .cashBalance(cash)
                .totalMarketValue(totalMarketValue)
                .portfolioValue(portfolioValue)
                .totalCostBasis(totalCostBasis)
                .totalUnrealizedPnl(totalUnrealizedPnl)
                .realizedPnl(realizedPnl)
                .totalPnl(totalPnl)
                .holdings(holdings)
                .simulated(true)
                .disclaimer(EDUCATIONAL_DISCLAIMER)
                .build();
    }

    /**
     * Get transaction history for user, newest first.
     */
    @Transactional(readOnly = true)
    public List<TransactionDto> getTransactions(Long userId) {
        List<VirtualTransaction> txList = transactionRepository.findByUserIdOrderByTimestampDesc(userId);
        List<TransactionDto> result = new ArrayList<>();
        for (VirtualTransaction tx : txList) {
            result.add(TransactionDto.builder()
                    .id(tx.getId())
                    .symbol(tx.getSymbol())
                    .transactionType(tx.getTransactionType())
                    .quantity(tx.getQuantity())
                    .executionPrice(tx.getExecutionPrice().setScale(4, RoundingMode.HALF_UP))
                    .totalValue(tx.getTotalValue().setScale(2, RoundingMode.HALF_UP))
                    .realizedProfitLoss(tx.getRealizedProfitLoss() != null
                            ? tx.getRealizedProfitLoss().setScale(2, RoundingMode.HALF_UP)
                            : null)
                    .timestamp(tx.getTimestamp())
                    .build());
        }
        return result;
    }

    /**
     * Validates and normalizes tradeable symbol against the whitelist.
     */
    public String validateAndNormalizeTradeableSymbol(String rawSymbol) {
        String normalized = marketDataService.normalizeSymbol(rawSymbol);
        if (!TRADEABLE_SYMBOLS.contains(normalized)) {
            throw new BusinessRuleException(String.format(
                    "Symbol '%s' is not tradeable. Supported tradeable stocks: %s",
                    normalized, TRADEABLE_SYMBOLS
            ));
        }
        return normalized;
    }

    /**
     * Fetch market quote and convert price to BigDecimal immediately.
     */
    public BigDecimal fetchMarketPrice(String symbol) {
        try {
            MarketQuoteDto quote = marketDataService.getQuote(symbol);
            if (quote == null || quote.getCurrentPrice() <= 0) {
                throw new BusinessRuleException("Market data unavailable — try again shortly.");
            }
            return BigDecimal.valueOf(quote.getCurrentPrice());
        } catch (BusinessRuleException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Market data failure for symbol {}: {}", symbol, e.getMessage());
            throw new BusinessRuleException("Market data unavailable — try again shortly.");
        }
    }
}
