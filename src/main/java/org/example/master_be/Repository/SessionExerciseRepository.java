package org.example.master_be.Repository;

import org.example.master_be.Model.SessionExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionExerciseRepository extends JpaRepository<SessionExercise, Long> {
    List<SessionExercise> findByExerciseId(Long exerciseId);
}