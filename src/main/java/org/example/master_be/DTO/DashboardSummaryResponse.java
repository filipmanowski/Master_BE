package org.example.master_be.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class DashboardSummaryResponse {
    private LocalDate from;
    private LocalDate to;
    private Long workoutsThisWeek;
    private Long workoutsThisMonth;
    private Long totalTrainingTimeMinutes;
    private Double totalLiftedWeight;
    private Double averageTrainingVolume;
    private List<FrequentItemResponse> mostFrequentExercises;
    private List<FrequentItemResponse> mostFrequentMuscleGroups;
}
