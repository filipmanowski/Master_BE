package org.example.master_be.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ExerciseProgressResponse {
    private ExerciseProgressSummaryResponse summary;
    private List<ExerciseProgressPointResponse> history;
    private List<PersonalRecordResponse> personalRecords;
}
