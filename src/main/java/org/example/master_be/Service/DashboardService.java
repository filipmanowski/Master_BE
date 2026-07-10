package org.example.master_be.Service;

import lombok.RequiredArgsConstructor;
import org.example.master_be.DTO.DashboardSummaryResponse;
import org.example.master_be.DTO.FrequentItemResponse;
import org.example.master_be.Model.PerformedExercise;
import org.example.master_be.Model.WorkoutSession;
import org.example.master_be.Repository.PerformedExerciseRepository;
import org.example.master_be.Repository.WorkoutSessionRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final WorkoutSessionRepository sessionRepo;
    private final PerformedExerciseRepository performedExerciseRepo;

    public DashboardSummaryResponse getSummary(Long userId, LocalDate from, LocalDate to) {
        LocalDate periodTo = to == null ? LocalDate.now() : to;
        LocalDate periodFrom = from == null ? periodTo.minusDays(30) : from;
        LocalDateTime fromDateTime = periodFrom.atStartOfDay();
        LocalDateTime toDateTime = periodTo.plusDays(1).atStartOfDay();

        List<WorkoutSession> sessions = sessionRepo.findByUserIdAndStartedAtGreaterThanEqualAndStartedAtLessThan(
                userId,
                fromDateTime,
                toDateTime
        );
        List<PerformedExercise> performedExercises = performedExerciseRepo.findCompletedByUserIdAndSessionStartedAtBetween(
                userId,
                fromDateTime,
                toDateTime
        );

        DashboardSummaryResponse response = new DashboardSummaryResponse();
        response.setFrom(periodFrom);
        response.setTo(periodTo);
        response.setWorkoutsThisWeek(countCurrentWeek(userId));
        response.setWorkoutsThisMonth(countCurrentMonth(userId));
        response.setTotalTrainingTimeMinutes(calculateTrainingMinutes(sessions));
        response.setTotalLiftedWeight(performedExercises.stream().mapToDouble(this::calculateVolume).sum());
        response.setAverageTrainingVolume(calculateAverageTrainingVolume(performedExercises, sessions.size()));
        response.setMostFrequentExercises(getMostFrequentExercises(performedExercises));
        response.setMostFrequentMuscleGroups(List.of());
        return response;
    }

    private long countCurrentWeek(Long userId) {
        LocalDate now = LocalDate.now();
        LocalDate weekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return sessionRepo.findByUserIdAndStartedAtGreaterThanEqualAndStartedAtLessThan(
                userId,
                weekStart.atStartOfDay(),
                weekStart.plusDays(7).atStartOfDay()
        ).size();
    }

    private long countCurrentMonth(Long userId) {
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        return sessionRepo.findByUserIdAndStartedAtGreaterThanEqualAndStartedAtLessThan(
                userId,
                monthStart.atStartOfDay(),
                monthStart.plusMonths(1).atStartOfDay()
        ).size();
    }

    private long calculateTrainingMinutes(List<WorkoutSession> sessions) {
        return sessions.stream()
                .filter(session -> session.getStartedAt() != null && session.getEndedAt() != null)
                .mapToLong(session -> Duration.between(session.getStartedAt(), session.getEndedAt()).toMinutes())
                .sum();
    }

    private double calculateAverageTrainingVolume(List<PerformedExercise> performedExercises, int sessionCount) {
        if (sessionCount == 0) {
            return 0.0;
        }
        return performedExercises.stream().mapToDouble(this::calculateVolume).sum() / sessionCount;
    }

    private List<FrequentItemResponse> getMostFrequentExercises(List<PerformedExercise> performedExercises) {
        Map<Long, FrequentCounter> counters = new LinkedHashMap<>();
        for (PerformedExercise pe : performedExercises) {
            counters.computeIfAbsent(pe.getExercise().getId(), id -> new FrequentCounter(id, pe.getExercise().getName()))
                    .increment();
        }

        return counters.values().stream()
                .sorted(Comparator.comparing(FrequentCounter::getCount).reversed())
                .limit(10)
                .map(counter -> {
                    FrequentItemResponse dto = new FrequentItemResponse();
                    dto.setId(counter.getId());
                    dto.setName(counter.getName());
                    dto.setCount(counter.getCount());
                    return dto;
                })
                .toList();
    }

    private double calculateVolume(PerformedExercise pe) {
        return intOrZero(pe.getSets()) * intOrZero(pe.getReps()) * doubleOrZero(pe.getWeight());
    }

    private int intOrZero(Integer value) {
        return value == null ? 0 : value;
    }

    private double doubleOrZero(Double value) {
        return value == null ? 0.0 : value;
    }

    private static class FrequentCounter {
        private final Long id;
        private final String name;
        private long count;

        private FrequentCounter(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        private void increment() {
            count++;
        }

        private Long getId() {
            return id;
        }

        private String getName() {
            return name;
        }

        private long getCount() {
            return count;
        }
    }
}
