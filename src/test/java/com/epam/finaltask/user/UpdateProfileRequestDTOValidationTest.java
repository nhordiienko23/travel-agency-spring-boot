package com.epam.finaltask.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UpdateProfileRequestDTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation
                .buildDefaultValidatorFactory()
                .getValidator();
    }

    // ========================================================================
    // USERNAME
    // ========================================================================

    @Test
    void username_shouldRejectNull() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .username(null)
                        .build();

        assertHasViolation(
                request,
                "username"
        );

        assertHasViolationWithTemplate(
                request,
                "username",
                "{val.username.req}"
        );
    }

    @Test
    void username_shouldRejectBlank() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .username("   ")
                        .build();

        assertHasViolation(
                request,
                "username"
        );

        assertHasViolationWithTemplate(
                request,
                "username",
                "{val.username.req}"
        );
    }

    @Test
    void username_shouldAcceptOneCharacter() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .username("a")
                        .build();

        assertNoViolation(
                request,
                "username"
        );
    }

    @Test
    void username_shouldAcceptTwoCharacters() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .username("ab")
                        .build();

        assertNoViolation(
                request,
                "username"
        );
    }

    @Test
    void username_shouldAcceptThreeCharacters() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .username("abc")
                        .build();

        assertNoViolation(
                request,
                "username"
        );
    }

    @Test
    void username_shouldAcceptFiftyCharacters() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .username("a".repeat(50))
                        .build();

        assertNoViolation(
                request,
                "username"
        );
    }

    @Test
    void username_shouldRejectFiftyOneCharacters() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .username("a".repeat(51))
                        .build();

        assertHasViolation(
                request,
                "username"
        );

        assertHasViolationWithTemplate(
                request,
                "username",
                "{val.username.size}"
        );
    }

    // ========================================================================
    // EMAIL
    // ========================================================================

    @Test
    void email_shouldRejectNull() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .email(null)
                        .build();

        assertHasViolation(
                request,
                "email"
        );

        assertHasViolationWithTemplate(
                request,
                "email",
                "{val.email.req}"
        );
    }

    @Test
    void email_shouldRejectBlank() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .email("   ")
                        .build();

        assertHasViolation(
                request,
                "email"
        );
    }

    @Test
    void email_shouldRejectInvalidFormat() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .email("invalid-email")
                        .build();

        assertHasViolation(
                request,
                "email"
        );

        assertHasViolationWithTemplate(
                request,
                "email",
                "{val.email.invalid}"
        );
    }

    @Test
    void email_shouldRejectAnotherInvalidFormat() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .email("john@")
                        .build();

        assertHasViolation(
                request,
                "email"
        );

        assertHasViolationWithTemplate(
                request,
                "email",
                "{val.email.invalid}"
        );
    }

    @Test
    void email_shouldAcceptValidEmail() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .email("john@gmail.com")
                        .build();

        assertNoViolation(
                request,
                "email"
        );
    }

    @Test
    void email_shouldAcceptValidComplexEmail() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .email("john.smith+test@example.co.uk")
                        .build();

        assertNoViolation(
                request,
                "email"
        );
    }

    // ========================================================================
    // LAST NAME
    // ========================================================================

    @Test
    void lastName_shouldAcceptNull() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .lastName(null)
                        .build();

        assertNoViolation(
                request,
                "lastName"
        );
    }

    @Test
    void lastName_shouldAcceptValue() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .lastName("Smith")
                        .build();

        assertNoViolation(
                request,
                "lastName"
        );
    }

    @Test
    void lastName_shouldAcceptBlankValue() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .lastName("   ")
                        .build();

        assertNoViolation(
                request,
                "lastName"
        );
    }

    // ========================================================================
    // PHONE NUMBER
    // ========================================================================

    @Test
    void phoneNumber_shouldRejectNull() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .phoneNumber(null)
                        .build();

        assertHasViolation(
                request,
                "phoneNumber"
        );

        assertHasViolationWithTemplate(
                request,
                "phoneNumber",
                "{val.phone.req}"
        );
    }

    @Test
    void phoneNumber_shouldRejectBlank() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .phoneNumber("   ")
                        .build();

        assertHasViolation(
                request,
                "phoneNumber"
        );
    }

    @Test
    void phoneNumber_shouldAcceptTenDigits() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .phoneNumber("1234567890")
                        .build();

        assertNoViolation(
                request,
                "phoneNumber"
        );
    }

    @Test
    void phoneNumber_shouldAcceptFifteenDigits() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .phoneNumber("123456789012345")
                        .build();

        assertNoViolation(
                request,
                "phoneNumber"
        );
    }

    @Test
    void phoneNumber_shouldAcceptPlusAndTenDigits() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .phoneNumber("+1234567890")
                        .build();

        assertNoViolation(
                request,
                "phoneNumber"
        );
    }

    @Test
    void phoneNumber_shouldAcceptPlusAndFifteenDigits() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .phoneNumber("+123456789012345")
                        .build();

        assertNoViolation(
                request,
                "phoneNumber"
        );
    }

    @Test
    void phoneNumber_shouldRejectNineDigits() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .phoneNumber("123456789")
                        .build();

        assertHasViolation(
                request,
                "phoneNumber"
        );

        assertHasViolationWithTemplate(
                request,
                "phoneNumber",
                "{val.phone.invalid}"
        );
    }

    @Test
    void phoneNumber_shouldRejectSixteenDigits() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .phoneNumber("1234567890123456")
                        .build();

        assertHasViolation(
                request,
                "phoneNumber"
        );

        assertHasViolationWithTemplate(
                request,
                "phoneNumber",
                "{val.phone.invalid}"
        );
    }

    @Test
    void phoneNumber_shouldRejectLetters() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .phoneNumber("12345abcde")
                        .build();

        assertHasViolation(
                request,
                "phoneNumber"
        );

        assertHasViolationWithTemplate(
                request,
                "phoneNumber",
                "{val.phone.invalid}"
        );
    }

    @Test
    void phoneNumber_shouldRejectSpaces() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .phoneNumber("123 456 7890")
                        .build();

        assertHasViolation(
                request,
                "phoneNumber"
        );

        assertHasViolationWithTemplate(
                request,
                "phoneNumber",
                "{val.phone.invalid}"
        );
    }

    @Test
    void phoneNumber_shouldRejectPlusInTheMiddle() {

        UpdateProfileRequestDTO request =
                validRequestBuilder()
                        .phoneNumber("123+4567890")
                        .build();

        assertHasViolation(
                request,
                "phoneNumber"
        );

        assertHasViolationWithTemplate(
                request,
                "phoneNumber",
                "{val.phone.invalid}"
        );
    }

    // ========================================================================
    // MESSAGE TEMPLATES
    // ========================================================================

    @Test
    void validationMessages_shouldUseExpectedMessageTemplates() {

        UpdateProfileRequestDTO usernameRequest =
                validRequestBuilder()
                        .username(null)
                        .build();

        UpdateProfileRequestDTO usernameSizeRequest =
                validRequestBuilder()
                        .username("a".repeat(51))
                        .build();

        UpdateProfileRequestDTO emailRequest =
                validRequestBuilder()
                        .email(null)
                        .build();

        UpdateProfileRequestDTO emailInvalidRequest =
                validRequestBuilder()
                        .email("invalid")
                        .build();

        UpdateProfileRequestDTO phoneRequest =
                validRequestBuilder()
                        .phoneNumber(null)
                        .build();

        UpdateProfileRequestDTO phoneInvalidRequest =
                validRequestBuilder()
                        .phoneNumber("123")
                        .build();

        assertHasViolationWithTemplate(
                usernameRequest,
                "username",
                "{val.username.req}"
        );

        assertHasViolationWithTemplate(
                usernameSizeRequest,
                "username",
                "{val.username.size}"
        );

        assertHasViolationWithTemplate(
                emailRequest,
                "email",
                "{val.email.req}"
        );

        assertHasViolationWithTemplate(
                emailInvalidRequest,
                "email",
                "{val.email.invalid}"
        );

        assertHasViolationWithTemplate(
                phoneRequest,
                "phoneNumber",
                "{val.phone.req}"
        );

        assertHasViolationWithTemplate(
                phoneInvalidRequest,
                "phoneNumber",
                "{val.phone.invalid}"
        );
    }

    // ========================================================================
    // HELPERS
    // ========================================================================

    private UpdateProfileRequestDTO.UpdateProfileRequestDTOBuilder
    validRequestBuilder() {

        return UpdateProfileRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .lastName("Smith")
                .phoneNumber("1234567890");
    }

    private void assertHasViolation(
            UpdateProfileRequestDTO request,
            String property
    ) {

        Set<ConstraintViolation<UpdateProfileRequestDTO>> violations =
                validator.validate(request);

        assertTrue(
                violations.stream()
                        .anyMatch(v ->
                                v.getPropertyPath()
                                        .toString()
                                        .equals(property)
                        ),
                "Expected violation for property: " + property
        );
    }

    private void assertHasViolationWithTemplate(
            UpdateProfileRequestDTO request,
            String property,
            String expectedTemplate
    ) {

        Set<ConstraintViolation<UpdateProfileRequestDTO>> violations =
                validator.validate(request);

        assertTrue(
                violations.stream()
                        .anyMatch(v ->
                                v.getPropertyPath()
                                        .toString()
                                        .equals(property)
                                        && v.getMessageTemplate()
                                        .equals(expectedTemplate)
                        ),
                "Expected violation with template "
                        + expectedTemplate
                        + " for property: "
                        + property
        );
    }

    private void assertNoViolation(
            UpdateProfileRequestDTO request,
            String property
    ) {

        Set<ConstraintViolation<UpdateProfileRequestDTO>> violations =
                validator.validate(request);

        assertFalse(
                violations.stream()
                        .anyMatch(v ->
                                v.getPropertyPath()
                                        .toString()
                                        .equals(property)
                        ),
                "Expected no violation for property: " + property
        );
    }
}

