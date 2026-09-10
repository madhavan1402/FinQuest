package com.finquest.controller;

import com.finquest.dto.trading.*;
import com.finquest.security.UserPrincipal;
import com.finquest.service.TradingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trading")
@RequiredArgsConstructor
public class TradingController {

    private final TradingService tradingService;

    @GetMapping("/wallet")
    public ResponseEntity<WalletDto> getWallet(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(tradingService.getWallet(principal.getId()));
    }

    @GetMapping("/holdings")
    public ResponseEntity<List<HoldingDto>> getHoldings(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(tradingService.getHoldings(principal.getId()));
    }

    @GetMapping("/portfolio")
    public ResponseEntity<PortfolioDto> getPortfolio(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(tradingService.getPortfolio(principal.getId()));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDto>> getTransactions(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(tradingService.getTransactions(principal.getId()));
    }

    @PostMapping("/buy")
    public ResponseEntity<TradeResultDto> buy(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TradeRequest request) {
        return ResponseEntity.ok(tradingService.buy(principal.getId(), request));
    }

    @PostMapping("/sell")
    public ResponseEntity<TradeResultDto> sell(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TradeRequest request) {
        return ResponseEntity.ok(tradingService.sell(principal.getId(), request));
    }
}
