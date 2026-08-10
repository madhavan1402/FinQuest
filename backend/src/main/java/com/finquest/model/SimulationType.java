package com.finquest.model;

// Enum stored as a string in the DB (BUDGET, STOCK, TAX).
// Using @Enumerated(EnumType.STRING) in Simulation makes the column human-readable.
public enum SimulationType {
    BUDGET,
    STOCK,
    TAX
}
