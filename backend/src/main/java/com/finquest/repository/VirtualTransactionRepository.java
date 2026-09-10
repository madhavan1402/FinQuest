package com.finquest.repository;

import com.finquest.model.VirtualTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository for {@link VirtualTransaction}.
 */
public interface VirtualTransactionRepository extends JpaRepository<VirtualTransaction, Long> {

    // Transaction history for a user — newest first.
    List<VirtualTransaction> findByUserIdOrderByTimestampDesc(Long userId);

    // Sum of all realized P/L from SELL transactions for a user.
    // Returns null if no SELL transactions exist — callers must null-check.
    @Query("SELECT COALESCE(SUM(t.realizedProfitLoss), 0) " +
           "FROM VirtualTransaction t " +
           "WHERE t.user.id = :userId AND t.transactionType = 'SELL'")
    BigDecimal sumRealizedPnlByUserId(@Param("userId") Long userId);
}
