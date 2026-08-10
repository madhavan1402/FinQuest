package com.finquest.dto;

import com.finquest.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Safe profile payload returned by {@code GET /api/profile} and used by
 * {@code PUT /api/profile}. Never exposes the password hash or any
 * verification/reset codes.
 */
@Data
@AllArgsConstructor
public class ProfileDto {
    private Long   id;
    private String name;
    private String email;
    private String role;
    private boolean emailVerified;
    private int    level;
    private int    xp;
    private int    financialScore;
    private int    coins;

    public static ProfileDto from(User user) {
        return new ProfileDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.isEmailVerified(),
                user.getLevel(),
                user.getXp(),
                user.getFinancialScore(),
                user.getCoins()
        );
    }
}
