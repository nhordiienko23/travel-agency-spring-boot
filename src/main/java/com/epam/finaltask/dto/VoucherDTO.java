package com.epam.finaltask.dto;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherDTO {

	private String id;

	@NotBlank(message = "Title is required")
	private String title;

	@NotBlank(message = "Description is required")
	private String description;

	@NotNull(message = "Price is required")
	@Positive(message = "Price must be greater than zero")
	private Double price;

	@NotBlank(message = "Tour type is required")
	private String tourType;

	@NotBlank(message = "Transfer type is required")
	private String transferType;

	@NotBlank(message = "Hotel type is required")
	private String hotelType;

	private String status;

	@NotNull(message = "Arrival date is required")
	//@FutureOrPresent(message = "Arrival date cannot be in the past")
	private LocalDate arrivalDate;

	@NotNull(message = "Eviction date is required")
	//@FutureOrPresent(message = "Eviction date cannot be in the past")
	private LocalDate evictionDate;

	private UUID userId;

	private Boolean isHot;
}