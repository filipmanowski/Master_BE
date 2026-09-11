package org.example.master_be.Repository;

import org.example.master_be.Model.PerformedExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PerformedExerciseRepository extends JpaRepository<PerformedExercise, Long> {
    List<PerformedExercise> findByExerciseId(Long exerciseId);

    @Query("""
            select pe from PerformedExercise pe
            join fetch pe.session s
            join fetch pe.exercise e
            where s.user.id = :userId
              and pe.exercise.id = :exerciseId
              and pe.completed = true
            order by s.startedAt asc, pe.id asc
            """)
    List<PerformedExercise> findCompletedByUserIdAndExerciseId(
            @Param("userId") Long userId,
            @Param("exerciseId") Long exerciseId
    );

    @Query("""
            select pe from PerformedExercise pe
            join fetch pe.session s
            join fetch pe.exercise e
            where s.user.id = :userId
              and pe.completed = true
              and s.startedAt >= :from
              and s.startedAt < :to
            order by s.startedAt asc, pe.id asc
            """)
    List<PerformedExercise> findCompletedByUserIdAndSessionStartedAtBetween(
            @Param("userId") Long userId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
            select pe from PerformedExercise pe
            join fetch pe.session s
            left join fetch s.plan p
            join fetch pe.exercise e
            where s.user.id = :userId
              and pe.completed = true
            order by s.startedAt desc, pe.id desc
            """)
    List<PerformedExercise> findCompletedHistoryByUserId(@Param("userId") Long userId);

    @Query("""
            select count(distinct s.id) from PerformedExercise pe
            join pe.session s
            where s.user.id = :userId
              and pe.completed = true
              and s.startedAt >= :from
              and s.startedAt < :to
            """)
    long countDistinctCompletedSessionsByUserIdAndStartedAtBetween(
            @Param("userId") Long userId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
