package org.example.master_be.Repository;

import org.example.master_be.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    List<Exercise> findByUserId(Long userId);

    Optional<Exercise> findByIdAndUserId(Long id, Long userId);

    List<Exercise> findByUserIdAndType(Long userId, ExerciseType type);
}