package org.example.master_be.Controller;

import lombok.RequiredArgsConstructor;
import org.example.master_be.DTO.PlanExerciseResponse;
import org.example.master_be.Model.*;
import org.example.master_be.Service.WorkoutService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workout")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService service;

    @PostMapping("/plan")
    public WorkoutPlan createPlan(@RequestBody WorkoutPlan plan) {
        return service.createPlan(plan);
    }

    @GetMapping("/plans/{userId}")
    public List<WorkoutPlan> getPlans(@PathVariable Long userId) {
        return service.getUserPlans(userId);
    }

    @PostMapping("/plan-exercise")
    public PlanExercise addExercise(@RequestBody PlanExercise pe) {
        return service.addExerciseToPlan(pe);
    }

    @GetMapping("/plan/{planId}")
    public List<PlanExerciseResponse> getPlanExercises(@PathVariable Long planId) {
        return service.getPlanExercisesDto(planId);
    }
}