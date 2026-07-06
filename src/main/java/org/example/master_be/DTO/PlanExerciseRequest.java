package org.example.master_be.DTO;

import lombok.Data;
import org.example.master_be.Model.ExerciseType;

@Data
public class PlanExerciseRequest {
    private String name;
    private ExerciseType type;
    private String description;

    private Integer sets;
    private Integer reps;
    private Double weight;
    private Integer duration;
    private Integer orderIndex;
}
