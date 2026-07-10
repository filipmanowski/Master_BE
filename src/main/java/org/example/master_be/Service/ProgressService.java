package org.example.master_be.Service;

import lombok.RequiredArgsConstructor;
import org.example.master_be.DTO.ChartPointResponse;
import org.example.master_be.DTO.ExerciseProgressPointResponse;
import org.example.master_be.DTO.ExerciseProgressResponse;
import org.example.master_be.DTO.ExerciseProgressSummaryResponse;
import org.example.master_be.DTO.PersonalRecordResponse;
import org.example.master_be.Model.PerformedExercise;
import org.example.master_be.Repository.ExerciseRepository;
import org.example.master_be.Repository.PerformedExerciseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ExerciseRepository exerciseRepo;
    private final PerformedExerciseRepository performedExerciseRepo;

    public ExerciseProgressResponse getProgress(Long userId, Long exerciseId) {
        List<PerformedExercise> entries = getExerciseEntries(userId, exerciseId);

        ExerciseProgressResponse response = new ExerciseProgressResponse();
        response.setSummary(buildSummary(exerciseId, entries));
        response.setHistory(entries.stream().map(this::mapHistoryPoint).toList());
        response.setPersonalRecords(getPersonalRecords(userId, exerciseId));
        return response;
    }

    public ExerciseProgressSummaryResponse getSummary(Long userId, Long exerciseId) {
        return buildSummary(exerciseId, getExerciseEntries(userId, exerciseId));
    }

    public List<ExerciseProgressPointResponse> getHistory(Long userId, Long exerciseId) {
        return getExerciseEntries(userId, exerciseId).stream()
                .map(this::mapHistoryPoint)
                .toList();
    }

    public List<ChartPointResponse> getWeightChart(Long userId, Long exerciseId) {
        return getChart(userId, exerciseId, pe -> valueOrZero(pe.getWeight()));
    }

    public List<ChartPointResponse> getRepsChart(Long userId, Long exerciseId) {
        return getChart(userId, exerciseId, pe -> (double) intOrZero(pe.getReps()));
    }

    public List<ChartPointResponse> getVolumeChart(Long userId, Long exerciseId) {
        return getChart(userId, exerciseId, this::calculateVolume);
    }

    public List<ChartPointResponse> getWorkoutCountChart(Long userId, Long exerciseId) {
        Map<LocalDate, Long> byDay = getExerciseEntries(userId, exerciseId).stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        pe -> pe.getSession().getStartedAt().toLocalDate(),
                        java.util.TreeMap::new,
                        java.util.stream.Collectors.counting()
                ));

        return byDay.entrySet().stream()
                .map(entry -> {
                    ChartPointResponse dto = new ChartPointResponse();
                    dto.setDateTime(entry.getKey().atStartOfDay());
                    dto.setLabel(entry.getKey().toString());
                    dto.setValue(entry.getValue().doubleValue());
                    return dto;
                })
                .toList();
    }

    public List<PersonalRecordResponse> getPersonalRecords(Long userId, Long exerciseId) {
        List<PerformedExercise> entries = getExerciseEntries(userId, exerciseId);
        return List.of(
                buildRecord("MAX_WEIGHT", entries, pe -> valueOrZero(pe.getWeight())),
                buildRecord("MAX_REPS", entries, pe -> (double) intOrZero(pe.getReps())),
                buildRecord("MAX_SETS", entries, pe -> (double) intOrZero(pe.getSets())),
                buildRecord("MAX_VOLUME", entries, this::calculateVolume),
                buildRecord("BEST_SCORE", entries, this::calculateScore)
        ).stream().filter(record -> record.getPerformedExerciseId() != null).toList();
    }

    private List<ChartPointResponse> getChart(Long userId, Long exerciseId, Function<PerformedExercise, Double> valueProvider) {
        return getExerciseEntries(userId, exerciseId).stream()
                .map(pe -> {
                    ChartPointResponse dto = new ChartPointResponse();
                    LocalDateTime dateTime = pe.getSession().getStartedAt();
                    dto.setDateTime(dateTime);
                    dto.setLabel(dateTime.toString());
                    dto.setValue(valueProvider.apply(pe));
                    return dto;
                })
                .toList();
    }

    private ExerciseProgressSummaryResponse buildSummary(Long exerciseId, List<PerformedExercise> entries) {
        ExerciseProgressSummaryResponse dto = new ExerciseProgressSummaryResponse();
        dto.setExerciseId(exerciseId);
        dto.setExerciseName(entries.isEmpty() ? null : entries.get(0).getExercise().getName());
        dto.setTotalPerformed(entries.size());
        dto.setMaxWeight(entries.stream().mapToDouble(pe -> valueOrZero(pe.getWeight())).max().orElse(0.0));
        dto.setMaxReps(entries.stream().mapToInt(pe -> intOrZero(pe.getReps())).max().orElse(0));
        dto.setMaxSets(entries.stream().mapToInt(pe -> intOrZero(pe.getSets())).max().orElse(0));
        dto.setMaxVolume(entries.stream().mapToDouble(this::calculateVolume).max().orElse(0.0));
        dto.setBestScore(entries.stream().mapToDouble(this::calculateScore).max().orElse(0.0));
        dto.setAverageScore(entries.stream().mapToDouble(this::calculateScore).average().orElse(0.0));
        return dto;
    }

    private PersonalRecordResponse buildRecord(String type, List<PerformedExercise> entries, Function<PerformedExercise, Double> valueProvider) {
        PersonalRecordResponse dto = new PersonalRecordResponse();
        dto.setType(type);

        entries.stream()
                .max(Comparator.comparing(valueProvider))
                .ifPresent(pe -> {
                    dto.setValue(valueProvider.apply(pe));
                    dto.setAchievedAt(pe.getSession().getStartedAt());
                    dto.setSessionId(pe.getSession().getId());
                    dto.setPerformedExerciseId(pe.getId());
                });

        return dto;
    }

    private ExerciseProgressPointResponse mapHistoryPoint(PerformedExercise pe) {
        ExerciseProgressPointResponse dto = new ExerciseProgressPointResponse();
        dto.setSessionId(pe.getSession().getId());
        dto.setPerformedExerciseId(pe.getId());
        dto.setExerciseId(pe.getExercise().getId());
        dto.setExerciseName(pe.getExercise().getName());
        dto.setPerformedAt(pe.getSession().getStartedAt());
        dto.setSets(pe.getSets());
        dto.setReps(pe.getReps());
        dto.setWeight(pe.getWeight());
        dto.setDuration(pe.getDuration());
        dto.setVolume(calculateVolume(pe));
        dto.setScore(calculateScore(pe));
        return dto;
    }

    private List<PerformedExercise> getExerciseEntries(Long userId, Long exerciseId) {
        exerciseRepo.findByIdAndUserId(exerciseId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found"));
        return performedExerciseRepo.findCompletedByUserIdAndExerciseId(userId, exerciseId);
    }

    private Double calculateVolume(PerformedExercise pe) {
        return intOrZero(pe.getSets()) * intOrZero(pe.getReps()) * valueOrZero(pe.getWeight());
    }

    private Double calculateScore(PerformedExercise pe) {
        double volume = calculateVolume(pe);
        if (volume > 0) {
            return volume;
        }
        if (pe.getDuration() != null && pe.getDuration() > 0) {
            return pe.getDuration().doubleValue();
        }
        return (double) (intOrZero(pe.getSets()) * intOrZero(pe.getReps()));
    }

    private int intOrZero(Integer value) {
        return value == null ? 0 : value;
    }

    private double valueOrZero(Double value) {
        return value == null ? 0.0 : value;
    }
}
