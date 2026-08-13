package com.epam.finaltask.service;

import java.util.List;
import java.util.UUID;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.model.User;
import org.springframework.data.domain.Page;

public interface UserService {
    UserDTO register(UserDTO userDTO);

    UserDTO getUserByUsername(String username);
    UserDTO changeAccountStatus(UserDTO userDTO);
    UserDTO getUserById(UUID id);
    Page<User> findUsers(String keyword, int page, int size);
    void toggleUserStatus(String username);
    void updateUserProfile(String username, UserDTO userDTO);
}
