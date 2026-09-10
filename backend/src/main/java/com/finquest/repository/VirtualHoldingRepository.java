package com.finquest.repository;

import com.finquest.model.VirtualHolding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link VirtualHolding}.
 */
public interface VirtualHoldingRepository extends JpaRepository<VirtualHolding, Long> {

    // All holdings for a user — used to build portfolio and holdings list.
    List<VirtualHolding> findByUserId(Long userId);

    // Single holding lookup — used in BUY (upsert) and SELL (verification).
    Optional<VirtualHolding> findByUserIdAndSymbol(Long userId, String symbol);
}
