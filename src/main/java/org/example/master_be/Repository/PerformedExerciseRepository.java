package org.example.master_be.Repository;

import org.example.master_be.Model.PerformedExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformedExerciseRepository extends JpaRepository<PerformedExercise, Long> {
    List<PerformedExercise> findByExerciseId(Long exerciseId);
}