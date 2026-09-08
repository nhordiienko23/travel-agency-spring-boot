package com.epam.finaltask.auth;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestDTOValidationTest {

    private static final String STRONG_PASSWORD = "Password123!";

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation
                .buildDefaultValidatorFactory()
                .getValidator();
    }

    // ========================================================================
    // VALID DATA
    // ========================================================================

    @Test
    void shouldHaveNoViolations_WhenRequestIsValid() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .password(STRONG_PASSWORD)
                .lastName("Smith")
                .phoneNumber("+48123456789")
                .build();

        Set<ConstraintViolation<RegisterRequestDTO>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldAllowNullLastName() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("1234567890")
                .build();

        Set<ConstraintViolation<RegisterRequestDTO>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    // ========================================================================
    // USERNAME
    // ========================================================================

    @Test
    void username_ShouldRejectNull() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username(null)
                .email("john@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("1234567890")
                .build();

        assertHasViolation(request, "username");
    }

    @Test
    void username_ShouldRejectBlank() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("")
                .email("john@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("1234567890")
                .build();

        assertHasViolation(request, "username");
    }

    @Test
    void username_ShouldRejectWhitespace() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("   ")
                .email("john@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("1234567890")
                .build();

        assertHasViolation(request, "username");
    }

    @Test
    void username_ShouldAcceptOneCharacter() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("a")
                .email("john@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("1234567890")
                .build();

        assertNoViolation(request, "username");
    }

    @Test
    void username_ShouldAcceptTwoCharacters() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("ab")
                .email("john@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("1234567890")
                .build();

        assertNoViolation(request, "username");
    }

    @Test
    void username_ShouldAcceptThreeCharacters() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("abc")
                .email("john@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("1234567890")
                .build();

        assertNoViolation(request, "username");
    }

    @Test
    void username_ShouldAcceptFiftyCharacters() {

        String username = "a".repeat(50);

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username(username)
                .email("john@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("1234567890")
                .build();

        assertNoViolation(request, "username");
    }

    @Test
    void username_ShouldRejectFiftyOneCharacters() {

        String username = "a".repeat(51);

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username(username)
                .email("john@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("1234567890")
                .build();

        assertHasViolation(request, "username");
    }

    // ========================================================================
    // EMAIL
    // ========================================================================

    @Test
    void email_ShouldRejectNull() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email(null)
                .password(STRONG_PASSWORD)
                .phoneNumber("1234567890")
                .build();

        assertHasViolation(request, "email");
    }

    @Test
    void email_ShouldRejectBlank() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("")
                .password(STRONG_PASSWORD)
                .phoneNumber("1234567890")
                .build();

        assertHasViolation(request, "email");
    }

    @Test
    void email_ShouldRejectInvalidFormat() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("invalid-email")
                .password(STRONG_PASSWORD)
                .phoneNumber("1234567890")
                .build();

        assertHasViolation(request, "email");
    }

    @Test
    void email_ShouldAcceptValidEmail() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john.smith+test@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("1234567890")
                .build();

        assertNoViolation(request, "email");
    }

    // ========================================================================
    // PASSWORD
    // ========================================================================

    @Test
    void password_ShouldRejectNull() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .password(null)
                .phoneNumber("1234567890")
                .build();

        assertHasViolation(request, "password");
    }

    @Test
    void password_ShouldRejectBlank() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .password("")
                .phoneNumber("1234567890")
                .build();

        assertHasViolation(request, "password");
    }

    @Test
    void password_ShouldRejectPasswordShorterThanEightCharacters() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .password("Ab1!")
                .phoneNumber("1234567890")
                .build();

        assertHasViolation(request, "password");
    }

    @Test
    void password_ShouldAcceptStrongEightCharacterPassword() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .password("Abcd123!")
                .phoneNumber("1234567890")
                .build();

        assertNoViolation(request, "password");
    }

    @Test
    void password_ShouldRejectPasswordWithoutUppercaseLetter() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .password("password123!")
                .phoneNumber("1234567890")
                .build();

        assertHasViolation(request, "password");
    }

    @Test
    void password_ShouldRejectPasswordWithoutLowercaseLetter() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .password("PASSWORD123!")
                .phoneNumber("1234567890")
                .build();

        assertHasViolation(request, "password");
    }

    @Test
    void password_ShouldRejectPasswordWithoutDigit() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .password("Password!")
                .phoneNumber("1234567890")
                .build();

        assertHasViolation(request, "password");
    }

    @Test
    void password_ShouldRejectPasswordWithoutSpecialCharacter() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .password("Password1234")
                .phoneNumber("1234567890")
                .build();

        assertHasViolation(request, "password");
    }

    @Test
    void password_ShouldAcceptLongStrongPassword() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .password("VeryStrongPassword123!")
                .phoneNumber("1234567890")
                .build();

        assertNoViolation(request, "password");
    }

    // ========================================================================
    // PHONE NUMBER
    // ========================================================================

    @Test
    void phone_ShouldRejectNull() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber(null)
                .build();

        assertHasViolation(request, "phoneNumber");
    }

    @Test
    void phone_ShouldRejectBlank() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("")
                .build();

        assertHasViolation(request, "phoneNumber");
    }

    @Test
    void phone_ShouldAcceptTenDigits() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("1234567890")
                .build();

        assertNoViolation(request, "phoneNumber");
    }

    @Test
    void phone_ShouldAcceptFifteenDigits() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("123456789012345")
                .build();

        assertNoViolation(request, "phoneNumber");
    }

    @Test
    void phone_ShouldAcceptPlusAndTenDigits() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("+1234567890")
                .build();

        assertNoViolation(request, "phoneNumber");
    }

    @Test
    void phone_ShouldAcceptPlusAndFifteenDigits() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("+123456789012345")
                .build();

        assertNoViolation(request, "phoneNumber");
    }

    @Test
    void phone_ShouldRejectNineDigits() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("123456789")
                .build();

        assertHasViolation(request, "phoneNumber");
    }

    @Test
    void phone_ShouldRejectSixteenDigits() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("1234567890123456")
                .build();

        assertHasViolation(request, "phoneNumber");
    }

    @Test
    void phone_ShouldRejectLetters() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("12345abcde")
                .build();

        assertHasViolation(request, "phoneNumber");
    }

    @Test
    void phone_ShouldRejectSpaces() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("123 4567890")
                .build();

        assertHasViolation(request, "phoneNumber");
    }

    @Test
    void phone_ShouldRejectPlusInMiddle() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("123+4567890")
                .build();

        assertHasViolation(request, "phoneNumber");
    }

    // ========================================================================
    // HELPERS
    // ========================================================================

    private void assertHasViolation(
            RegisterRequestDTO request,
            String property
    ) {

        Set<ConstraintViolation<RegisterRequestDTO>> violations =
                validator.validate(request);

        assertTrue(
                violations.stream()
                        .anyMatch(v -> v.getPropertyPath()
                                .toString()
                                .equals(property)),
                "Expected violation for property: " + property
        );
    }

    private void assertNoViolation(
            RegisterRequestDTO request,
            String property
    ) {

        Set<ConstraintViolation<RegisterRequestDTO>> violations =
                validator.validate(request);

        assertFalse(
                violations.stream()
                        .anyMatch(v -> v.getPropertyPath()
                                .toString()
                                .equals(property)),
                "Expected no violation for property: " + property
        );
    }

    // ========================================================================
    // LOMBOK BUILDER
    // ========================================================================

    @Test
    void builder_toString_shouldReturnString() {

        RegisterRequestDTO.RegisterRequestDTOBuilder builder =
                RegisterRequestDTO.builder()
                        .username("john")
                        .email("john@gmail.com")
                        .password(STRONG_PASSWORD)
                        .lastName("Smith")
                        .phoneNumber("+48123456789");

        assertNotNull(builder.toString());
    }
}

