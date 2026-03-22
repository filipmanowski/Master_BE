package org.example.master_be.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonRequest {

    @JsonProperty("user_id")  // ← DODAJ TĄ LINIJKĘ
    private Long userId;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("weight")
    private Double weight;

    @JsonProperty("height")
    private Integer height;

    @JsonProperty("activity_level")  // ← snake_case z JSON
    private String activityLevel;    // → camelCase w Javie

    @JsonProperty("sleep_hours")
    private Double sleepHours;

    @JsonProperty("waist_circumference")
    private Double waistCircumference;

    @JsonProperty("hips_circumference")
    private Double hipsCircumference;

    @JsonProperty("thigh_circumference")
    private Double thighCircumference;

    @JsonProperty("biceps_circumference")
    private Double bicepsCircumference;

    @JsonProperty("chest_circumference")
    private Double chestCircumference;
}