package com.finquest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// Returned to the client after a successful register or login.
// Password is intentionally excluded — never send it back in a response.
@Data
@AllArgsConstructor
public class AuthResponse {
    private String message;       // "Registered successfully" / "Login successful"
    private Long   userId;
    private String name;
    private String email;
    private int    level;
    private int    xp;
    private int    financialScore;
}
