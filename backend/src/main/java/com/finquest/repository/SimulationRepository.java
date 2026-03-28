package com.finquest.repository;

import com.finquest.model.Simulation;
import com.finquest.model.SimulationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimulationRepository extends JpaRepository<Simulation, Long> {

    // All simulations run by a user regardless of type
    List<Simulation> findByUserId(Long userId);

    // Filter by type — e.g. show only BUDGET simulations for a user
    List<Simulation> findByUserIdAndType(Long userId, SimulationType type);
}
