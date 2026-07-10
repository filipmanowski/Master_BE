package org.example.master_be.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ExerciseProgressPointResponse {
    private Long sessionId;
    private Long performedExerciseId;
    private Long exerciseId;
    private String exerciseName;
    private LocalDateTime performedAt;
    private Integer sets;
    private Integer reps;
    private Double weight;
    private Integer duration;
    private Double volume;
    private Double score;
}
