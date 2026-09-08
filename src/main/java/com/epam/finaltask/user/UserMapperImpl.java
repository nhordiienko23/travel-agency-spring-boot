package com.epam.finaltask.user;

import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponseDTO toUserResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserResponseDTO.builder()
                .id(user.getId() != null ? user.getId().toString() : null)
                .username(user.getUsername())
                .email(user.getEmail())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .balance(user.getBalance())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .active(user.isActive())
                .build();
    }
}