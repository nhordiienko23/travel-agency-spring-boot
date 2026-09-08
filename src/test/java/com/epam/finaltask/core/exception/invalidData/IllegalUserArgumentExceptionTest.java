
package com.epam.finaltask.core.exception.invalidData;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IllegalUserArgumentExceptionTest {

    @Test
    void constructor_shouldSetMessage() {

        String message = "Invalid user argument";

        IllegalUserArgumentException exception =
                new IllegalUserArgumentException(message);

        assertEquals(
                message,
                exception.getMessage()
        );
    }

    @Test
    void shouldExtendIllegalArgumentException() {

        IllegalUserArgumentException exception =
                new IllegalUserArgumentException(
                        "Invalid user argument"
                );

        assertInstanceOf(
                IllegalArgumentException.class,
                exception
        );
    }

    @Test
    void shouldExtendRuntimeException() {

        IllegalUserArgumentException exception =
                new IllegalUserArgumentException(
                        "Invalid user argument"
                );

        assertInstanceOf(
                RuntimeException.class,
                exception
        );
    }
}

