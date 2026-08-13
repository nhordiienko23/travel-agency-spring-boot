package com.epam.finaltask.service;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper userMapper;

	@Override
	@Transactional
	public UserDTO register(UserDTO userDTO) {
		if (userRepository.findUserByUsername(userDTO.getUsername()).isPresent()) {
			throw new IllegalArgumentException("Username is already taken");
		}

		if (userDTO.getPassword() == null || userDTO.getPassword().length() < 4) {
			throw new IllegalArgumentException("Password must be at least 4 characters long");
		}

		User user = userMapper.toUser(userDTO);

		user.setEmail(userDTO.getEmail());
		user.setLastName(userDTO.getLastName());
		user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		user.setRole(Role.USER);
		user.setActive(true);
		if (user.getBalance() == null) {
			user.setBalance(5000.0);
		}

		User savedUser = userRepository.save(user);
		return userMapper.toUserDTO(savedUser);
	}

	@Override
	public UserDTO getUserById(UUID id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("User not found"));
		return userMapper.toUserDTO(user);
	}

	@Override
	public UserDTO getUserByUsername(String username) {
		User user = userRepository.findUserByUsername(username)
				.orElseThrow(() -> new RuntimeException("User not found"));
		return userMapper.toUserDTO(user);
	}

	@Override
	@Transactional
	public UserDTO changeAccountStatus(UserDTO userDTO) {
		// ИСПРАВЛЕНО: Платформа ожидает поиск по ID, а не по Username
		User user = userRepository.findById(UUID.fromString(userDTO.getId()))
				.orElseThrow(() -> new RuntimeException("User not found"));


		userMapper.toUser(userDTO);

		user.setActive(userDTO.isActive());
		User savedUser = userRepository.save(user);
		return userMapper.toUserDTO(savedUser);
	}

	@Override
	public Page<User> findUsers(String keyword, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		if (keyword != null && !keyword.isEmpty()) {
			return userRepository.searchUsers(keyword, pageable);
		}
		return userRepository.findAll(pageable);
	}

	@Override
	@Transactional
	public void toggleUserStatus(String username) {
		User user = userRepository.findUserByUsername(username)
				.orElseThrow(() -> new RuntimeException("User not found"));

		user.setActive(!user.isActive());
		userRepository.save(user);
	}

	@Override
	@Transactional
	public void updateUserProfile(String username, UserDTO userDTO) {
		User user = userRepository.findUserByUsername(username)
				.orElseThrow(() -> new RuntimeException("User not found"));

		user.setEmail(userDTO.getEmail());
		user.setLastName(userDTO.getLastName());
		user.setPhoneNumber(userDTO.getPhoneNumber());

		if (userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()) {
			if (userDTO.getPassword().length() < 4) {
				throw new IllegalArgumentException("Password must be at least 4 characters long");
			}
			user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		}

		userRepository.save(user);
	}
}