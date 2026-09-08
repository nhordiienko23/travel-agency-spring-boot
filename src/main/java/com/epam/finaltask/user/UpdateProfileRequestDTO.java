package com.epam.finaltask.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateProfileRequestDTO(

        @NotBlank(message = "{val.username.req}")
        @Size(
                max = 50,
                message = "{val.username.size}"
        )
        String username,

        @NotBlank(message = "{val.email.req}")
        @Email(message = "{val.email.invalid}")
        String email,

        String lastName,

        @NotBlank(message = "{val.phone.req}")
        @Pattern(
                regexp = "^\\+?[0-9]{10,15}$",
                message = "{val.phone.invalid}"
        )
        String phoneNumber
) {
}

