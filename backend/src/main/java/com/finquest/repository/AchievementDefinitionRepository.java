package com.finquest.repository;

import com.finquest.model.AchievementDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AchievementDefinitionRepository extends JpaRepository<AchievementDefinition, Long> {
    Optional<AchievementDefinition> findByCode(String code);
}
