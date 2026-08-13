package com.epam.finaltask.dto;

import java.util.List;
import com.epam.finaltask.model.Voucher;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

	private String id;

	@NotBlank(message = "Username is required")
	@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
	private String username;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Password is required")
	@Size(min = 4, message = "Password must be at least 4 characters long")
	private String password;

	private String lastName;

	@NotBlank(message = "Role is required")
	private String role;

	private List<Voucher> vouchers;

	@NotBlank(message = "Phone number is required")
	private String phoneNumber;

	@NotNull(message = "Balance is required")
	@PositiveOrZero(message = "Balance cannot be negative")
	private Double balance;

	private boolean active;
}