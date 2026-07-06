package org.example.master_be.DTO;

import lombok.Data;

@Data
public class WorkoutPlanRequest {
    private String name;
    private String description;
    private Boolean isTemplate;
    private Long parentPlanId;
}
