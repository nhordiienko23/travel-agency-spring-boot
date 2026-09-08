package com.epam.finaltask.voucher;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
public record CreateVoucherRequestDTO(
        @NotBlank(message = "{val.title.req}")
        @Size(max = 255, message = "{val.title.size}")
        String title,

        @NotBlank(message = "{val.desc.req}")
        @Size(max = 255, message = "{val.desc.size}")
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
        LocalDate arrivalDate,

        @NotNull(message = "{val.evictionDate.req}")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate evictionDate
) {
}