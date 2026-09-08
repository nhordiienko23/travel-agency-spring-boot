package com.epam.finaltask.security;

import com.epam.finaltask.log.AppLogService;
import com.epam.finaltask.log.LogFormatDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CustomAccessDeniedHandlerTest {

    private AppLogService appLogService;
    private MessageSource messageSource;
    private CustomAccessDeniedHandler handler;

    private HttpServletRequest request;
    private HttpServletResponse response;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {

        appLogService = mock(AppLogService.class);
        messageSource = mock(MessageSource.class);

        handler = new CustomAccessDeniedHandler(
                appLogService,
                messageSource
        );

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        responseWriter = new StringWriter();

        when(response.getWriter())
                .thenReturn(
                        new PrintWriter(responseWriter)
                );

        when(messageSource.getMessage(
                eq("error.forbidden"),
                isNull(),
                anyString(),
                any(Locale.class)
        )).thenReturn("Forbidden");

        when(messageSource.getMessage(
                eq("err.access.denied"),
                isNull(),
                anyString(),
                any(Locale.class)
        )).thenReturn("Access denied to this resource");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ========================================================================
    // WEB REQUEST
    // ========================================================================

    @Test
    void handle_shouldRedirectAndLogAuthenticatedUser()
            throws Exception {

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

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getRequestURI())
                .thenReturn("/admin/users");

        when(request.getContextPath())
                .thenReturn("");

        AccessDeniedException exception =
                new AccessDeniedException(
                        "Access denied"
                );

        handler.handle(
                request,
                response,
                exception
        );

        verify(response)
                .sendRedirect(
                        "/error?status=403"
                );

        verify(response, never())
                .setStatus(anyInt());

        verify(response, never())
                .setContentType(anyString());

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
                "GET /admin/users",
                log.method()
        );

        assertEquals(
                "Access denied for user 'admin'",
                log.errorMessage()
        );
    }

    // ========================================================================
    // REST API - AUTHENTICATED USER
    // ========================================================================

    @Test
    void handle_shouldReturnJsonForApiRequest()
            throws Exception {

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

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getRequestURI())
                .thenReturn("/api/admin/users");

        when(request.getContextPath())
                .thenReturn("");

        AccessDeniedException exception =
                new AccessDeniedException(
                        "Access denied"
                );

        handler.handle(
                request,
                response,
                exception
        );

        verify(response)
                .setStatus(
                        HttpServletResponse.SC_FORBIDDEN
                );

        verify(response)
                .setContentType(
                        "application/json"
                );

        verify(response)
                .setCharacterEncoding(
                        "UTF-8"
                );

        verify(response, never())
                .sendRedirect(anyString());

        String json =
                responseWriter.toString();

        assertTrue(
                json.contains(
                        "\"status\": 403"
                )
        );

        assertTrue(
                json.contains(
                        "\"error\": \"Forbidden\""
                )
        );

        assertTrue(
                json.contains(
                        "\"message\": \"Access denied to this resource\""
                )
        );

        verify(appLogService)
                .logAsync(any(LogFormatDTO.class));
    }

    // ========================================================================
    // ANONYMOUS / NO AUTHENTICATION - WEB
    // ========================================================================

    @Test
    void handle_shouldUseAnonymousWhenAuthenticationIsNull()
            throws Exception {

        SecurityContextHolder.clearContext();

        when(request.getMethod())
                .thenReturn("POST");

        when(request.getRequestURI())
                .thenReturn("/admin/users");

        when(request.getContextPath())
                .thenReturn("");

        AccessDeniedException exception =
                new AccessDeniedException(
                        "Access denied"
                );

        handler.handle(
                request,
                response,
                exception
        );

        verify(response)
                .sendRedirect(
                        "/error?status=403"
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
    // ANONYMOUS AUTHENTICATION TOKEN
    // ========================================================================

    @Test
    void handle_shouldUseAnonymousForAnonymousAuthenticationToken()
            throws Exception {

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

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getRequestURI())
                .thenReturn("/admin/vouchers");

        when(request.getContextPath())
                .thenReturn("");

        AccessDeniedException exception =
                new AccessDeniedException(
                        "Access denied"
                );

        handler.handle(
                request,
                response,
                exception
        );

        verify(response)
                .sendRedirect(
                        "/error?status=403"
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
    // CONTEXT PATH
    // ========================================================================

    @Test
    void handle_shouldIncludeContextPathInRedirect()
            throws Exception {

        SecurityContextHolder.clearContext();

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getRequestURI())
                .thenReturn("/admin/users");

        when(request.getContextPath())
                .thenReturn("/travel-agency");

        AccessDeniedException exception =
                new AccessDeniedException(
                        "Access denied"
                );

        handler.handle(
                request,
                response,
                exception
        );

        verify(response)
                .sendRedirect(
                        "/travel-agency/error?status=403"
                );

        verify(appLogService)
                .logAsync(any(LogFormatDTO.class));
    }

    // ========================================================================
    // UNAUTHENTICATED AUTHENTICATION - API
    // ========================================================================

    @Test
    void handle_shouldUseAnonymousForUnauthenticatedAuthentication()
            throws Exception {

        Authentication authentication =
                mock(Authentication.class);

        when(authentication.isAuthenticated())
                .thenReturn(false);

        when(authentication.getName())
                .thenReturn("ignoredUser");

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getRequestURI())
                .thenReturn("/api/protected");

        when(request.getContextPath())
                .thenReturn("");

        AccessDeniedException exception =
                new AccessDeniedException(
                        "Access denied"
                );

        handler.handle(
                request,
                response,
                exception
        );

        verify(response)
                .setStatus(
                        HttpServletResponse.SC_FORBIDDEN
                );

        verify(response)
                .setContentType(
                        "application/json"
                );

        verify(response, never())
                .sendRedirect(anyString());

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
                "GET /api/protected",
                log.method()
        );

        assertEquals(
                "Access denied for user 'Anonymous/Unauthenticated'",
                log.errorMessage()
        );

        String json =
                responseWriter.toString();

        assertTrue(
                json.contains(
                        "\"status\": 403"
                )
        );

        assertTrue(
                json.contains(
                        "\"error\": \"Forbidden\""
                )
        );

        assertTrue(
                json.contains(
                        "\"message\": \"Access denied to this resource\""
                )
        );
    }

    // ========================================================================
    // API WITH CONTEXT PATH
    // ========================================================================

    @Test
    void handle_shouldReturnJsonForApiWithContextPath()
            throws Exception {

        SecurityContextHolder.clearContext();

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getRequestURI())
                .thenReturn(
                        "/travel-agency/api/users"
                );

        when(request.getContextPath())
                .thenReturn(
                        "/travel-agency"
                );

        AccessDeniedException exception =
                new AccessDeniedException(
                        "Access denied"
                );

        handler.handle(
                request,
                response,
                exception
        );

        verify(response)
                .setStatus(
                        HttpServletResponse.SC_FORBIDDEN
                );

        verify(response)
                .setContentType(
                        "application/json"
                );

        verify(response, never())
                .sendRedirect(anyString());

        String json =
                responseWriter.toString();

        assertTrue(
                json.contains(
                        "\"status\": 403"
                )
        );

        verify(appLogService)
                .logAsync(any(LogFormatDTO.class));
    }

    // ========================================================================
    // MESSAGE SOURCE
    // ========================================================================

    @Test
    void handle_shouldUseLocalizedMessagesForApi()
            throws Exception {

        when(messageSource.getMessage(
                eq("error.forbidden"),
                isNull(),
                anyString(),
                any(Locale.class)
        )).thenReturn("Доступ запрещён");

        when(messageSource.getMessage(
                eq("err.access.denied"),
                isNull(),
                anyString(),
                any(Locale.class)
        )).thenReturn(
                "У вас нет доступа к этому ресурсу"
        );

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getRequestURI())
                .thenReturn("/api/users");

        when(request.getContextPath())
                .thenReturn("");

        AccessDeniedException exception =
                new AccessDeniedException(
                        "Access denied"
                );

        handler.handle(
                request,
                response,
                exception
        );

        String json =
                responseWriter.toString();

        assertTrue(
                json.contains(
                        "Доступ запрещён"
                )
        );

        assertTrue(
                json.contains(
                        "У вас нет доступа к этому ресурсу"
                )
        );
    }
}

