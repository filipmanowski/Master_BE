package org.example.master_be.Repository;

import org.example.master_be.Model.NutritionEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NutritionEntryRepository extends JpaRepository<NutritionEntry, Long> {
    List<NutritionEntry> findByUserIdOrderByConsumedAtDesc(Long userId);

    List<NutritionEntry> findByUserIdAndConsumedAtBetweenOrderByConsumedAtAsc(
            Long userId,
            LocalDateTime from,
            LocalDateTime to
    );

    Optional<NutritionEntry> findById(Long id);

}
