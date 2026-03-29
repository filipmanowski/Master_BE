package org.example.master_be.Controller;

import lombok.RequiredArgsConstructor;
import org.example.master_be.Model.SessionExercise;
import org.example.master_be.Model.WorkoutSession;
import org.example.master_be.Service.SessionService;
import org.springframework.web.bind.annotation.*;
import org.example.master_be.DTO.StartSessionRequest;
import org.example.master_be.DTO.SaveExerciseRequest;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService service;

    @PostMapping("/start")
    public WorkoutSession start(@RequestBody StartSessionRequest req) {
        return service.startSession(req.getUserId(), req.getPlanId());
    }

    @PostMapping("/exercise")
    public SessionExercise saveExercise(@RequestBody SaveExerciseRequest req) {
        return service.saveExercise(req);
    }

    @PostMapping("/end/{sessionId}")
    public WorkoutSession end(@PathVariable Long sessionId) {
        return service.endSession(sessionId);
    }
}