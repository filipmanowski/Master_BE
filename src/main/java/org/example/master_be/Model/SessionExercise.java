package org.example.master_be.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "session_exercises")
public class SessionExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer sets;
    private Integer reps;
    private Double weight;
    private Integer duration;

    private Boolean completed = false;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private WorkoutSession session;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private UserExercise exercise;

}
