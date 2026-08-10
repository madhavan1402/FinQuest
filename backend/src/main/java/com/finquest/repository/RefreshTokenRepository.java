package com.finquest.repository;

import com.finquest.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Find a token by its SHA-256 hash — used during refresh & logout.
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    // All live (non-revoked) tokens for a user — for logout-all or cleanup.
    List<RefreshToken> findByUserIdAndRevokedFalse(Long userId);

    // Invalidate every token belonging to a user when needed (logout-all).
    void deleteByUserId(Long userId);
}

