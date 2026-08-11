package com.epam.finaltask.mapper;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.model.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();

        if (dto.getId() != null && !dto.getId().isBlank()) {
            user.setId(UUID.fromString(dto.getId()));
        }

        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());

        if (dto.getRole() != null && !dto.getRole().isBlank()) {
            user.setRole(Role.valueOf(dto.getRole().toUpperCase()));
        }

        user.setPhoneNumber(dto.getPhoneNumber());
        user.setBalance(dto.getBalance());
        user.setAccountStatus(dto.isActive());

        return user;
    }

    @Override
    public UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();

        if (user.getId() != null) {
            dto.setId(user.getId().toString());
        }

        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());

        if (user.getRole() != null) {
            dto.setRole(user.getRole().name());
        }

        dto.setVouchers(user.getVouchers());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setBalance(user.getBalance());
        dto.setActive(user.isAccountStatus());

        return dto;
    }
}