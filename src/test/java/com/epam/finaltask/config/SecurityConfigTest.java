package com.epam.finaltask.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Убираем (classes = SecurityConfig.class), чтобы Spring Boot загрузил полный контекст, включая Web MVC
@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void securityFilterChainBeanExists() {
        assertNotNull(securityFilterChain, "SecurityFilterChain bean should be created successfully");
        assertTrue(securityFilterChain.getFilters().size() > 0, "Security filter chain should contain filters");
    }

    @Test
    void passwordEncoderBeanExists() {
        assertNotNull(passwordEncoder, "PasswordEncoder bean should be created successfully");
    }
}