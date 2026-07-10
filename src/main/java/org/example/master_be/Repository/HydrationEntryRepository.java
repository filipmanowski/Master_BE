package org.example.master_be.Repository;

import org.example.master_be.Model.HydrationEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HydrationEntryRepository extends JpaRepository<HydrationEntry, Long> {
    List<HydrationEntry> findByUserIdOrderByDrankAtDesc(Long userId);

    List<HydrationEntry> findByUserIdAndDrankAtBetweenOrderByDrankAtAsc(
            Long userId,
            LocalDateTime from,
            LocalDateTime to
    );
}
