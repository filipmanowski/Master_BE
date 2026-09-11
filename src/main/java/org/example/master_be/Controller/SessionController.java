package org.example.master_be.Controller;

import lombok.RequiredArgsConstructor;
import org.example.master_be.Config.AuthUtil;
import org.example.master_be.DTO.SessionResponse;
import org.example.master_be.DTO.WorkoutHistoryResponse;
import org.example.master_be.Service.SessionService;
import org.springframework.web.bind.annotation.*;
import org.example.master_be.DTO.StartSessionRequest;
import org.example.master_be.DTO.SaveExerciseRequest;

import java.util.List;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService service;
    private final AuthUtil authUtil;

    @PostMapping("/start")
    public SessionResponse start(@RequestBody StartSessionRequest req) {
        Long userId = authUtil.getCurrentUserId();
        return service.startSession(userId, req.getPlanId());
    }

    @PostMapping("/exercise")
    public WorkoutHistoryResponse saveExercise(@RequestBody SaveExerciseRequest req) {
        return service.saveExercise(authUtil.getCurrentUserId(), req);
    }

    @PostMapping("/end/{sessionId}")
    public SessionResponse end(@PathVariable Long sessionId) {
        return service.endSession(authUtil.getCurrentUserId(), sessionId);
    }

    @GetMapping("/history")
    public List<WorkoutHistoryResponse> history() {
        return service.getHistory(authUtil.getCurrentUserId());
    }
}
