package org.example.master_be.Repository;

import org.example.master_be.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserExerciseRepository extends JpaRepository<UserExercise, Long> {
    List<UserExercise> findByUserId(Long userId);
}