package com.finquest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request body for {@code POST /api/auth/login}.
 * <p>
 * Kept flat (email + password) so the existing frontend login form keeps working.
 */
@Data
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "A valid email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
