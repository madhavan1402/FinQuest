package com.finquest.repository;

import com.finquest.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    // Fetch all badges earned by a specific user
    // Spring Data translates this to: SELECT * FROM achievements WHERE user_id = ?
    List<Achievement> findByUserId(Long userId);

    // Prevent awarding the same badge twice to the same user
    boolean existsByUserIdAndBadgeName(Long userId, String badgeName);
}
