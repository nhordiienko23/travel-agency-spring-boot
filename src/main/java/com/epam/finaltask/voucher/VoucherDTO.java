package com.epam.finaltask.voucher;

import lombok.Builder;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record VoucherDTO(
		String id,
		String title,
		String description,
		Double price,
		String tourType,
		String transferType,
		String hotelType,
		VoucherStatus status,
		LocalDate arrivalDate,
		LocalDate evictionDate,
		UUID userId,
		Boolean isHot
) {
}