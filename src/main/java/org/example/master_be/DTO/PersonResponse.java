package org.example.master_be.DTO;
public record PersonResponse(
        Long id,
        Long userId,
        String gender,
        Integer age,
        Double weight,
        Integer height,
        String activityLevel,
        Double sleepHours,
        Double waistCircumference,
        Double hipsCircumference,
        Double thighCircumference,
        Double bicepsCircumference,
        Double chestCircumference
) {}