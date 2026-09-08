package com.epam.finaltask.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegisterRequestDTO(

        @NotBlank(message = "{val.username.req}")
        @Size(
                max = 50,
                message = "{val.username.size}"
        )
        String username,

        @NotBlank(message = "{val.email.req}")
        @Email(message = "{val.email.invalid}")
        String email,

        @NotBlank(message = "{val.password.req}")
        @Size(
                min = 8,
                message = "{val.password.size}"
        )
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
                message = "{val.password.strong}"
        )
        String password,

        String lastName,

        @NotBlank(message = "{val.phone.req}")
        @Pattern(
                regexp = "^\\+?[0-9]{10,15}$",
                message = "{val.phone.invalid}"
        )
        String phoneNumber
) {
}

