package com.epam.finaltask.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequestDTO(

        @NotBlank(message = "{val.username.req}")
        String username,

        @NotBlank(message = "{val.password.req}")
        String password
) {
}

