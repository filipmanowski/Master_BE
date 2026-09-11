package org.example.master_be.Repository;

import org.example.master_be.Model.WorkoutSession;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
    List<WorkoutSession> findByUserIdAndStartedAtGreaterThanEqualAndStartedAtLessThan(
            Long userId,
            LocalDateTime from,
            LocalDateTime to
    );

    Optional<WorkoutSession> findByIdAndUserId(Long id, Long userId);

    @Modifying
    @Query("update WorkoutSession s set s.plan = null where s.plan.id = :planId and s.user.id = :userId")
    void clearPlanForUserSessions(@Param("planId") Long planId, @Param("userId") Long userId);
}
