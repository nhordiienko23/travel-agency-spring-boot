package com.epam.finaltask.core.exception.notFound;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserNotFoundExceptionTest {

    @Test
    void constructor_shouldSetMessage() {

        String message = "User not found";

        UserNotFoundException exception =
                new UserNotFoundException(message);

        assertEquals(
                message,
                exception.getMessage()
        );
    }

    @Test
    void shouldExtendResourceNotFoundException() {

        UserNotFoundException exception =
                new UserNotFoundException("User not found");

        assertInstanceOf(
                ResourceNotFoundException.class,
                exception
        );
    }

    @Test
    void shouldBeRuntimeException() {

        UserNotFoundException exception =
                new UserNotFoundException("User not found");

        assertInstanceOf(
                RuntimeException.class,
                exception
        );
    }
}

