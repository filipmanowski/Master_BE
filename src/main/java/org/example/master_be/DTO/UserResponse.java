package org.example.master_be.DTO;

public record UserResponse(
        Long id,
        String email,
        boolean enabled,
        String role
) {}
