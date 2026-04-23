package org.example.master_be.Service;

import lombok.RequiredArgsConstructor;
import org.example.master_be.DTO.SaveExerciseRequest;
import org.example.master_be.Model.PerformedExercise;
import org.example.master_be.Model.WorkoutSession;
import org.example.master_be.Repository.*;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SessionService {

    private final WorkoutSessionRepository sessionRepo;
    private final PerformedExerciseRepository sessionExerciseRepo;
    private final UserRepository userRepo;
    private final WorkoutPlanRepository planRepo;
    private final ExerciseRepository exerciseRepo;

    public WorkoutSession startSession(Long userId, Long planId) {
        var session = new WorkoutSession();

        session.setUser(userRepo.findById(userId).orElseThrow());
        session.setPlan(planRepo.findById(planId).orElseThrow());

        return sessionRepo.save(session);
    }

    public PerformedExercise saveExercise(SaveExerciseRequest req) {
        PerformedExercise se = new PerformedExercise();

        se.setSession(sessionRepo.findById(req.getSessionId()).orElseThrow());
        se.setExercise(exerciseRepo.findById(req.getExerciseId()).orElseThrow());

        se.setSets(req.getSets());
        se.setReps(req.getReps());
        se.setWeight(req.getWeight());
        se.setDuration(req.getDuration());
        se.setCompleted(req.getCompleted());

        return sessionExerciseRepo.save(se);
    }

    public WorkoutSession endSession(Long sessionId) {
        WorkoutSession session = sessionRepo.findById(sessionId).orElseThrow();
        session.setEndedAt(java.time.LocalDateTime.now());
        return sessionRepo.save(session);
    }
}
