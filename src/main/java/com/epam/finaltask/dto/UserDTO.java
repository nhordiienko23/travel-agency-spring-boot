package com.epam.finaltask.dto;

import java.util.List;
import com.epam.finaltask.model.Voucher;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class UserDTO {

	private String id;

	@NotBlank(message = "Username is required")
	@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
	private String username;

	@NotBlank(message = "Password is required")
	@Size(min = 4, message = "Password must be at least 4 characters long")
	private String password;

	@NotBlank(message = "Role is required")
	private String role;

	private List<Voucher> vouchers;

	@NotBlank(message = "Phone number is required")
	private String phoneNumber;

	@NotNull(message = "Balance is required")
	@PositiveOrZero(message = "Balance cannot be negative")
	private Double balance;

	private boolean active;


	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	public String getRole() { return role; }
	public void setRole(String role) { this.role = role; }
	public List<Voucher> getVouchers() { return vouchers; }
	public void setVouchers(List<Voucher> vouchers) { this.vouchers = vouchers; }
	public String getPhoneNumber() { return phoneNumber; }
	public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
	public Double getBalance() { return balance; }
	public void setBalance(Double balance) { this.balance = balance; }
	public boolean isActive() { return active; }
	public void setActive(boolean active) { this.active = active; }
}