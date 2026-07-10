package org.example.master_be.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class NutritionEntryRequest {
    private LocalDateTime consumedAt;
    private String mealName;
    private String description;
    private Integer calories;
    private Double protein;
    private Double carbohydrates;
    private Double fat;
    private List<MicronutrientRequest> micronutrients;
}
