package com.epam.finaltask.core.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ErrorPageControllerTest {

    private final ErrorPageController controller =
            new ErrorPageController();

    @Test
    void testError_shouldThrowRuntimeException() {

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> controller.testError()
                );

        assertEquals(
                "Test error",
                exception.getMessage()
        );
    }
}

