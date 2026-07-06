package org.example.master_be.Controller;

import lombok.RequiredArgsConstructor;
import org.example.master_be.Config.AuthUtil;
import org.example.master_be.DTO.PlanExerciseRequest;
import org.example.master_be.DTO.PlanExerciseResponse;
import org.example.master_be.DTO.WorkoutPlanRequest;
import org.example.master_be.Model.WorkoutPlan;
import org.example.master_be.Service.WorkoutService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/workout")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService service;
    private final AuthUtil authUtil;

    @PostMapping("/plan")
    @ResponseStatus(HttpStatus.CREATED)
    public WorkoutPlan createPlan(@RequestBody WorkoutPlanRequest plan) {
        Long userId = authUtil.getCurrentUserId();
        return service.createPlan(userId, plan);
    }

    @GetMapping("/plans")
    public List<WorkoutPlan> getPlans() {
        Long userId = authUtil.getCurrentUserId();
        return service.getUserPlans(userId);
    }

    @DeleteMapping("/plan/{planId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlan(@PathVariable Long planId) {
        Long userId = authUtil.getCurrentUserId();
        service.deletePlan(planId, userId);
    }

    @PostMapping("/plan/{planId}/exercise")
    @ResponseStatus(HttpStatus.CREATED)
    public PlanExerciseResponse addExercise(@PathVariable Long planId,
                                            @RequestBody PlanExerciseRequest request) {
        Long userId = authUtil.getCurrentUserId();
        return service.addExerciseToPlan(planId, userId, request);
    }

    @PutMapping("/plan-exercise/{planExerciseId}")
    public PlanExerciseResponse updateExercise(@PathVariable Long planExerciseId,
                                               @RequestBody PlanExerciseRequest request) {
        Long userId = authUtil.getCurrentUserId();
        return service.updatePlanExercise(planExerciseId, userId, request);
    }

    @GetMapping("/plan/{planId}")
    public List<PlanExerciseResponse> getPlanExercises(@PathVariable Long planId) {
        Long userId = authUtil.getCurrentUserId();
        return service.getPlanExercisesDto(planId, userId);
    }
}
