package com.finquest.repository;

import com.finquest.model.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {

    // Retrieve all quiz attempts by a user — used for progress history
    List<QuizResult> findByUserId(Long userId);

    // All attempts for a specific user + level
    List<QuizResult> findByUserIdAndLevel(Long userId, int level);
}
