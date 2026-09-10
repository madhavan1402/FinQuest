package com.finquest.repository;

import com.finquest.model.VirtualWallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository for {@link VirtualWallet}.
 *
 * Two lookup strategies:
 * 1. findByUserId — used for read-only wallet fetches (GET /wallet).
 * 2. findByUserIdForUpdate — acquires a PESSIMISTIC_WRITE (SELECT FOR UPDATE)
 *    lock, used inside every BUY and SELL transaction to prevent double-spend.
 */
public interface VirtualWalletRepository extends JpaRepository<VirtualWallet, Long> {

    Optional<VirtualWallet> findByUserId(Long userId);

    // SELECT ... FOR UPDATE — ensures only one BUY/SELL modifies the wallet row
    // at a time for a given user. Concurrent requests for different users are
    // never blocked by each other (row-level lock, not table-level).
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM VirtualWallet w WHERE w.user.id = :userId")
    Optional<VirtualWallet> findByUserIdForUpdate(@Param("userId") Long userId);

    // Atomic wallet initialisation — avoids race condition when two requests
    // simultaneously attempt to create a wallet for the same user.
    // Uses MySQL "INSERT IGNORE" semantics via a native query:
    //   If the row already exists (uk_vwallet_user), the insert is silently
    //   skipped; no duplicate-key error is thrown.
    @Modifying
    @Query(value = """
            INSERT IGNORE INTO virtual_wallets
                (user_id, cash_balance, initial_balance, created_at, updated_at)
            VALUES
                (:userId, 100000.00, 100000.00, NOW(6), NOW(6))
            """, nativeQuery = true)
    void insertIgnoreForUser(@Param("userId") Long userId);
}
