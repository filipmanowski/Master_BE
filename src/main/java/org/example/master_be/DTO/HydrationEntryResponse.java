package org.example.master_be.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class HydrationEntryResponse {
    private Long id;
    private LocalDateTime drankAt;
    private Integer amountMl;
}
