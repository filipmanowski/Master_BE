package org.example.master_be.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "persons")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "gender")
    private String gender;

    @Column(name = "age")
    private Integer age;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "height")
    private Integer height;

    @Column(name = "activity_level")
    private String activityLevel;

    @Column(name = "sleep_hours")
    private Double sleepHours;

    @Column(name = "waist_circumference")
    private Double waistCircumference;

    @Column(name = "hips_circumference")
    private Double hipsCircumference;

    @Column(name = "thigh_circumference")
    private Double thighCircumference;

    @Column(name = "biceps_circumference")
    private Double bicepsCircumference;

    @Column(name = "chest_circumference")
    private Double chestCircumference;
}