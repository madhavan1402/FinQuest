package com.finquest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request body for {@code POST /api/auth/reset-password}.
 * <p>
 * Carries the reset code from the emailed link plus the new password.
 */
@Data
public class ResetPasswordRequest {

    @NotBlank(message = "Reset code is required")
    private String code;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 72, message = "New password must be between 8 and 72 characters")
    private String newPassword;
}
