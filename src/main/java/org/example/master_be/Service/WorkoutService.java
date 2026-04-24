package org.example.master_be.Service;

import lombok.RequiredArgsConstructor;
import org.example.master_be.DTO.PlanExerciseResponse;
import org.example.master_be.Model.*;
import org.example.master_be.Repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutPlanRepository planRepo;
    private final PlanExerciseRepository planExerciseRepo;
    private final ExerciseRepository exerciseRepo;

    public WorkoutPlan createPlan(WorkoutPlan plan) {
        return planRepo.save(plan);
    }

    public List<WorkoutPlan> getUserPlans(Long userId) {
        return planRepo.findByUserId(userId);
    }

    public PlanExercise addExerciseToPlan(PlanExercise pe) {
        return planExerciseRepo.save(pe);
    }

    public List<PlanExercise> getPlanExercises(Long planId) {
        return planExerciseRepo.findByPlanId(planId);
    }


    public List<PlanExerciseResponse> getPlanExercisesDto(Long planId, Long userId) {
        // ← DODAJ userId do parametrów
        // Weryfikuj czy plan należy do tego użytkownika
        planRepo.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new RuntimeException("Plan not found or unauthorized"));

        return planExerciseRepo.findByPlanId(planId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }
    private PlanExerciseResponse mapToDto(PlanExercise pe) {
        PlanExerciseResponse dto = new PlanExerciseResponse();

        dto.setId(pe.getId());
        dto.setName(pe.getExercise().getName());
        dto.setType(pe.getExercise().getType().name());

        dto.setSets(pe.getSets());
        dto.setReps(pe.getReps());
        dto.setWeight(pe.getWeight());
        dto.setDuration(pe.getDuration());

        return dto;
    }



}