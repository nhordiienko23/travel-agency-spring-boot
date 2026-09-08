package com.epam.finaltask.log;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppLogServiceImplTest {

    @Mock
    private AppLogRepository appLogRepository;

    private AppLogServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AppLogServiceImpl(appLogRepository);
    }

    @Test
    void logAsync_shouldCreateAndSaveLogEntry() {
        LogFormatDTO dto = LogFormatDTO.builder()
                .level("INFO")
                .method("com.epam.finaltask.user.UserServiceImpl.depositBalance")
                .args("amount=100.00")
                .build();

        service.logAsync(dto);

        ArgumentCaptor<AppLog> captor =
                ArgumentCaptor.forClass(AppLog.class);

        verify(appLogRepository).save(captor.capture());

        AppLog saved = captor.getValue();

        assertNotNull(saved);
        assertEquals("INFO", saved.getLogLevel());
        assertEquals(
                "com.epam.finaltask.user.UserServiceImpl.depositBalance",
                saved.getTargetMethod()
        );
        assertEquals("amount=100.00", saved.getMessage());
        assertNotNull(saved.getTimestamp());
    }

    @Test
    void logAsync_shouldStoreFormattedMessage() {
        LogFormatDTO dto = LogFormatDTO.builder()
                .level("WARN")
                .method("test.method")
                .result("User='admin' | Action='DELETE_TOUR'")
                .errorMessage("Something failed")
                .build();

        service.logAsync(dto);

        ArgumentCaptor<AppLog> captor =
                ArgumentCaptor.forClass(AppLog.class);

        verify(appLogRepository).save(captor.capture());

        assertEquals(
                "User='admin' | Action='DELETE_TOUR' | Error='Something failed'",
                captor.getValue().getMessage()
        );
    }

    @Test
    void logAsync_shouldSetTimestampCloseToCurrentTime() {
        LocalDateTime before = LocalDateTime.now();

        LogFormatDTO dto = LogFormatDTO.builder()
                .level("INFO")
                .method("test.method")
                .build();

        service.logAsync(dto);

        LocalDateTime after = LocalDateTime.now();

        ArgumentCaptor<AppLog> captor =
                ArgumentCaptor.forClass(AppLog.class);

        verify(appLogRepository).save(captor.capture());

        LocalDateTime timestamp =
                captor.getValue().getTimestamp();

        assertFalse(timestamp.isBefore(before));
        assertFalse(timestamp.isAfter(after));
    }

    @Test
    void logAsync_shouldCallRepositoryExactlyOnce() {
        LogFormatDTO dto = LogFormatDTO.builder()
                .level("INFO")
                .method("test.method")
                .build();

        service.logAsync(dto);

        verify(appLogRepository, times(1))
                .save(any(AppLog.class));

        verifyNoMoreInteractions(appLogRepository);
    }
}