package org.example.master_be.Repository;

import org.example.master_be.Model.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {
    List<WorkoutPlan> findByUserId(Long userId);

    Optional<WorkoutPlan> findByIdAndUserId(Long id, Long userId);
}