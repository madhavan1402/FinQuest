package com.finquest.repository;
import com.finquest.model.LearningModule;
import com.finquest.model.LearningTier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface LearningModuleRepository extends JpaRepository<LearningModule, Long> {
    Optional<LearningModule> findByModuleKey(String moduleKey);
    Optional<LearningModule> findBySequenceNumber(int sequenceNumber);
    List<LearningModule> findAllByOrderBySequenceNumberAsc();
    List<LearningModule> findByActiveTrueOrderBySequenceNumberAsc();
    List<LearningModule> findByTierAndActiveTrueOrderBySequenceNumberAsc(LearningTier tier);
}
