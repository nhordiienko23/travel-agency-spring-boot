package com.epam.finaltask.user;

import com.epam.finaltask.voucher.Voucher;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(unique = true, nullable = false)
	private String username;

	@Column(unique = true, nullable = false)
	private String email;

	private String password;

	@Column(name = "last_name")
	private String lastName;

	private String phoneNumber;

	private Double balance;

	@Enumerated(EnumType.STRING)
	private Role role;

	@Builder.Default
	@Column(nullable = false)
	private boolean active = true;

	@OneToMany(
			mappedBy = "user",
			fetch = FetchType.LAZY
	)
	private List<Voucher> vouchers;
}