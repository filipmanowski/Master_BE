package org.example.master_be.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class HydrationEntryRequest {
    private LocalDateTime drankAt;
    private Integer amountMl;
}
