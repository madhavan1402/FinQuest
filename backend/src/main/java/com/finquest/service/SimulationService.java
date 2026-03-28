package com.finquest.service;

import com.finquest.dto.BudgetRequest;
import com.finquest.dto.StockRequest;
import com.finquest.dto.TaxRequest;
import com.finquest.model.Simulation;
import com.finquest.model.SimulationType;
import com.finquest.model.User;
import com.finquest.repository.SimulationRepository;
import com.finquest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SimulationService {

    private final SimulationRepository simulationRepository;
    private final UserRepository       userRepository;
    private final LevelService         levelService;

    // ── A. Budget Simulation ──────────────────────────────────────────────────
    // Logic:
    //   savings         = income - expenses
    //   savingsRate     = savings / income × 100   (percentage)
    //   score           = min(savingsRate, 100)     (capped at 100)
    //   XP              = score (1 point of score = 1 XP)
    //
    // Example: income=50000, expenses=35000
    //   savings=15000, savingsRate=30%, score=30, XP=30
    public Map<String, Object> runBudget(BudgetRequest req) {
        User user = loadUser(req.getUserId());

        double savings     = req.getIncome() - req.getExpenses();
        double savingsRate = (req.getIncome() > 0) ? (savings / req.getIncome()) * 100 : 0;
        int    score       = (int) Math.min(Math.max(savingsRate, 0), 100); // clamp 0–100

        String resultText = String.format(
                "Income: %.0f | Expenses: %.0f | Savings: %.0f | Savings Rate: %.1f%%",
                req.getIncome(), req.getExpenses(), savings, savingsRate);

        return persist(user, SimulationType.BUDGET, resultText, score);
    }

    // ── B. Stock Simulation ───────────────────────────────────────────────────
    // Logic:
    //   virtualMoney = 100 000 (starting balance)
    //   stockPrice   = 1 000 per unit (fixed for simplicity)
    //   BUY          → spend 10 000, receive 10 units (if funds available)
    //   SELL         → sell all held units at current price
    //   profit/loss  = finalBalance - 100 000
    //   score        = profit > 0 ? min(profit/1000, 100) : 0
    //   XP           = score
    public Map<String, Object> runStock(StockRequest req) {
        User user = loadUser(req.getUserId());

        final double START_MONEY  = 100_000;
        final double STOCK_PRICE  = 1_000;  // price per unit
        final int    UNITS_PER_BUY = 10;    // each BUY purchases 10 units

        double balance = START_MONEY;
        int    units   = 0;

        for (String decision : req.getDecisions()) {
            if ("BUY".equalsIgnoreCase(decision)) {
                double cost = UNITS_PER_BUY * STOCK_PRICE;
                if (balance >= cost) {       // only buy if funds available
                    balance -= cost;
                    units   += UNITS_PER_BUY;
                }
            } else if ("SELL".equalsIgnoreCase(decision) && units > 0) {
                balance += units * STOCK_PRICE; // sell all held units
                units    = 0;
            }
        }

        // Liquidate any remaining units at the end
        balance += units * STOCK_PRICE;

        double profitLoss = balance - START_MONEY;
        // Score: every 1000 profit = 1 point, capped at 100; loss = 0
        int score = (profitLoss > 0) ? (int) Math.min(profitLoss / 1000, 100) : 0;

        String resultText = String.format(
                "Final Balance: %.0f | Profit/Loss: %.0f", balance, profitLoss);

        return persist(user, SimulationType.STOCK, resultText, score);
    }

    // ── C. Tax Simulation ─────────────────────────────────────────────────────
    // Indian Income Tax slabs (Old Regime, FY 2024-25):
    //   0       – 2,50,000  → 0%
    //   2,50,001– 5,00,000  → 5%
    //   5,00,001–10,00,000  → 20%
    //   10,00,001+          → 30%
    //
    // Score: inverse of effective tax rate — lower tax burden = higher score.
    //   effectiveRate = tax / salary × 100
    //   score         = max(100 - effectiveRate, 0)
    public Map<String, Object> runTax(TaxRequest req) {
        User user = loadUser(req.getUserId());

        double salary = req.getSalary();
        double tax    = calculateTax(salary);
        double effectiveRate = (salary > 0) ? (tax / salary) * 100 : 0;
        int    score  = (int) Math.max(100 - effectiveRate, 0);

        String resultText = String.format(
                "Salary: %.0f | Tax: %.0f | Effective Rate: %.1f%%",
                salary, tax, effectiveRate);

        return persist(user, SimulationType.TAX, resultText, score);
    }

    // Indian income tax slab calculation
    private double calculateTax(double salary) {
        double tax = 0;
        if (salary <= 250_000)       return 0;
        if (salary <= 500_000)       return (salary - 250_000) * 0.05;
        if (salary <= 1_000_000) {
            tax = 12_500;                              // 5% on 2.5L–5L
            tax += (salary - 500_000) * 0.20;         // 20% on 5L–10L
            return tax;
        }
        tax = 12_500 + 100_000;                        // 5% + 20% bands
        tax += (salary - 1_000_000) * 0.30;            // 30% above 10L
        return tax;
    }

    // ── Shared: persist + award XP ────────────────────────────────────────────
    // Saves the simulation row, calls LevelService for XP, returns merged response.
    private Map<String, Object> persist(User user, SimulationType type,
                                        String resultText, int score) {
        Simulation sim = new Simulation(null, user, type, resultText, score);
        simulationRepository.save(sim);

        // XP = score (budget/tax) or score (stock) — already 0–100
        Map<String, Object> xpResult = levelService.awardXp(user.getId(), score);

        Map<String, Object> response = new HashMap<>();
        response.put("simulationType", type);
        response.put("result",         resultText);
        response.put("score",          score);
        response.putAll(xpResult); // merges xp, level, leveledUp, xpToNextLevel, badgeAwarded
        return response;
    }

    // Loads user or throws a clear error
    private User loadUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
    }
}
