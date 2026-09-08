package com.epam.finaltask.user;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperImplTest {

    private final UserMapperImpl mapper = new UserMapperImpl();

    // ========================================================================
    // NULL
    // ========================================================================

    @Test
    void toUserResponseDTO_shouldReturnNullForNullUser() {
        assertNull(mapper.toUserResponseDTO(null));
    }

    // ========================================================================
    // FULL MAPPING
    // ========================================================================

    @Test
    void toUserResponseDTO_shouldMapAllFields() {

        UUID id = UUID.randomUUID();

        User user = User.builder()
                .id(id)
                .username("john")
                .email("john@test.com")
                .lastName("Doe")
                .phoneNumber("+48123456789")
                .balance(100.0)
                .role(Role.ADMIN)
                .active(true)
                .build();

        UserResponseDTO result =
                mapper.toUserResponseDTO(user);

        assertNotNull(result);

        assertEquals(id.toString(), result.id());
        assertEquals("john", result.username());
        assertEquals("john@test.com", result.email());
        assertEquals("Doe", result.lastName());
        assertEquals("+48123456789", result.phoneNumber());
        assertEquals(100.0, result.balance());
        assertEquals("ADMIN", result.role());
        assertTrue(result.active());
    }

    // ========================================================================
    // NULL ID / NULL ROLE
    // ========================================================================

    @Test
    void toUserResponseDTO_shouldHandleNullIdAndRole() {

        User user = User.builder()
                .username("john")
                .email("john@test.com")
                .lastName("Doe")
                .phoneNumber("+48123456789")
                .balance(50.0)
                .role(null)
                .active(false)
                .build();

        UserResponseDTO result =
                mapper.toUserResponseDTO(user);

        assertNotNull(result);

        assertNull(result.id());
        assertEquals("john", result.username());
        assertEquals("john@test.com", result.email());
        assertEquals("Doe", result.lastName());
        assertEquals("+48123456789", result.phoneNumber());
        assertEquals(50.0, result.balance());
        assertNull(result.role());
        assertFalse(result.active());
    }

    // ========================================================================
    // ALL OTHER NULLABLE FIELDS
    // ========================================================================

    @Test
    void toUserResponseDTO_shouldMapNullValues() {

        User user = User.builder()
                .id(null)
                .username(null)
                .email(null)
                .lastName(null)
                .phoneNumber(null)
                .balance(null)
                .role(null)
                .active(false)
                .build();

        UserResponseDTO result =
                mapper.toUserResponseDTO(user);

        assertNotNull(result);

        assertNull(result.id());
        assertNull(result.username());
        assertNull(result.email());
        assertNull(result.lastName());
        assertNull(result.phoneNumber());
        assertNull(result.balance());
        assertNull(result.role());
        assertFalse(result.active());
    }

    // ========================================================================
    // ROLE MAPPING
    // ========================================================================

    @Test
    void toUserResponseDTO_shouldMapUserRole() {

        User user = User.builder()
                .role(Role.USER)
                .active(true)
                .build();

        UserResponseDTO result =
                mapper.toUserResponseDTO(user);

        assertEquals("USER", result.role());
        assertTrue(result.active());
    }

    @Test
    void toUserResponseDTO_shouldMapManagerRole() {

        User user = User.builder()
                .role(Role.MANAGER)
                .active(true)
                .build();

        UserResponseDTO result =
                mapper.toUserResponseDTO(user);

        assertEquals("MANAGER", result.role());
        assertTrue(result.active());
    }

    @Test
    void toUserResponseDTO_shouldMapAdminRole() {

        User user = User.builder()
                .role(Role.ADMIN)
                .active(true)
                .build();

        UserResponseDTO result =
                mapper.toUserResponseDTO(user);

        assertEquals("ADMIN", result.role());
        assertTrue(result.active());
    }

    // ========================================================================
    // ACTIVE FLAG
    // ========================================================================

    @Test
    void toUserResponseDTO_shouldMapActiveFalse() {

        User user = User.builder()
                .active(false)
                .build();

        UserResponseDTO result =
                mapper.toUserResponseDTO(user);

        assertFalse(result.active());
    }

    @Test
    void toUserResponseDTO_shouldMapActiveTrue() {

        User user = User.builder()
                .active(true)
                .build();

        UserResponseDTO result =
                mapper.toUserResponseDTO(user);

        assertTrue(result.active());
    }
}

