package com.epam.finaltask.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

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

	@Column(nullable = false)
    private String username;
	@Column(nullable = false)
    private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
    private Role role;


	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Voucher> vouchers;


	@Column(nullable = false)
    private String phoneNumber;
	@Column(nullable = false)
    private Double balance;
	@Column(nullable = false)
    private boolean active;


    
}