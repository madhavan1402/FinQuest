package com.finquest.dto;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Returned after successful register or login.
 * <p>
 * Intentionally keeps the same shape as the original {@code AuthResponse}
 * (message, userId, name, email, level, xp, financialScore) so the existing
 * frontend keeps working, and additionally carries the JWT access token,
 * refresh token, role and email-verified flag for the new auth flow.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private String message;
=======
import lombok.AllArgsConstructor;
import lombok.Data;

// Returned to the client after a successful register or login.
// Password is intentionally excluded — never send it back in a response.
@Data
@AllArgsConstructor
public class AuthResponse {
    private String message;       // "Registered successfully" / "Login successful"
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
    private Long   userId;
    private String name;
    private String email;
    private int    level;
    private int    xp;
    private int    financialScore;
<<<<<<< HEAD
    // New auth fields
    private String role;
    private boolean emailVerified;
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";

    // 7-arg constructor — backward compatible with the original AuthResponse
    // used by UserService.getProfile() and the seeded-test-user flow.
    public AuthResponse(String message, Long userId, String name, String email,
                        int level, int xp, int financialScore) {
        this.message = message;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.level = level;
        this.xp = xp;
        this.financialScore = financialScore;
    }

    // Full constructor for the JWT auth flow.
    public AuthResponse(String message, Long userId, String name, String email,
                        int level, int xp, int financialScore, String role,
                        boolean emailVerified, String accessToken, String refreshToken) {
        this(message, userId, name, email, level, xp, financialScore);
        this.role = role;
        this.emailVerified = emailVerified;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = "Bearer";
    }
=======
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
}
