package com.epam.finaltask.mapper;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.model.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperImplTest {

    private final UserMapperImpl userMapper = new UserMapperImpl();

    @Test
    void toUser_ValidDTO_ReturnsUser() {
        UserDTO dto = new UserDTO();
        dto.setId(UUID.randomUUID().toString());
        dto.setUsername("john");
        dto.setRole("ADMIN");
        dto.setActive(true);

        User user = userMapper.toUser(dto);

        assertNotNull(user);
        assertEquals("john", user.getUsername());
        assertEquals(Role.ADMIN, user.getRole());
        assertTrue(user.isActive());
    }

    @Test
    void toUserDTO_ValidUser_ReturnsDTO() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("alice");
        user.setRole(Role.MANAGER);

        UserDTO dto = userMapper.toUserDTO(user);

        assertNotNull(dto);
        assertEquals("alice", dto.getUsername());
        assertEquals("MANAGER", dto.getRole());
    }

    @Test
    void nullChecks() {
        assertNull(userMapper.toUser(null));
        assertNull(userMapper.toUserDTO(null));
    }
}