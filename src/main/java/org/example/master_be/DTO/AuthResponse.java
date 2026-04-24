package org.example.master_be.DTO;

public record AuthResponse(
        String token,
        Long id,
        String email,
        Boolean enabled,
        String role,
        boolean needsProfileCompletion
) {
}

