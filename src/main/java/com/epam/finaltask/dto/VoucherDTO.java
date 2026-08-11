package com.epam.finaltask.dto;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

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
	@FutureOrPresent(message = "Arrival date cannot be in the past")
	private LocalDate arrivalDate;

	@NotNull(message = "Eviction date is required")
	@FutureOrPresent(message = "Eviction date cannot be in the past")
	private LocalDate evictionDate;

	private UUID userId;

	private Boolean isHot;

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public Double getPrice() { return price; }
	public void setPrice(Double price) { this.price = price; }
	public String getTourType() { return tourType; }
	public void setTourType(String tourType) { this.tourType = tourType; }
	public String getTransferType() { return transferType; }
	public void setTransferType(String transferType) { this.transferType = transferType; }
	public String getHotelType() { return hotelType; }
	public void setHotelType(String hotelType) { this.hotelType = hotelType; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
	public LocalDate getArrivalDate() { return arrivalDate; }
	public void setArrivalDate(LocalDate arrivalDate) { this.arrivalDate = arrivalDate; }
	public LocalDate getEvictionDate() { return evictionDate; }
	public void setEvictionDate(LocalDate evictionDate) { this.evictionDate = evictionDate; }
	public UUID getUserId() { return userId; }
	public void setUserId(UUID userId) { this.userId = userId; }
	public Boolean getIsHot() { return isHot; }
	public void setIsHot(Boolean isHot) { this.isHot = isHot; }
}