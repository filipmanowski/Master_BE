package org.example.master_be.Repository;

import org.example.master_be.Model.PlanExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlanExerciseRepository extends JpaRepository<PlanExercise, Long> {
    List<PlanExercise> findByPlanId(Long planId);

    Optional<PlanExercise> findByIdAndPlanUserId(Long id, Long userId);

    void deleteByPlanId(Long planId);
}
