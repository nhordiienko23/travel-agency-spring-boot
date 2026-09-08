package com.epam.finaltask.user;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ChangeAccountStatusRequestDTO(

        @NotNull(message = "{val.userId.req}")
        UUID id,

        @NotNull(message = "{val.status.req}")
        Boolean active
) {
}

