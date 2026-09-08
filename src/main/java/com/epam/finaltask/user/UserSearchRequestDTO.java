package com.epam.finaltask.user;

public record UserSearchRequestDTO(
        String username,
        String phoneNumber,
        String role,
        Boolean active
) {
}