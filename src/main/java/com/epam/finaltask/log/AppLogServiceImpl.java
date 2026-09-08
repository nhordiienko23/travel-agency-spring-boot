package com.epam.finaltask.log;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AppLogServiceImpl implements AppLogService {

    private final AppLogRepository appLogRepository;

    @Async
    @Override
    @Transactional
    public void logAsync(LogFormatDTO dto) {

        AppLog logEntry = AppLog.builder()
                .logLevel(dto.level())
                .targetMethod(dto.method())
                .message(dto.formatMessage())
                .timestamp(LocalDateTime.now())
                .build();

        appLogRepository.save(logEntry);
    }
}