package com.epam.finaltask.core.exception;

import com.epam.finaltask.core.exception.notFound.ResourceNotFoundException;
import com.epam.finaltask.log.AppLogService;
import com.epam.finaltask.log.LogFormatDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice(
        annotations = RestController.class
)
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final AppLogService appLogService;
    private final MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>>
    handleValidationExceptions(
            MethodArgumentNotValidException ex
    ) {

        Locale locale =
                LocaleContextHolder.getLocale();

        Map<String, String> errors =
                new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {

                    String fieldName =
                            error.getField();

                    String errorMessage =
                            messageSource.getMessage(
                                    error,
                                    locale
                            );

                    errors.put(
                            fieldName,
                            errorMessage
                    );
                });

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "timestamp",
                LocalDateTime.now()
        );

        response.put(
                "status",
                HttpStatus.BAD_REQUEST.value()
        );

        response.put(
                "error",
                getMessage(
                        "error.validation",
                        "Validation Failed",
                        locale
                )
        );

        response.put(
                "errors",
                errors
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>>
    handleResourceNotFoundException(
            ResourceNotFoundException ex
    ) {

        Locale locale =
                LocaleContextHolder.getLocale();

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "timestamp",
                LocalDateTime.now()
        );

        response.put(
                "status",
                HttpStatus.NOT_FOUND.value()
        );

        response.put(
                "error",
                getMessage(
                        "error.notFound",
                        "Resource Not Found",
                        locale
                )
        );

        response.put(
                "message",
                resolveExceptionMessage(
                        ex.getMessage(),
                        locale
                )
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>>
    handleIllegalArgumentException(
            IllegalArgumentException ex
    ) {

        Locale locale =
                LocaleContextHolder.getLocale();

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "timestamp",
                LocalDateTime.now()
        );

        response.put(
                "status",
                HttpStatus.BAD_REQUEST.value()
        );

        response.put(
                "error",
                getMessage(
                        "error.badRequest",
                        "Bad Request",
                        locale
                )
        );

        response.put(
                "message",
                resolveExceptionMessage(
                        ex.getMessage(),
                        locale
                )
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>>
    handleBadCredentialsException(
            BadCredentialsException ex
    ) {

        Locale locale =
                LocaleContextHolder.getLocale();

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "timestamp",
                LocalDateTime.now()
        );

        response.put(
                "status",
                HttpStatus.UNAUTHORIZED.value()
        );

        response.put(
                "error",
                getMessage(
                        "error.unauthorized",
                        "Unauthorized",
                        locale
                )
        );

        response.put(
                "message",
                getMessage(
                        "err.login.invalid",
                        "Invalid username or password",
                        locale
                )
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, Object>>
    handleDisabledException(
            DisabledException ex
    ) {

        Locale locale =
                LocaleContextHolder.getLocale();

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "timestamp",
                LocalDateTime.now()
        );

        response.put(
                "status",
                HttpStatus.FORBIDDEN.value()
        );

        response.put(
                "error",
                getMessage(
                        "error.forbidden",
                        "Forbidden",
                        locale
                )
        );

        response.put(
                "message",
                getMessage(
                        "err.account.blocked",
                        "User account is blocked",
                        locale
                )
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>>
    handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {

        String username =
                "Anonymous/Unauthenticated";

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)) {

            username = auth.getName();
        }

        String logMessage =
                "Security Breach Attempt: User '"
                        + username
                        + "' tried to access protected resource: "
                        + request.getRequestURI();

        LogFormatDTO logDto =
                LogFormatDTO.builder()
                        .level("WARN")
                        .method(
                                request.getMethod()
                                        + " "
                                        + request.getRequestURI()
                        )
                        .errorMessage(logMessage)
                        .build();

        appLogService.logAsync(logDto);

        Locale locale =
                LocaleContextHolder.getLocale();

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "timestamp",
                LocalDateTime.now()
        );

        response.put(
                "status",
                HttpStatus.FORBIDDEN.value()
        );

        response.put(
                "error",
                getMessage(
                        "error.forbidden",
                        "Forbidden",
                        locale
                )
        );

        response.put(
                "message",
                getMessage(
                        "err.access.denied",
                        "Access denied to this resource",
                        locale
                )
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>>
    handleGlobalException(
            Exception ex,
            HttpServletRequest request
    ) {

        Locale locale =
                LocaleContextHolder.getLocale();

        /*
         * ERROR:
         * unexpected system/application error.
         *
         * Internal exception details are logged,
         * but are NOT exposed to the client.
         */
        LogFormatDTO logDto =
                LogFormatDTO.builder()
                        .level("ERROR")
                        .method(
                                request.getMethod()
                                        + " "
                                        + request.getRequestURI()
                        )
                        .errorMessage(
                                ex.getClass()
                                        .getSimpleName()
                        )
                        .cause(
                                ex.getMessage()
                        )
                        .build();

        appLogService.logAsync(logDto);

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "timestamp",
                LocalDateTime.now()
        );

        response.put(
                "status",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        response.put(
                "error",
                getMessage(
                        "error.internal",
                        "Internal Server Error",
                        locale
                )
        );

        /*
         * Never expose internal exception details.
         */
        response.put(
                "message",
                getMessage(
                        "error.internal.message",
                        "An unexpected error occurred",
                        locale
                )
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    private String resolveExceptionMessage(
            String message,
            Locale locale
    ) {

        if (message == null
                || message.isBlank()) {

            return getMessage(
                    "error.unknown",
                    "An error occurred",
                    locale
            );
        }

        return messageSource.getMessage(
                message,
                null,
                message,
                locale
        );
    }

    private String getMessage(
            String key,
            String defaultMessage,
            Locale locale
    ) {

        return messageSource.getMessage(
                key,
                null,
                defaultMessage,
                locale
        );
    }
}

