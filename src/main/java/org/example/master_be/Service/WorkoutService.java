package org.example.master_be.Service;

import lombok.RequiredArgsConstructor;
import org.example.master_be.DTO.PlanExerciseRequest;
import org.example.master_be.DTO.PlanExerciseResponse;
import org.example.master_be.DTO.WorkoutPlanRequest;
import org.example.master_be.Model.Exercise;
import org.example.master_be.Model.PlanExercise;
import org.example.master_be.Model.User;
import org.example.master_be.Model.WorkoutPlan;
import org.example.master_be.Repository.ExerciseRepository;
import org.example.master_be.Repository.PlanExerciseRepository;
import org.example.master_be.Repository.UserRepository;
import org.example.master_be.Repository.WorkoutPlanRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutPlanRepository planRepo;
    private final PlanExerciseRepository planExerciseRepo;
    private final ExerciseRepository exerciseRepo;
    private final UserRepository userRepo;

    public WorkoutPlan createPlan(Long userId, WorkoutPlanRequest request) {
        validatePlanRequest(request);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        WorkoutPlan plan = new WorkoutPlan();
        plan.setName(request.getName().trim());
        plan.setDescription(trimToNull(request.getDescription()));
        plan.setIsTemplate(Boolean.TRUE.equals(request.getIsTemplate()));
        plan.setUser(user);

        if (request.getParentPlanId() != null) {
            WorkoutPlan parent = planRepo.findByIdAndUserId(request.getParentPlanId(), userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent plan not found"));
            plan.setParentPlan(parent);
        }

        return planRepo.save(plan);
    }

    public List<WorkoutPlan> getUserPlans(Long userId) {
        return planRepo.findByUserId(userId);
    }

    @Transactional
    public void deletePlan(Long planId, Long userId) {
        WorkoutPlan plan = planRepo.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));
        planExerciseRepo.deleteByPlanId(plan.getId());
        planRepo.delete(plan);
    }

    public PlanExerciseResponse addExerciseToPlan(Long planId, Long userId, PlanExerciseRequest request) {
        validateExerciseRequest(request);

        WorkoutPlan plan = planRepo.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Exercise exercise = new Exercise();
        exercise.setName(request.getName().trim());
        exercise.setType(request.getType());
        exercise.setDescription(trimToNull(request.getDescription()));
        exercise.setUser(user);
        exercise = exerciseRepo.save(exercise);

        PlanExercise planExercise = new PlanExercise();
        planExercise.setPlan(plan);
        planExercise.setExercise(exercise);
        applyPlanExerciseFields(planExercise, request);

        return mapToDto(planExerciseRepo.save(planExercise));
    }

    public List<PlanExerciseResponse> getPlanExercisesDto(Long planId, Long userId) {
        planRepo.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));

        return planExerciseRepo.findByPlanId(planId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public PlanExerciseResponse updatePlanExercise(Long planExerciseId, Long userId, PlanExerciseRequest request) {
        validateExerciseRequest(request);

        PlanExercise planExercise = planExerciseRepo.findByIdAndPlanUserId(planExerciseId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found"));

        Exercise exercise = planExercise.getExercise();
        exercise.setName(request.getName().trim());
        exercise.setType(request.getType());
        exercise.setDescription(trimToNull(request.getDescription()));
        exerciseRepo.save(exercise);

        applyPlanExerciseFields(planExercise, request);
        return mapToDto(planExerciseRepo.save(planExercise));
    }

    private PlanExerciseResponse mapToDto(PlanExercise pe) {
        PlanExerciseResponse dto = new PlanExerciseResponse();

        dto.setId(pe.getId());
        dto.setExerciseId(pe.getExercise().getId());
        dto.setName(pe.getExercise().getName());
        dto.setType(pe.getExercise().getType().name());
        dto.setDescription(pe.getExercise().getDescription());

        dto.setSets(pe.getSets());
        dto.setReps(pe.getReps());
        dto.setWeight(pe.getWeight());
        dto.setDuration(pe.getDuration());
        dto.setOrderIndex(pe.getOrderIndex());

        return dto;
    }

    private void applyPlanExerciseFields(PlanExercise planExercise, PlanExerciseRequest request) {
        planExercise.setSets(request.getSets());
        planExercise.setReps(request.getReps());
        planExercise.setWeight(request.getWeight());
        planExercise.setDuration(request.getDuration());
        planExercise.setOrderIndex(request.getOrderIndex());
    }

    private void validatePlanRequest(WorkoutPlanRequest request) {
        if (request == null || request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Plan name is required");
        }
    }

    private void validateExerciseRequest(PlanExerciseRequest request) {
        if (request == null || request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exercise name is required");
        }
        if (request.getType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exercise type is required");
        }
        if (request.getSets() != null && request.getSets() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sets cannot be negative");
        }
        if (request.getReps() != null && request.getReps() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reps cannot be negative");
        }
        if (request.getWeight() != null && request.getWeight() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Weight cannot be negative");
        }
        if (request.getDuration() != null && request.getDuration() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duration cannot be negative");
        }
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
