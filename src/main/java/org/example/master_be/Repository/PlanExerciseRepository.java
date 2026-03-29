package org.example.master_be.Repository;

import org.example.master_be.Model.PlanExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanExerciseRepository extends JpaRepository<PlanExercise, Long> {
    List<PlanExercise> findByPlanId(Long planId);
}