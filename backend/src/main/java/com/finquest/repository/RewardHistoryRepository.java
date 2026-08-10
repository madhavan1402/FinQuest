package com.finquest.repository;
import com.finquest.model.RewardHistory; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface RewardHistoryRepository extends JpaRepository<RewardHistory, Long> { List<RewardHistory> findByUserIdOrderByEarnedAtDesc(Long userId); }
