package com.finquest.dto;

import lombok.Data;

// Request body for POST /api/simulation/budget
// { "userId": 1, "income": 50000, "expenses": 35000 }
@Data
public class BudgetRequest {
    private Long userId;
    private double income;
    private double expenses;
}
