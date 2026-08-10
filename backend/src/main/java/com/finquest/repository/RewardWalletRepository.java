package com.finquest.repository;
import com.finquest.model.RewardWallet; import org.springframework.data.jpa.repository.JpaRepository; import java.util.Optional;
public interface RewardWalletRepository extends JpaRepository<RewardWallet, Long> { Optional<RewardWallet> findByUserId(Long userId); }
