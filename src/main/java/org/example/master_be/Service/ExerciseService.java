package org.example.master_be.Service;

import lombok.RequiredArgsConstructor;
import org.example.master_be.Model.Exercise;
import org.example.master_be.Model.ExerciseType;
import org.example.master_be.Repository.ExerciseRepository;
import org.example.master_be.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    public Exercise createExercise(Long userId, Exercise exercise){
        exercise.setUser(userRepository.findById(userId).orElseThrow());
        return exerciseRepository.save(exercise);
    }

    public List<Exercise> getUserExercises(Long userId){
        return exerciseRepository.findByUserId(userId);
    }

    public Exercise getById(Long id, Long userId){
        return exerciseRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));
    }

    public Exercise update(Long id, Long userId, Exercise updatedExercise){
        Exercise ex = getById(id, userId);

        ex.setName(updatedExercise.getName());
        ex.setType(updatedExercise.getType());
        ex.setDescription(updatedExercise.getDescription());

        return exerciseRepository.save(ex);
    }

    public void delete(Long id, Long userId){
        Exercise ex = getById(id, userId);
        exerciseRepository.delete(ex);
    }

    public List<Exercise> getByType(Long userId, ExerciseType type) {
        return exerciseRepository.findByUserIdAndType(userId, type);
    }
}