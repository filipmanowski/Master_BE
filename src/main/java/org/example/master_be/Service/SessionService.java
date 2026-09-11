package org.example.master_be.Service;

import lombok.RequiredArgsConstructor;
import org.example.master_be.DTO.SaveExerciseRequest;
import org.example.master_be.DTO.SessionResponse;
import org.example.master_be.DTO.WorkoutHistoryResponse;
import org.example.master_be.Model.Exercise;
import org.example.master_be.Model.PerformedExercise;
import org.example.master_be.Model.WorkoutPlan;
import org.example.master_be.Model.WorkoutSession;
import org.example.master_be.Repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final WorkoutSessionRepository sessionRepo;
    private final PerformedExerciseRepository sessionExerciseRepo;
    private final UserRepository userRepo;
    private final WorkoutPlanRepository planRepo;
    private final ExerciseRepository exerciseRepo;

    public SessionResponse startSession(Long userId, Long planId) {
        if (planId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Plan id is required");
        }
        var session = new WorkoutSession();

        session.setUser(userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
        session.setPlan(planRepo.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found")));

        return mapSession(sessionRepo.save(session));
    }

    public WorkoutHistoryResponse saveExercise(Long userId, SaveExerciseRequest req) {
        validateSaveExerciseRequest(req);

        WorkoutSession session = sessionRepo.findByIdAndUserId(req.getSessionId(), userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));
        Exercise exercise = exerciseRepo.findByIdAndUserId(req.getExerciseId(), userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found"));

        PerformedExercise se = new PerformedExercise();

        se.setSession(session);
        se.setExercise(exercise);

        se.setSets(req.getSets());
        se.setReps(req.getReps());
        se.setWeight(req.getWeight());
        se.setDuration(req.getDuration());
        se.setCompleted(req.getCompleted() == null || req.getCompleted());

        return mapHistory(sessionExerciseRepo.save(se));
    }

    public SessionResponse endSession(Long userId, Long sessionId) {
        WorkoutSession session = sessionRepo.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));
        session.setEndedAt(LocalDateTime.now());
        return mapSession(sessionRepo.save(session));
    }

    @Transactional(readOnly = true)
    public List<WorkoutHistoryResponse> getHistory(Long userId) {
        return sessionExerciseRepo.findCompletedHistoryByUserId(userId)
                .stream()
                .map(this::mapHistory)
                .toList();
    }

    private void validateSaveExerciseRequest(SaveExerciseRequest req) {
        if (req == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Performed exercise is required");
        }
        if (req.getSessionId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session id is required");
        }
        if (req.getExerciseId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exercise id is required");
        }
        validateNonNegative(req.getSets(), "Sets");
        validateNonNegative(req.getReps(), "Reps");
        validateNonNegative(req.getWeight(), "Weight");
        validateNonNegative(req.getDuration(), "Duration");
    }

    private void validateNonNegative(Number value, String fieldName) {
        if (value != null && value.doubleValue() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " cannot be negative");
        }
    }

    private SessionResponse mapSession(WorkoutSession session) {
        SessionResponse dto = new SessionResponse();
        dto.setId(session.getId());
        dto.setStartedAt(session.getStartedAt());
        dto.setEndedAt(session.getEndedAt());
        if (session.getPlan() != null) {
            dto.setPlanId(session.getPlan().getId());
            dto.setPlanName(session.getPlan().getName());
        }
        return dto;
    }

    private WorkoutHistoryResponse mapHistory(PerformedExercise pe) {
        WorkoutHistoryResponse dto = new WorkoutHistoryResponse();
        WorkoutSession session = pe.getSession();
        WorkoutPlan plan = session.getPlan();
        dto.setSessionId(session.getId());
        dto.setPerformedExerciseId(pe.getId());
        dto.setExerciseId(pe.getExercise().getId());
        dto.setExerciseName(pe.getExercise().getName());
        dto.setPerformedAt(session.getStartedAt());
        dto.setEndedAt(session.getEndedAt());
        if (plan != null) {
            dto.setPlanId(plan.getId());
            dto.setPlanName(plan.getName());
        }
        dto.setSets(pe.getSets());
        dto.setReps(pe.getReps());
        dto.setWeight(pe.getWeight());
        dto.setDuration(pe.getDuration());
        dto.setVolume(calculateVolume(pe));
        dto.setScore(calculateScore(pe));
        return dto;
    }

    private double calculateVolume(PerformedExercise pe) {
        return intOrZero(pe.getSets()) * intOrZero(pe.getReps()) * doubleOrZero(pe.getWeight());
    }

    private double calculateScore(PerformedExercise pe) {
        double volume = calculateVolume(pe);
        if (volume > 0) {
            return volume;
        }
        if (pe.getDuration() != null && pe.getDuration() > 0) {
            return pe.getDuration().doubleValue();
        }
        return intOrZero(pe.getSets()) * intOrZero(pe.getReps());
    }

    private int intOrZero(Integer value) {
        return value == null ? 0 : value;
    }

    private double doubleOrZero(Double value) {
        return value == null ? 0.0 : value;
    }
}
