package org.example.master_be.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ExerciseProgressSummaryResponse {
    private Long exerciseId;
    private String exerciseName;
    private Integer totalPerformed;
    private Double maxWeight;
    private Integer maxReps;
    private Integer maxSets;
    private Double maxVolume;
    private Double bestScore;
    private Double averageScore;
}
