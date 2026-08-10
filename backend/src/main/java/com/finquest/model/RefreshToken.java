package com.finquest.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Persisted refresh token for a logged-in user.
 * <p>
 * Kept separate from {@code users} so tokens can be individually revoked
 * (secure logout) and rotated on each refresh (a stolen refresh token cannot
 * be replayed indefinitely — the previous token is invalidated on rotation).
 */
@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The JWT used as the refresh token. Stored hashed via SHA-256 so a DB
    // leak does not expose live tokens.
    @Column(nullable = false, unique = true, length = 128)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // True once the token has been revoked (logout) or rotated.
    @Column(nullable = false)
    private boolean revoked = false;
}

