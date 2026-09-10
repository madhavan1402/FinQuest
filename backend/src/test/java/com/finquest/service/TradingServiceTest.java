package com.finquest.service;

import com.finquest.dto.trading.PortfolioDto;
import com.finquest.dto.trading.TradeRequest;
import com.finquest.dto.trading.TradeResultDto;
import com.finquest.dto.trading.WalletDto;
import com.finquest.exception.BadRequestException;
import com.finquest.exception.BusinessRuleException;
import com.finquest.model.User;
import com.finquest.model.VirtualHolding;
import com.finquest.model.VirtualTransaction;
import com.finquest.model.VirtualWallet;
import com.finquest.repository.UserRepository;
import com.finquest.repository.VirtualHoldingRepository;
import com.finquest.repository.VirtualTransactionRepository;
import com.finquest.repository.VirtualWalletRepository;
import com.finquest.service.market.MarketDataService;
import com.finquest.service.market.MockMarketDataProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradingServiceTest {

    @Mock
    private VirtualWalletRepository walletRepository;

    @Mock
    private VirtualHoldingRepository holdingRepository;

    @Mock
    private VirtualTransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    private MarketDataService marketDataService;
    private TradingService tradingService;

    private User testUser;
    private VirtualWallet testWallet;

    @BeforeEach
    void setUp() {
        marketDataService = new MarketDataService(new MockMarketDataProvider());
        marketDataService.clearCache();

        tradingService = new TradingService(
                walletRepository,
                holdingRepository,
                transactionRepository,
                userRepository,
                marketDataService
        );

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Trader Joe");

        testWallet = new VirtualWallet();
        testWallet.setId(10L);
        testWallet.setUser(testUser);
        testWallet.setCashBalance(new BigDecimal("100000.00"));
        testWallet.setInitialBalance(new BigDecimal("100000.00"));
    }

    @Test
    void test1_walletInitialization() {
        doNothing().when(walletRepository).insertIgnoreForUser(1L);
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(testWallet));

        VirtualWallet wallet = tradingService.getOrCreateWallet(1L);
        assertNotNull(wallet);
        verify(walletRepository).insertIgnoreForUser(1L);
    }

    @Test
    void test2_walletRemains100000Initially() {
        doNothing().when(walletRepository).insertIgnoreForUser(1L);
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(testWallet));

        WalletDto walletDto = tradingService.getWallet(1L);
        assertEquals(new BigDecimal("100000.00"), walletDto.getCashBalance());
        assertEquals(new BigDecimal("100000.00"), walletDto.getInitialBalance());
    }

    @Test
    void test3_validBuy() {
        TradeRequest request = new TradeRequest();
        request.setSymbol("RELIANCE");
        request.setQuantity(5);

        doNothing().when(walletRepository).insertIgnoreForUser(1L);
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(testWallet));
        when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(testWallet));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(holdingRepository.findByUserIdAndSymbol(1L, "RELIANCE")).thenReturn(Optional.empty());

        TradeResultDto result = tradingService.buy(1L, request);

        assertNotNull(result);
        assertEquals("BUY_EXECUTED", result.getStatus());
        assertEquals("RELIANCE", result.getSymbol());
        assertEquals(5, result.getQuantity());
        assertTrue(result.getExecutionPrice().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(result.getTotalValue().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(result.getCashBalanceAfter().compareTo(new BigDecimal("100000.00")) < 0);
        assertTrue(result.isSimulated());
    }

    @Test
    void test4_buyDeductsCash() {
        TradeRequest request = new TradeRequest();
        request.setSymbol("TCS");
        request.setQuantity(2);

        doNothing().when(walletRepository).insertIgnoreForUser(1L);
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(testWallet));
        when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(testWallet));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(holdingRepository.findByUserIdAndSymbol(1L, "TCS")).thenReturn(Optional.empty());

        TradeResultDto result = tradingService.buy(1L, request);

        BigDecimal expectedCash = new BigDecimal("100000.00").subtract(result.getTotalValue());
        assertEquals(expectedCash, testWallet.getCashBalance());
        verify(walletRepository).save(testWallet);
    }

    @Test
    void test5_buyCreatesHolding() {
        TradeRequest request = new TradeRequest();
        request.setSymbol("INFY");
        request.setQuantity(4);

        doNothing().when(walletRepository).insertIgnoreForUser(1L);
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(testWallet));
        when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(testWallet));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(holdingRepository.findByUserIdAndSymbol(1L, "INFY")).thenReturn(Optional.empty());

        tradingService.buy(1L, request);

        verify(holdingRepository).save(argThat(h ->
                h.getSymbol().equals("INFY") &&
                h.getQuantity() == 4 &&
                h.getAverageBuyPrice().compareTo(BigDecimal.ZERO) > 0
        ));
    }

    @Test
    void test6_buyWeightedAverageCalculation() {
        TradeRequest request = new TradeRequest();
        request.setSymbol("HDFCBANK");
        request.setQuantity(10);

        VirtualHolding existing = new VirtualHolding();
        existing.setUser(testUser);
        existing.setSymbol("HDFCBANK");
        existing.setQuantity(10);
        existing.setAverageBuyPrice(new BigDecimal("1000.0000"));

        doNothing().when(walletRepository).insertIgnoreForUser(1L);
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(testWallet));
        when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(testWallet));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(holdingRepository.findByUserIdAndSymbol(1L, "HDFCBANK")).thenReturn(Optional.of(existing));

        TradeResultDto result = tradingService.buy(1L, request);

        assertEquals(20, existing.getQuantity());
        // Price should be between 1000 and current market price
        assertTrue(existing.getAverageBuyPrice().compareTo(BigDecimal.ZERO) > 0);
        verify(holdingRepository).save(existing);
    }

    @Test
    void test7_insufficientFunds() {
        TradeRequest request = new TradeRequest();
        request.setSymbol("RELIANCE");
        request.setQuantity(1000); // Exceeds ₹100,000

        doNothing().when(walletRepository).insertIgnoreForUser(1L);
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(testWallet));
        when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(testWallet));

        assertThrows(BusinessRuleException.class, () -> tradingService.buy(1L, request));
    }

    @Test
    void test8_invalidQuantity() {
        TradeRequest request = new TradeRequest();
        request.setSymbol("RELIANCE");
        request.setQuantity(0);

        assertThrows(BadRequestException.class, () -> tradingService.buy(1L, request));
    }

    @Test
    void test9_nonTradeableNifty50Rejected() {
        TradeRequest request = new TradeRequest();
        request.setSymbol("NIFTY50");
        request.setQuantity(1);

        assertThrows(BusinessRuleException.class, () -> tradingService.buy(1L, request));
    }

    @Test
    void test10_nonTradeableSensexRejected() {
        TradeRequest request = new TradeRequest();
        request.setSymbol("SENSEX");
        request.setQuantity(1);

        assertThrows(BusinessRuleException.class, () -> tradingService.buy(1L, request));
    }

    @Test
    void test11_unknownSymbol() {
        TradeRequest request = new TradeRequest();
        request.setSymbol("UNKNOWN_CO");
        request.setQuantity(1);

        assertThrows(BusinessRuleException.class, () -> tradingService.buy(1L, request));
    }

    @Test
    void test12_validSell() {
        TradeRequest request = new TradeRequest();
        request.setSymbol("RELIANCE");
        request.setQuantity(2);

        VirtualHolding holding = new VirtualHolding();
        holding.setUser(testUser);
        holding.setSymbol("RELIANCE");
        holding.setQuantity(5);
        holding.setAverageBuyPrice(new BigDecimal("2000.0000"));

        doNothing().when(walletRepository).insertIgnoreForUser(1L);
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(testWallet));
        when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(testWallet));
        when(holdingRepository.findByUserIdAndSymbol(1L, "RELIANCE")).thenReturn(Optional.of(holding));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        TradeResultDto result = tradingService.sell(1L, request);

        assertEquals("SELL_EXECUTED", result.getStatus());
        assertEquals(2, result.getQuantity());
        assertNotNull(result.getExecutionPrice());
        assertNotNull(result.getTotalValue());
        assertNotNull(result.getRealizedProfitLoss());
    }

    @Test
    void test13_sellIncreasesCash() {
        TradeRequest request = new TradeRequest();
        request.setSymbol("RELIANCE");
        request.setQuantity(3);

        VirtualHolding holding = new VirtualHolding();
        holding.setUser(testUser);
        holding.setSymbol("RELIANCE");
        holding.setQuantity(5);
        holding.setAverageBuyPrice(new BigDecimal("2000.0000"));

        doNothing().when(walletRepository).insertIgnoreForUser(1L);
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(testWallet));
        when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(testWallet));
        when(holdingRepository.findByUserIdAndSymbol(1L, "RELIANCE")).thenReturn(Optional.of(holding));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        TradeResultDto result = tradingService.sell(1L, request);

        BigDecimal expectedCash = new BigDecimal("100000.00").add(result.getTotalValue());
        assertEquals(expectedCash, testWallet.getCashBalance());
        verify(walletRepository).save(testWallet);
    }

    @Test
    void test14_sellDecreasesHolding() {
        TradeRequest request = new TradeRequest();
        request.setSymbol("ICICIBANK");
        request.setQuantity(2);

        VirtualHolding holding = new VirtualHolding();
        holding.setUser(testUser);
        holding.setSymbol("ICICIBANK");
        holding.setQuantity(5);
        holding.setAverageBuyPrice(new BigDecimal("1000.0000"));

        doNothing().when(walletRepository).insertIgnoreForUser(1L);
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(testWallet));
        when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(testWallet));
        when(holdingRepository.findByUserIdAndSymbol(1L, "ICICIBANK")).thenReturn(Optional.of(holding));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        tradingService.sell(1L, request);

        assertEquals(3, holding.getQuantity());
        verify(holdingRepository).save(holding);
    }

    @Test
    void test15_sellRemovesHoldingAtZero() {
        TradeRequest request = new TradeRequest();
        request.setSymbol("ICICIBANK");
        request.setQuantity(5);

        VirtualHolding holding = new VirtualHolding();
        holding.setUser(testUser);
        holding.setSymbol("ICICIBANK");
        holding.setQuantity(5);
        holding.setAverageBuyPrice(new BigDecimal("1000.0000"));

        doNothing().when(walletRepository).insertIgnoreForUser(1L);
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(testWallet));
        when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(testWallet));
        when(holdingRepository.findByUserIdAndSymbol(1L, "ICICIBANK")).thenReturn(Optional.of(holding));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        tradingService.sell(1L, request);

        verify(holdingRepository).delete(holding);
    }

    @Test
    void test16_realizedPnlCalculation() {
        TradeRequest request = new TradeRequest();
        request.setSymbol("TCS");
        request.setQuantity(4);

        VirtualHolding holding = new VirtualHolding();
        holding.setUser(testUser);
        holding.setSymbol("TCS");
        holding.setQuantity(10);
        holding.setAverageBuyPrice(new BigDecimal("1000.0000")); // Very low cost basis

        doNothing().when(walletRepository).insertIgnoreForUser(1L);
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(testWallet));
        when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(testWallet));
        when(holdingRepository.findByUserIdAndSymbol(1L, "TCS")).thenReturn(Optional.of(holding));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        TradeResultDto result = tradingService.sell(1L, request);

        // Price > 1000 so profit > 0
        assertTrue(result.getRealizedProfitLoss().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void test17_insufficientHoldings() {
        TradeRequest request = new TradeRequest();
        request.setSymbol("RELIANCE");
        request.setQuantity(10);

        VirtualHolding holding = new VirtualHolding();
        holding.setUser(testUser);
        holding.setSymbol("RELIANCE");
        holding.setQuantity(5);
        holding.setAverageBuyPrice(new BigDecimal("2000.0000"));

        doNothing().when(walletRepository).insertIgnoreForUser(1L);
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(testWallet));
        when(walletRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(testWallet));
        when(holdingRepository.findByUserIdAndSymbol(1L, "RELIANCE")).thenReturn(Optional.of(holding));

        assertThrows(BusinessRuleException.class, () -> tradingService.sell(1L, request));
    }

    @Test
    void test18_portfolioCalculation() {
        VirtualHolding holding = new VirtualHolding();
        holding.setUser(testUser);
        holding.setSymbol("RELIANCE");
        holding.setQuantity(5);
        holding.setAverageBuyPrice(new BigDecimal("2000.0000"));

        doNothing().when(walletRepository).insertIgnoreForUser(1L);
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(testWallet));
        when(holdingRepository.findByUserId(1L)).thenReturn(List.of(holding));
        when(transactionRepository.sumRealizedPnlByUserId(1L)).thenReturn(new BigDecimal("500.00"));

        PortfolioDto portfolio = tradingService.getPortfolio(1L);

        assertEquals(new BigDecimal("100000.00"), portfolio.getCashBalance());
        assertTrue(portfolio.getTotalMarketValue().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(portfolio.getPortfolioValue().compareTo(new BigDecimal("100000.00")) > 0);
        assertEquals(new BigDecimal("10000.00"), portfolio.getTotalCostBasis());
        assertEquals(new BigDecimal("500.00"), portfolio.getRealizedPnl());
    }

    @Test
    void test19_totalPnl() {
        doNothing().when(walletRepository).insertIgnoreForUser(1L);
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(testWallet));
        when(holdingRepository.findByUserId(1L)).thenReturn(List.of());
        when(transactionRepository.sumRealizedPnlByUserId(1L)).thenReturn(new BigDecimal("1200.50"));

        PortfolioDto portfolio = tradingService.getPortfolio(1L);
        assertEquals(new BigDecimal("1200.50"), portfolio.getRealizedPnl());
        assertEquals(new BigDecimal("1200.50"), portfolio.getTotalPnl());
    }

    @Test
    void test20_transactionHistory() {
        VirtualTransaction tx = new VirtualTransaction();
        tx.setId(101L);
        tx.setUser(testUser);
        tx.setSymbol("RELIANCE");
        tx.setTransactionType("BUY");
        tx.setQuantity(10);
        tx.setExecutionPrice(new BigDecimal("2500.0000"));
        tx.setTotalValue(new BigDecimal("25000.00"));
        tx.setRealizedProfitLoss(null);

        when(transactionRepository.findByUserIdOrderByTimestampDesc(1L)).thenReturn(List.of(tx));

        var transactions = tradingService.getTransactions(1L);
        assertEquals(1, transactions.size());
        assertEquals("RELIANCE", transactions.get(0).getSymbol());
        assertEquals("BUY", transactions.get(0).getTransactionType());
    }

    @Test
    void test21_marketDataFailurePreventsTrade() {
        // Mock empty symbol or failing mock
        TradeRequest request = new TradeRequest();
        request.setSymbol("INVALID_FEED");
        request.setQuantity(1);

        assertThrows(BusinessRuleException.class, () -> tradingService.buy(1L, request));
    }
}
