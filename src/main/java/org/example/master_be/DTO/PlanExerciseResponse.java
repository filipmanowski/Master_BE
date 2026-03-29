package org.example.master_be.DTO;

import lombok.Data;

@Data
public class PlanExerciseResponse {
    private Long id;
    private String name;
    private String type;

    private Integer sets;
    private Integer reps;
    private Double weight;
    private Integer Duration;
}
