package com.epam.finaltask.voucher;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ChangeVoucherStatusRequestDTO(

        @NotNull(message = "{val.status.req}")
        VoucherStatus status
) {
}

