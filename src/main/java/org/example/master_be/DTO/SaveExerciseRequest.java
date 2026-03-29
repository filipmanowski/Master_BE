package org.example.master_be.DTO;

import lombok.Data;

@Data
public class SaveExerciseRequest {
    private Long sessionId;
    private Long exerciseId;

    private Integer sets;
    private Integer reps;
    private Double weight;
    private Integer duration;

    private Boolean completed;
}