package com.finquest.controller;

import com.finquest.dto.BudgetRequest;
import com.finquest.dto.StockRequest;
import com.finquest.dto.TaxRequest;
import com.finquest.service.SimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/simulation")
@RequiredArgsConstructor
public class SimulationController {

    private final SimulationService simulationService;

    // POST /api/simulation/budget
    // Body: { "userId": 1, "income": 50000, "expenses": 35000 }
    // Returns: savings, savingsRate, score, XP update
    @PostMapping("/budget")
    public ResponseEntity<Map<String, Object>> budget(@RequestBody BudgetRequest request) {
        return ResponseEntity.ok(simulationService.runBudget(request));
    }

    // POST /api/simulation/stock
    // Body: { "userId": 1, "decisions": ["BUY", "BUY", "SELL", "BUY", "SELL"] }
    // Returns: finalBalance, profit/loss, score, XP update
    @PostMapping("/stock")
    public ResponseEntity<Map<String, Object>> stock(@RequestBody StockRequest request) {
        return ResponseEntity.ok(simulationService.runStock(request));
    }

    // POST /api/simulation/tax
    // Body: { "userId": 1, "salary": 800000 }
    // Returns: tax amount, effective rate, score, XP update
    @PostMapping("/tax")
    public ResponseEntity<Map<String, Object>> tax(@RequestBody TaxRequest request) {
        return ResponseEntity.ok(simulationService.runTax(request));
    }
}
