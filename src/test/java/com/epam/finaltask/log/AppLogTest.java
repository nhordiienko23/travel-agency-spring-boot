package com.epam.finaltask.log;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AppLogTest {

    @Test
    void noArgsConstructor_shouldCreateObject() {
        AppLog log = new AppLog();

        assertNotNull(log);
        assertNull(log.getId());
        assertNull(log.getLogLevel());
        assertNull(log.getTargetMethod());
        assertNull(log.getMessage());
        assertNull(log.getTimestamp());
    }

    @Test
    void allArgsConstructor_shouldSetAllFields() {
        UUID id = UUID.randomUUID();
        LocalDateTime timestamp = LocalDateTime.now();

        AppLog log = new AppLog(
                id,
                "INFO",
                "com.epam.finaltask.user.UserServiceImpl.depositBalance",
                "Test message",
                timestamp
        );

        assertEquals(id, log.getId());
        assertEquals("INFO", log.getLogLevel());
        assertEquals(
                "com.epam.finaltask.user.UserServiceImpl.depositBalance",
                log.getTargetMethod()
        );
        assertEquals("Test message", log.getMessage());
        assertEquals(timestamp, log.getTimestamp());
    }

    @Test
    void settersAndGetters_shouldWork() {
        AppLog log = new AppLog();

        UUID id = UUID.randomUUID();
        LocalDateTime timestamp = LocalDateTime.now();

        log.setId(id);
        log.setLogLevel("WARN");
        log.setTargetMethod("test.method");
        log.setMessage("Warning");
        log.setTimestamp(timestamp);

        assertEquals(id, log.getId());
        assertEquals("WARN", log.getLogLevel());
        assertEquals("test.method", log.getTargetMethod());
        assertEquals("Warning", log.getMessage());
        assertEquals(timestamp, log.getTimestamp());
    }

    @Test
    void builder_shouldCreateObject() {
        UUID id = UUID.randomUUID();
        LocalDateTime timestamp = LocalDateTime.now();

        AppLog log = AppLog.builder()
                .id(id)
                .logLevel("INFO")
                .targetMethod("test.method")
                .message("Message")
                .timestamp(timestamp)
                .build();

        assertEquals(id, log.getId());
        assertEquals("INFO", log.getLogLevel());
        assertEquals("test.method", log.getTargetMethod());
        assertEquals("Message", log.getMessage());
        assertEquals(timestamp, log.getTimestamp());
    }

    @Test
    void differentInstances_shouldNotBeEqual() {
        UUID id = UUID.randomUUID();
        LocalDateTime timestamp = LocalDateTime.now();

        AppLog first = AppLog.builder()
                .id(id)
                .logLevel("INFO")
                .targetMethod("test.method")
                .message("Message")
                .timestamp(timestamp)
                .build();

        AppLog second = AppLog.builder()
                .id(id)
                .logLevel("INFO")
                .targetMethod("test.method")
                .message("Message")
                .timestamp(timestamp)
                .build();

        assertNotSame(first, second);
        assertNotEquals(first, second);
    }

    @Test
    void equals_shouldReturnFalseForDifferentValues() {
        AppLog first = AppLog.builder()
                .id(UUID.randomUUID())
                .logLevel("INFO")
                .targetMethod("method.one")
                .message("Message one")
                .timestamp(LocalDateTime.now())
                .build();

        AppLog second = AppLog.builder()
                .id(UUID.randomUUID())
                .logLevel("ERROR")
                .targetMethod("method.two")
                .message("Message two")
                .timestamp(LocalDateTime.now().plusSeconds(1))
                .build();

        assertNotEquals(first, second);
    }

    @Test
    void equals_shouldHandleNullAndDifferentClass() {
        AppLog log = new AppLog();

        assertNotEquals(log, null);
        assertNotEquals(log, "not an AppLog");
        assertEquals(log, log);
    }


    @Test
    void builder_shouldSetAllFields() {
        UUID id = UUID.randomUUID();
        LocalDateTime timestamp = LocalDateTime.now();

        AppLog log = AppLog.builder()
                .id(id)
                .logLevel("INFO")
                .targetMethod("TestService.testMethod")
                .message("Test message")
                .timestamp(timestamp)
                .build();

        assertEquals(id, log.getId());
        assertEquals("INFO", log.getLogLevel());
        assertEquals("TestService.testMethod", log.getTargetMethod());
        assertEquals("Test message", log.getMessage());
        assertEquals(timestamp, log.getTimestamp());
    }

    @Test
    void builder_toString_shouldReturnString() {
        AppLog.AppLogBuilder builder = AppLog.builder()
                .id(UUID.randomUUID())
                .logLevel("INFO")
                .targetMethod("TestService.testMethod")
                .message("Test message")
                .timestamp(LocalDateTime.now());

        assertNotNull(builder.toString());
    }



}

