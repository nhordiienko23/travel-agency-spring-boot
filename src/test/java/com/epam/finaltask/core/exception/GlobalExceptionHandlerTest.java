package com.epam.finaltask.core.exception;

import com.epam.finaltask.core.exception.notFound.ResourceNotFoundException;
import com.epam.finaltask.log.AppLogService;
import com.epam.finaltask.log.LogFormatDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final AppLogService appLogService =
            mock(AppLogService.class);

    private final MessageSource messageSource =
            mock(MessageSource.class);

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler(
                    appLogService,
                    messageSource
            );

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ========================================================================
    // VALIDATION
    // ========================================================================

    @Test
    void handleValidationExceptions_shouldReturnBadRequestWithErrors() {

        FieldError fieldError =
                new FieldError(
                        "request",
                        "username",
                        "{val.username.req}"
                );

        BindingResult bindingResult =
                mock(BindingResult.class);

        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(fieldError));

        MethodArgumentNotValidException exception =
                mock(MethodArgumentNotValidException.class);

        when(exception.getBindingResult())
                .thenReturn(bindingResult);

        when(messageSource.getMessage(
                eq(fieldError),
                any(Locale.class)
        )).thenReturn("Username is required");

        when(messageSource.getMessage(
                eq("error.validation"),
                isNull(),
                eq("Validation Failed"),
                any(Locale.class)
        )).thenReturn("Validation Failed");

        ResponseEntity<Map<String, Object>> response =
                handler.handleValidationExceptions(exception);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        Map<String, Object> body =
                response.getBody();

        assertEquals(
                400,
                body.get("status")
        );

        assertEquals(
                "Validation Failed",
                body.get("error")
        );

        assertNotNull(
                body.get("timestamp")
        );

        @SuppressWarnings("unchecked")
        Map<String, String> errors =
                (Map<String, String>) body.get("errors");

        assertEquals(
                1,
                errors.size()
        );

        assertEquals(
                "Username is required",
                errors.get("username")
        );

        verify(messageSource).getMessage(
                eq(fieldError),
                any(Locale.class)
        );

        verify(messageSource).getMessage(
                eq("error.validation"),
                isNull(),
                eq("Validation Failed"),
                any(Locale.class)
        );
    }

    @Test
    void handleValidationExceptions_shouldSupportMultipleErrors() {

        FieldError usernameError =
                new FieldError(
                        "request",
                        "username",
                        "{val.username.req}"
                );

        FieldError emailError =
                new FieldError(
                        "request",
                        "email",
                        "{val.email.invalid}"
                );

        BindingResult bindingResult =
                mock(BindingResult.class);

        when(bindingResult.getFieldErrors())
                .thenReturn(
                        List.of(
                                usernameError,
                                emailError
                        )
                );

        MethodArgumentNotValidException exception =
                mock(MethodArgumentNotValidException.class);

        when(exception.getBindingResult())
                .thenReturn(bindingResult);

        when(messageSource.getMessage(
                eq(usernameError),
                any(Locale.class)
        )).thenReturn("Username is required");

        when(messageSource.getMessage(
                eq(emailError),
                any(Locale.class)
        )).thenReturn("Invalid email format");

        when(messageSource.getMessage(
                eq("error.validation"),
                isNull(),
                eq("Validation Failed"),
                any(Locale.class)
        )).thenReturn("Validation Failed");

        ResponseEntity<Map<String, Object>> response =
                handler.handleValidationExceptions(exception);

        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, String> errors =
                (Map<String, String>) response.getBody()
                        .get("errors");

        assertEquals(
                2,
                errors.size()
        );

        assertEquals(
                "Username is required",
                errors.get("username")
        );

        assertEquals(
                "Invalid email format",
                errors.get("email")
        );

        verify(messageSource)
                .getMessage(
                        eq(usernameError),
                        any(Locale.class)
                );

        verify(messageSource)
                .getMessage(
                        eq(emailError),
                        any(Locale.class)
                );

        verify(messageSource)
                .getMessage(
                        eq("error.validation"),
                        isNull(),
                        eq("Validation Failed"),
                        any(Locale.class)
                );
    }

    // ========================================================================
    // RESOURCE NOT FOUND
    // ========================================================================

    @Test
    void handleResourceNotFoundException_shouldResolveMessageKey() {

        ResourceNotFoundException exception =
                new ResourceNotFoundException(
                        "err.user.notFound"
                );

        when(messageSource.getMessage(
                eq("error.notFound"),
                isNull(),
                eq("Resource Not Found"),
                any(Locale.class)
        )).thenReturn("Resource Not Found");

        when(messageSource.getMessage(
                eq("err.user.notFound"),
                isNull(),
                eq("err.user.notFound"),
                any(Locale.class)
        )).thenReturn("User not found");

        ResponseEntity<Map<String, Object>> response =
                handler.handleResourceNotFoundException(
                        exception
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        Map<String, Object> body =
                response.getBody();

        assertEquals(
                404,
                body.get("status")
        );

        assertEquals(
                "Resource Not Found",
                body.get("error")
        );

        assertEquals(
                "User not found",
                body.get("message")
        );

        assertNotNull(
                body.get("timestamp")
        );

        verify(messageSource)
                .getMessage(
                        eq("error.notFound"),
                        isNull(),
                        eq("Resource Not Found"),
                        any(Locale.class)
                );

        verify(messageSource)
                .getMessage(
                        eq("err.user.notFound"),
                        isNull(),
                        eq("err.user.notFound"),
                        any(Locale.class)
                );
    }

    @Test
    void handleResourceNotFoundException_shouldUseFallbackForUnknownMessage() {

        ResourceNotFoundException exception =
                new ResourceNotFoundException(
                        "Voucher not found"
                );

        when(messageSource.getMessage(
                eq("error.notFound"),
                isNull(),
                eq("Resource Not Found"),
                any(Locale.class)
        )).thenReturn("Resource Not Found");

        when(messageSource.getMessage(
                eq("Voucher not found"),
                isNull(),
                eq("Voucher not found"),
                any(Locale.class)
        )).thenReturn("Voucher not found");

        ResponseEntity<Map<String, Object>> response =
                handler.handleResourceNotFoundException(
                        exception
                );

        assertNotNull(response.getBody());

        assertEquals(
                "Voucher not found",
                response.getBody().get("message")
        );
    }

    @Test
    void handleResourceNotFoundException_shouldUseUnknownMessageWhenMessageIsNull() {

        ResourceNotFoundException exception =
                new ResourceNotFoundException(
                        (String) null
                );

        when(messageSource.getMessage(
                eq("error.notFound"),
                isNull(),
                eq("Resource Not Found"),
                any(Locale.class)
        )).thenReturn("Resource Not Found");

        when(messageSource.getMessage(
                eq("error.unknown"),
                isNull(),
                eq("An error occurred"),
                any(Locale.class)
        )).thenReturn("An error occurred");

        ResponseEntity<Map<String, Object>> response =
                handler.handleResourceNotFoundException(
                        exception
                );

        assertNotNull(response.getBody());

        assertEquals(
                "An error occurred",
                response.getBody().get("message")
        );
    }

    @Test
    void handleResourceNotFoundException_shouldUseUnknownMessageWhenMessageIsBlank() {

        ResourceNotFoundException exception =
                new ResourceNotFoundException(
                        "   "
                );

        when(messageSource.getMessage(
                eq("error.notFound"),
                isNull(),
                eq("Resource Not Found"),
                any(Locale.class)
        )).thenReturn("Resource Not Found");

        when(messageSource.getMessage(
                eq("error.unknown"),
                isNull(),
                eq("An error occurred"),
                any(Locale.class)
        )).thenReturn("An error occurred");

        ResponseEntity<Map<String, Object>> response =
                handler.handleResourceNotFoundException(
                        exception
                );

        assertNotNull(response.getBody());

        assertEquals(
                "An error occurred",
                response.getBody().get("message")
        );
    }

    // ========================================================================
    // ILLEGAL ARGUMENT
    // ========================================================================

    @Test
    void handleIllegalArgumentException_shouldResolveMessageKey() {

        IllegalArgumentException exception =
                new IllegalArgumentException(
                        "err.username.taken"
                );

        when(messageSource.getMessage(
                eq("error.badRequest"),
                isNull(),
                eq("Bad Request"),
                any(Locale.class)
        )).thenReturn("Bad Request");

        when(messageSource.getMessage(
                eq("err.username.taken"),
                isNull(),
                eq("err.username.taken"),
                any(Locale.class)
        )).thenReturn("Username is already taken");

        ResponseEntity<Map<String, Object>> response =
                handler.handleIllegalArgumentException(
                        exception
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                400,
                response.getBody().get("status")
        );

        assertEquals(
                "Bad Request",
                response.getBody().get("error")
        );

        assertEquals(
                "Username is already taken",
                response.getBody().get("message")
        );
    }

    @Test
    void handleIllegalArgumentException_shouldUseFallbackForUnknownMessage() {

        IllegalArgumentException exception =
                new IllegalArgumentException(
                        "Invalid argument"
                );

        when(messageSource.getMessage(
                eq("error.badRequest"),
                isNull(),
                eq("Bad Request"),
                any(Locale.class)
        )).thenReturn("Bad Request");

        when(messageSource.getMessage(
                eq("Invalid argument"),
                isNull(),
                eq("Invalid argument"),
                any(Locale.class)
        )).thenReturn("Invalid argument");

        ResponseEntity<Map<String, Object>> response =
                handler.handleIllegalArgumentException(
                        exception
                );

        assertNotNull(response.getBody());

        assertEquals(
                "Invalid argument",
                response.getBody().get("message")
        );
    }

    @Test
    void handleIllegalArgumentException_shouldUseUnknownMessageWhenMessageIsNull() {

        IllegalArgumentException exception =
                new IllegalArgumentException(
                        (String) null
                );

        when(messageSource.getMessage(
                eq("error.badRequest"),
                isNull(),
                eq("Bad Request"),
                any(Locale.class)
        )).thenReturn("Bad Request");

        when(messageSource.getMessage(
                eq("error.unknown"),
                isNull(),
                eq("An error occurred"),
                any(Locale.class)
        )).thenReturn("An error occurred");

        ResponseEntity<Map<String, Object>> response =
                handler.handleIllegalArgumentException(
                        exception
                );

        assertNotNull(response.getBody());

        assertEquals(
                "An error occurred",
                response.getBody().get("message")
        );
    }

    @Test
    void handleIllegalArgumentException_shouldUseUnknownMessageWhenMessageIsBlank() {

        IllegalArgumentException exception =
                new IllegalArgumentException(
                        "   "
                );

        when(messageSource.getMessage(
                eq("error.badRequest"),
                isNull(),
                eq("Bad Request"),
                any(Locale.class)
        )).thenReturn("Bad Request");

        when(messageSource.getMessage(
                eq("error.unknown"),
                isNull(),
                eq("An error occurred"),
                any(Locale.class)
        )).thenReturn("An error occurred");

        ResponseEntity<Map<String, Object>> response =
                handler.handleIllegalArgumentException(
                        exception
                );

        assertNotNull(response.getBody());

        assertEquals(
                "An error occurred",
                response.getBody().get("message")
        );
    }

    // ========================================================================
    // BAD CREDENTIALS
    // ========================================================================

    @Test
    void handleBadCredentialsException_shouldReturnUnauthorized() {

        BadCredentialsException exception =
                new BadCredentialsException(
                        "err.login.invalid"
                );

        when(messageSource.getMessage(
                eq("error.unauthorized"),
                isNull(),
                eq("Unauthorized"),
                any(Locale.class)
        )).thenReturn("Unauthorized");

        when(messageSource.getMessage(
                eq("err.login.invalid"),
                isNull(),
                eq("Invalid username or password"),
                any(Locale.class)
        )).thenReturn("Invalid username or password");

        ResponseEntity<Map<String, Object>> response =
                handler.handleBadCredentialsException(
                        exception
                );

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        Map<String, Object> body =
                response.getBody();

        assertEquals(
                401,
                body.get("status")
        );

        assertEquals(
                "Unauthorized",
                body.get("error")
        );

        assertEquals(
                "Invalid username or password",
                body.get("message")
        );

        assertNotNull(
                body.get("timestamp")
        );
    }

    // ========================================================================
    // DISABLED ACCOUNT
    // ========================================================================

    @Test
    void handleDisabledException_shouldReturnForbidden() {

        DisabledException exception =
                new DisabledException(
                        "err.account.blocked"
                );

        when(messageSource.getMessage(
                eq("error.forbidden"),
                isNull(),
                eq("Forbidden"),
                any(Locale.class)
        )).thenReturn("Forbidden");

        when(messageSource.getMessage(
                eq("err.account.blocked"),
                isNull(),
                eq("User account is blocked"),
                any(Locale.class)
        )).thenReturn("User account is blocked");

        ResponseEntity<Map<String, Object>> response =
                handler.handleDisabledException(
                        exception
                );

        assertEquals(
                HttpStatus.FORBIDDEN,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        Map<String, Object> body =
                response.getBody();

        assertEquals(
                403,
                body.get("status")
        );

        assertEquals(
                "Forbidden",
                body.get("error")
        );

        assertEquals(
                "User account is blocked",
                body.get("message")
        );

        assertNotNull(
                body.get("timestamp")
        );
    }

    // ========================================================================
    // ACCESS DENIED - ANONYMOUS
    // ========================================================================

    @Test
    void handleAccessDeniedException_shouldLogAnonymousAccessAttempt() {

        SecurityContextHolder.clearContext();

        HttpServletRequest request =
                mock(HttpServletRequest.class);

        when(request.getRequestURI())
                .thenReturn("/api/admin/users");

        when(request.getMethod())
                .thenReturn("GET");

        when(messageSource.getMessage(
                eq("error.forbidden"),
                isNull(),
                eq("Forbidden"),
                any(Locale.class)
        )).thenReturn("Forbidden");

        when(messageSource.getMessage(
                eq("err.access.denied"),
                isNull(),
                eq("Access denied to this resource"),
                any(Locale.class)
        )).thenReturn("Access Denied to this resource");

        AccessDeniedException exception =
                new AccessDeniedException(
                        "Access denied"
                );

        ResponseEntity<Map<String, Object>> response =
                handler.handleAccessDeniedException(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.FORBIDDEN,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        Map<String, Object> body =
                response.getBody();

        assertEquals(
                403,
                body.get("status")
        );

        assertEquals(
                "Forbidden",
                body.get("error")
        );

        assertEquals(
                "Access Denied to this resource",
                body.get("message")
        );

        ArgumentCaptor<LogFormatDTO> captor =
                ArgumentCaptor.forClass(
                        LogFormatDTO.class
                );

        verify(appLogService)
                .logAsync(captor.capture());

        LogFormatDTO log =
                captor.getValue();

        assertEquals(
                "WARN",
                log.level()
        );

        assertEquals(
                "GET /api/admin/users",
                log.method()
        );

        assertNotNull(
                log.errorMessage()
        );

        assertTrue(
                log.errorMessage().contains(
                        "Anonymous/Unauthenticated"
                )
        );

        assertTrue(
                log.errorMessage().contains(
                        "/api/admin/users"
                )
        );
    }

    // ========================================================================
    // ACCESS DENIED - AUTHENTICATED
    // ========================================================================

    @Test
    void handleAccessDeniedException_shouldLogAuthenticatedUsername() {

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "admin",
                        null,
                        Collections.singleton(
                                new SimpleGrantedAuthority(
                                        "ROLE_ADMIN"
                                )
                        )
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        HttpServletRequest request =
                mock(HttpServletRequest.class);

        when(request.getRequestURI())
                .thenReturn("/api/admin/vouchers");

        when(request.getMethod())
                .thenReturn("DELETE");

        when(messageSource.getMessage(
                eq("error.forbidden"),
                isNull(),
                eq("Forbidden"),
                any(Locale.class)
        )).thenReturn("Forbidden");

        when(messageSource.getMessage(
                eq("err.access.denied"),
                isNull(),
                eq("Access denied to this resource"),
                any(Locale.class)
        )).thenReturn("Access Denied to this resource");

        AccessDeniedException exception =
                new AccessDeniedException(
                        "Access denied"
                );

        handler.handleAccessDeniedException(
                exception,
                request
        );

        ArgumentCaptor<LogFormatDTO> captor =
                ArgumentCaptor.forClass(
                        LogFormatDTO.class
                );

        verify(appLogService)
                .logAsync(captor.capture());

        LogFormatDTO log =
                captor.getValue();

        assertEquals(
                "WARN",
                log.level()
        );

        assertEquals(
                "DELETE /api/admin/vouchers",
                log.method()
        );

        assertTrue(
                log.errorMessage().contains(
                        "User 'admin'"
                )
        );

        assertTrue(
                log.errorMessage().contains(
                        "/api/admin/vouchers"
                )
        );
    }

    // ========================================================================
    // ACCESS DENIED - ANONYMOUS TOKEN
    // ========================================================================

    @Test
    void handleAccessDeniedException_shouldTreatAnonymousTokenAsAnonymous() {

        AnonymousAuthenticationToken anonymous =
                new AnonymousAuthenticationToken(
                        "key",
                        "anonymousUser",
                        Collections.singleton(
                                new SimpleGrantedAuthority(
                                        "ROLE_ANONYMOUS"
                                )
                        )
                );

        SecurityContextHolder.getContext()
                .setAuthentication(anonymous);

        HttpServletRequest request =
                mock(HttpServletRequest.class);

        when(request.getRequestURI())
                .thenReturn("/api/admin/users");

        when(request.getMethod())
                .thenReturn("POST");

        when(messageSource.getMessage(
                eq("error.forbidden"),
                isNull(),
                eq("Forbidden"),
                any(Locale.class)
        )).thenReturn("Forbidden");

        when(messageSource.getMessage(
                eq("err.access.denied"),
                isNull(),
                eq("Access denied to this resource"),
                any(Locale.class)
        )).thenReturn("Access Denied to this resource");

        AccessDeniedException exception =
                new AccessDeniedException(
                        "Access denied"
                );

        handler.handleAccessDeniedException(
                exception,
                request
        );

        ArgumentCaptor<LogFormatDTO> captor =
                ArgumentCaptor.forClass(
                        LogFormatDTO.class
                );

        verify(appLogService)
                .logAsync(captor.capture());

        assertTrue(
                captor.getValue()
                        .errorMessage()
                        .contains(
                                "Anonymous/Unauthenticated"
                        )
        );
    }

    // ========================================================================
    // ACCESS DENIED - UNAUTHENTICATED AUTHENTICATION
    // ========================================================================

    @Test
    void handleAccessDeniedException_shouldHandleUnauthenticatedUser() {

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "user",
                        null
                );

        assertFalse(
                authentication.isAuthenticated()
        );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        HttpServletRequest request =
                mock(HttpServletRequest.class);

        when(request.getRequestURI())
                .thenReturn("/api/protected");

        when(request.getMethod())
                .thenReturn("GET");

        when(messageSource.getMessage(
                eq("error.forbidden"),
                isNull(),
                eq("Forbidden"),
                any(Locale.class)
        )).thenReturn("Forbidden");

        when(messageSource.getMessage(
                eq("err.access.denied"),
                isNull(),
                eq("Access denied to this resource"),
                any(Locale.class)
        )).thenReturn("Access Denied to this resource");

        AccessDeniedException exception =
                new AccessDeniedException(
                        "Access denied"
                );

        ResponseEntity<Map<String, Object>> response =
                handler.handleAccessDeniedException(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.FORBIDDEN,
                response.getStatusCode()
        );

        ArgumentCaptor<LogFormatDTO> captor =
                ArgumentCaptor.forClass(
                        LogFormatDTO.class
                );

        verify(appLogService)
                .logAsync(captor.capture());

        LogFormatDTO log =
                captor.getValue();

        assertEquals(
                "GET /api/protected",
                log.method()
        );

        assertTrue(
                log.errorMessage().contains(
                        "Anonymous/Unauthenticated"
                )
        );

        assertFalse(
                log.errorMessage().contains(
                        "User 'user'"
                )
        );
    }

    // ========================================================================
    // ACCESS DENIED - NULL AUTHENTICATION
    // ========================================================================

    @Test
    void handleAccessDeniedException_shouldHandleNullAuthentication() {

        SecurityContextHolder.clearContext();

        HttpServletRequest request =
                mock(HttpServletRequest.class);

        when(request.getRequestURI())
                .thenReturn("/api/protected");

        when(request.getMethod())
                .thenReturn("GET");

        when(messageSource.getMessage(
                eq("error.forbidden"),
                isNull(),
                eq("Forbidden"),
                any(Locale.class)
        )).thenReturn("Forbidden");

        when(messageSource.getMessage(
                eq("err.access.denied"),
                isNull(),
                eq("Access denied to this resource"),
                any(Locale.class)
        )).thenReturn("Access Denied to this resource");

        AccessDeniedException exception =
                new AccessDeniedException(
                        "Access denied"
                );

        ResponseEntity<Map<String, Object>> response =
                handler.handleAccessDeniedException(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.FORBIDDEN,
                response.getStatusCode()
        );

        ArgumentCaptor<LogFormatDTO> captor =
                ArgumentCaptor.forClass(
                        LogFormatDTO.class
                );

        verify(appLogService)
                .logAsync(captor.capture());

        assertTrue(
                captor.getValue()
                        .errorMessage()
                        .contains(
                                "Anonymous/Unauthenticated"
                        )
        );
    }

    // ========================================================================
    // GLOBAL EXCEPTION
    // ========================================================================


    @Test
    void handleGlobalException_shouldReturnInternalServerError() {

        Exception exception =
                new Exception(
                        "Unexpected failure"
                );

        HttpServletRequest request =
                mock(HttpServletRequest.class);

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getRequestURI())
                .thenReturn("/api/test");

        when(messageSource.getMessage(
                eq("error.internal"),
                isNull(),
                eq("Internal Server Error"),
                any(Locale.class)
        )).thenReturn("Internal Server Error");

        when(messageSource.getMessage(
                eq("error.internal.message"),
                isNull(),
                eq("An unexpected error occurred"),
                any(Locale.class)
        )).thenReturn("An unexpected error occurred");

        ResponseEntity<Map<String, Object>> response =
                handler.handleGlobalException(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        Map<String, Object> body =
                response.getBody();

        assertEquals(
                500,
                body.get("status")
        );

        assertEquals(
                "Internal Server Error",
                body.get("error")
        );

        assertEquals(
                "An unexpected error occurred",
                body.get("message")
        );

        assertNotEquals(
                "Unexpected failure",
                body.get("message")
        );

        assertNotNull(
                body.get("timestamp")
        );

        ArgumentCaptor<LogFormatDTO> captor =
                ArgumentCaptor.forClass(
                        LogFormatDTO.class
                );

        verify(appLogService)
                .logAsync(captor.capture());

        LogFormatDTO log =
                captor.getValue();

        assertEquals(
                "ERROR",
                log.level()
        );

        assertEquals(
                "GET /api/test",
                log.method()
        );

        assertEquals(
                "Exception",
                log.errorMessage()
        );

        assertEquals(
                "Unexpected failure",
                log.cause()
        );
    }


}

