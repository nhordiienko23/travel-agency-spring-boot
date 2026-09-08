package com.epam.finaltask.log;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class LoggableTest {

    @Test
    void annotation_shouldContainActionValue() throws Exception {
        Method method = TestTarget.class.getDeclaredMethod("testMethod");

        Loggable annotation = method.getAnnotation(Loggable.class);

        assertNotNull(annotation);
        assertEquals("TEST_ACTION", annotation.value());
    }

    static class TestTarget {

        @Loggable("TEST_ACTION")
        void testMethod() {
        }
    }
}