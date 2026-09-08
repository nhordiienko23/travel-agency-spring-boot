package com.epam.finaltask.auth;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestDTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation
                .buildDefaultValidatorFactory()
                .getValidator();
    }

    @Test
    void validRequest_ShouldHaveNoViolations() {

        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("john")
                .password("password123")
                .build();

        assertTrue(
                validator.validate(request).isEmpty()
        );
    }

    @Test
    void username_ShouldRejectNull() {

        LoginRequestDTO request = LoginRequestDTO.builder()
                .username(null)
                .password("password")
                .build();

        assertFalse(
                validator.validate(request).isEmpty()
        );
    }

    @Test
    void username_ShouldRejectBlank() {

        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("")
                .password("password")
                .build();

        assertFalse(
                validator.validate(request).isEmpty()
        );
    }

    @Test
    void username_ShouldRejectWhitespace() {

        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("   ")
                .password("password")
                .build();

        assertFalse(
                validator.validate(request).isEmpty()
        );
    }

    @Test
    void password_ShouldRejectNull() {

        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("john")
                .password(null)
                .build();

        assertFalse(
                validator.validate(request).isEmpty()
        );
    }

    @Test
    void password_ShouldRejectBlank() {

        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("john")
                .password("")
                .build();

        assertFalse(
                validator.validate(request).isEmpty()
        );
    }

    @Test
    void password_ShouldRejectWhitespace() {

        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("john")
                .password("   ")
                .build();

        assertFalse(
                validator.validate(request).isEmpty()
        );
    }


    @Test
    void builder_toString_shouldReturnString() {

        LoginRequestDTO.LoginRequestDTOBuilder builder =
                LoginRequestDTO.builder()
                        .username("john")
                        .password("password123");

        assertNotNull(
                builder.toString()
        );
    }


}