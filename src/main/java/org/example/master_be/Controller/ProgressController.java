package org.example.master_be.Controller;

import lombok.RequiredArgsConstructor;
import org.example.master_be.Config.AuthUtil;
import org.example.master_be.DTO.ChartPointResponse;
import org.example.master_be.DTO.ExerciseProgressPointResponse;
import org.example.master_be.DTO.ExerciseProgressResponse;
import org.example.master_be.DTO.ExerciseProgressSummaryResponse;
import org.example.master_be.DTO.PersonalRecordResponse;
import org.example.master_be.Service.ProgressService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService service;
    private final AuthUtil authUtil;

    @GetMapping("/exercises/{exerciseId}")
    public ExerciseProgressResponse getProgress(@PathVariable Long exerciseId) {
        return service.getProgress(authUtil.getCurrentUserId(), exerciseId);
    }

    @GetMapping("/exercises/{exerciseId}/summary")
    public ExerciseProgressSummaryResponse getSummary(@PathVariable Long exerciseId) {
        return service.getSummary(authUtil.getCurrentUserId(), exerciseId);
    }

    @GetMapping("/exercises/{exerciseId}/history")
    public List<ExerciseProgressPointResponse> getHistory(@PathVariable Long exerciseId) {
        return service.getHistory(authUtil.getCurrentUserId(), exerciseId);
    }

    @GetMapping("/exercises/{exerciseId}/charts/weight")
    public List<ChartPointResponse> getWeightChart(@PathVariable Long exerciseId) {
        return service.getWeightChart(authUtil.getCurrentUserId(), exerciseId);
    }

    @GetMapping("/exercises/{exerciseId}/charts/reps")
    public List<ChartPointResponse> getRepsChart(@PathVariable Long exerciseId) {
        return service.getRepsChart(authUtil.getCurrentUserId(), exerciseId);
    }

    @GetMapping("/exercises/{exerciseId}/charts/volume")
    public List<ChartPointResponse> getVolumeChart(@PathVariable Long exerciseId) {
        return service.getVolumeChart(authUtil.getCurrentUserId(), exerciseId);
    }

    @GetMapping("/exercises/{exerciseId}/charts/workouts")
    public List<ChartPointResponse> getWorkoutCountChart(@PathVariable Long exerciseId) {
        return service.getWorkoutCountChart(authUtil.getCurrentUserId(), exerciseId);
    }

    @GetMapping("/exercises/{exerciseId}/records")
    public List<PersonalRecordResponse> getPersonalRecords(@PathVariable Long exerciseId) {
        return service.getPersonalRecords(authUtil.getCurrentUserId(), exerciseId);
    }
}
