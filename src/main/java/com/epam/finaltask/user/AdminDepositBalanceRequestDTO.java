package com.epam.finaltask.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.UUID;

@Builder
public record AdminDepositBalanceRequestDTO(

        @NotNull(message = "{val.userId.req}")
        UUID userId,

        @NotNull(message = "{val.amount.req}")
        @Positive(message = "{val.amount.pos}")
        Double amount
) {
}

