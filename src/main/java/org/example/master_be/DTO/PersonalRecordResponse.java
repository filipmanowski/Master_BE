package org.example.master_be.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class PersonalRecordResponse {
    private String type;
    private Double value;
    private LocalDateTime achievedAt;
    private Long sessionId;
    private Long performedExerciseId;
}
