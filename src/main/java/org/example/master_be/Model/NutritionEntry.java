package org.example.master_be.Model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "nutrition_entries")
public class NutritionEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "consumed_at", nullable = false)
    private LocalDateTime consumedAt = LocalDateTime.now();

    @Column(name = "meal_name", nullable = false)
    private String mealName;

    @Column(name = "description")
    private String description;

    @Column(name = "calories", nullable = false)
    private Integer calories;

    @Column(name = "protein", nullable = false)
    private Double protein;

    @Column(name = "carbohydrates", nullable = false)
    private Double carbohydrates;

    @Column(name = "fat", nullable = false)
    private Double fat;

    @OneToMany(mappedBy = "nutritionEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NutritionMicronutrient> micronutrients = new ArrayList<>();
}
