package org.example.master_be.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FrequentItemResponse {
    private Long id;
    private String name;
    private Long count;
}
