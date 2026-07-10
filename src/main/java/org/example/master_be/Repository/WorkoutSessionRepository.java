package org.example.master_be.Repository;

import org.example.master_be.Model.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
    List<WorkoutSession> findByUserIdAndStartedAtGreaterThanEqualAndStartedAtLessThan(
            Long userId,
            LocalDateTime from,
            LocalDateTime to
    );
}
