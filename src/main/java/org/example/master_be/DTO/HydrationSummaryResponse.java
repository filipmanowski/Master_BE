package org.example.master_be.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class HydrationSummaryResponse {
    private LocalDate from;
    private LocalDate to;
    private Integer totalMl;
    private Double averageMl;
}
