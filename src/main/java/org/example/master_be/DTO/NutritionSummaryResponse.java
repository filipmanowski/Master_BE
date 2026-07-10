package org.example.master_be.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class NutritionSummaryResponse {
    private LocalDate date;
    private Integer calories;
    private Double protein;
    private Double carbohydrates;
    private Double fat;
}
