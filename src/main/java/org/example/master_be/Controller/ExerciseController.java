package org.example.master_be.Controller;
import lombok.RequiredArgsConstructor;
import org.example.master_be.Config.AuthUtil;
import org.example.master_be.Model.Exercise;
import org.example.master_be.Service.ExerciseService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.example.master_be.Model.ExerciseType;


@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService service;
    private final AuthUtil authUtil;

    @PostMapping
    public Exercise create(@RequestBody Exercise exercise) {
        Long userId = authUtil.getCurrentUserId();
        return service.createExercise(userId, exercise);
    }

    @GetMapping
    public List<Exercise> getAll() {
        Long userId = authUtil.getCurrentUserId();
        return service.getUserExercises(userId);
    }

    @GetMapping("/{id}")
    public Exercise getOne(@PathVariable Long id) {
        Long userId = authUtil.getCurrentUserId();
        return service.getById(id, userId);
    }

    @PutMapping("/{id}")
    public Exercise update(@PathVariable Long id,
                           @RequestBody Exercise exercise) {
        Long userId = authUtil.getCurrentUserId();
        return service.update(id, userId, exercise);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Long userId = authUtil.getCurrentUserId();
        service.delete(id, userId);
    }

    @GetMapping("/type/{type}")
    public List<Exercise> getByType(@PathVariable ExerciseType type) {
        Long userId = authUtil.getCurrentUserId();
        return service.getByType(userId, type);
    }
}