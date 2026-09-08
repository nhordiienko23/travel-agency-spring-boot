package com.epam.finaltask.voucher;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public record VoucherSearchRequestDTO(
        String keyword,
        String tourType,
        String transferType,
        String hotelType,
        Double maxPrice,
        Boolean isHot,
        String status,

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate dateFrom,

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate dateTo
) {
}