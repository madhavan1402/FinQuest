package com.finquest.repository;
import com.finquest.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    List<UserProgress> findByUserId(Long userId);
    Optional<UserProgress> findByUserIdAndLearningModuleId(Long userId, Long learningModuleId);
    void deleteByUserId(Long userId);
}
