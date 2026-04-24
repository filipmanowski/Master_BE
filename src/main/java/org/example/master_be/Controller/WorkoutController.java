package org.example.master_be.Controller;

import lombok.RequiredArgsConstructor;
import org.example.master_be.Config.AuthUtil;
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
    private final AuthUtil authUtil;

    @PostMapping("/plan")
    public WorkoutPlan createPlan(@RequestBody WorkoutPlan plan) {
        Long userId = authUtil.getCurrentUserId();
        User user = new User();
        user.setId(userId);
        plan.setUser(user);
        return service.createPlan(plan);
    }

    @GetMapping("/plans")
    public List<WorkoutPlan> getPlans() {
        Long userId = authUtil.getCurrentUserId();
        return service.getUserPlans(userId);
    }

    @PostMapping("/plan-exercise")
    public PlanExercise addExercise(@RequestBody PlanExercise pe) {
        return service.addExerciseToPlan(pe);
    }

    @GetMapping("/plan/{planId}")
    public List<PlanExerciseResponse> getPlanExercises(@PathVariable Long planId) {
        Long userId = authUtil.getCurrentUserId();
        return service.getPlanExercisesDto(planId, userId);  // ← DODAJ userId
    }
}