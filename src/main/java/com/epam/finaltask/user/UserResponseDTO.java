package com.epam.finaltask.user;

import lombok.Builder;

@Builder
public record UserResponseDTO(
        String id,
        String username,
        String email,
        String lastName,
        String phoneNumber,
        Double balance,
        String role,
        boolean active
) {
}