package org.example.master_be.Controller;
import lombok.RequiredArgsConstructor;
import org.example.master_be.Model.Exercise;
import org.example.master_be.Service.ExerciseService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.example.master_be.Model.ExerciseType;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users/{userId}/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService service;

    @PostMapping
    public Exercise create(@PathVariable Long userId,
                           @RequestBody Exercise exercise) {
        return service.createExercise(userId, exercise);
    }

    @GetMapping
    public List<Exercise> getAll(@PathVariable Long userId) {
        return service.getUserExercises(userId);
    }

    @GetMapping("/{id}")
    public Exercise getOne(@PathVariable Long userId,
                           @PathVariable Long id) {
        return service.getById(id, userId);
    }

    @PutMapping("/{id}")
    public Exercise update(@PathVariable Long userId,
                           @PathVariable Long id,
                           @RequestBody Exercise exercise) {
        return service.update(id, userId, exercise);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long userId,
                       @PathVariable Long id) {
        service.delete(id, userId);
    }

    @GetMapping("/type/{type}")
    public List<Exercise> getByType(@PathVariable Long userId,
                                    @PathVariable ExerciseType type) {
        return service.getByType(userId, type);
    }
}