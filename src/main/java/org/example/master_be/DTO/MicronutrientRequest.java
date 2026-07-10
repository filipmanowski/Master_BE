package org.example.master_be.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MicronutrientRequest {
    private String name;
    private Double amount;
    private String unit;
}
