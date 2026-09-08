package com.epam.finaltask.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record DepositBalanceRequestDTO(

        @NotNull(message = "{val.amount.req}")
        @Positive(message = "{val.amount.pos}")
        Double amount
) {
}

