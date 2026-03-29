package org.example.master_be.DTO;

import lombok.Data;

@Data
public class StartSessionRequest {
    private Long userId;
    private Long planId;
}