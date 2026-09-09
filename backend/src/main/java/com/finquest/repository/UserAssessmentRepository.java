package com.finquest.repository;

import com.finquest.model.UserAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAssessmentRepository extends JpaRepository<UserAssessment, Long> {
    Optional<UserAssessment> findTopByUserIdOrderByCompletedAtDesc(Long userId);
    List<UserAssessment> findByUserIdOrderByCompletedAtDesc(Long userId);
}
