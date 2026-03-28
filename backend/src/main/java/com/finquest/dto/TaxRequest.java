package com.finquest.dto;

import lombok.Data;

// Request body for POST /api/simulation/tax
// { "userId": 1, "salary": 800000 }
@Data
public class TaxRequest {
    private Long userId;
    private double salary;
}
