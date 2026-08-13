package com.epam.finaltask.token;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    // Используем тот же тестовый секретный ключ (минимум 256 бит), что и в application.properties
    private final String secret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final long expiration = 86400000L; // 1 день

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(secret, expiration);
    }

    @Test
    void generateToken_And_GetUsername_Success() {
        String username = "testuser";

        String token = jwtUtils.generateToken(username);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        String extractedUsername = jwtUtils.getUsernameFromToken(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        String token = jwtUtils.generateToken("testuser");

        boolean isValid = jwtUtils.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid.token";

        boolean isValid = jwtUtils.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    void validateToken_EmptyToken_ReturnsFalse() {
        boolean isValid = jwtUtils.validateToken("");

        assertFalse(isValid);
    }
}