package com.epam.finaltask.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ChangePasswordRequestDTO(

        @NotBlank(message = "{val.currentPassword.req}")
        String currentPassword,

        @NotBlank(message = "{val.password.req}")
        @Size(
                min = 8,
                message = "{val.password.size}"
        )
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
                message = "{val.password.strong}"
        )
        String newPassword
) {
}

