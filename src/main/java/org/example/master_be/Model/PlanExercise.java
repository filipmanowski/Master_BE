package org.example.master_be.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "plan_exercises")
public class PlanExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer sets;
    private Integer reps;
    private Double weight;
    private Integer duration;

    private Integer orderIndex;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private WorkoutPlan plan;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private UserExercise exercise;

}