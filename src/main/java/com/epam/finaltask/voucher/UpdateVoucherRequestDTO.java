package com.epam.finaltask.voucher;

import jakarta.validation.constraints.*;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

@Builder
public record UpdateVoucherRequestDTO(

        @NotBlank(message = "{val.title.req}")
        @Size(
                max = 255,
                message = "{val.title.size}"
        )
        String title,

        @NotBlank(message = "{val.desc.req}")
        @Size(
                max = 255,
                message = "{val.desc.size}"
        )
        String description,

        @NotNull(message = "{val.price.req}")
        @Positive(message = "{val.price.pos}")
        Double price,

        @NotBlank(message = "{val.tourType.req}")
        String tourType,

        @NotBlank(message = "{val.transferType.req}")
        String transferType,

        @NotBlank(message = "{val.hotelType.req}")
        String hotelType,

        @NotNull(message = "{val.arrivalDate.req}")
        @FutureOrPresent(message = "{val.arrivalDate.future}")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        java.time.LocalDate arrivalDate,

        @NotNull(message = "{val.evictionDate.req}")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        java.time.LocalDate evictionDate,

        Boolean isHot,

        VoucherStatus status
) {
}

