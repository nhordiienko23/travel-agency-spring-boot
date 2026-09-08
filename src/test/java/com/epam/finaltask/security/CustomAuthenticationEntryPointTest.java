package com.epam.finaltask.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationEntryPointTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private CustomAuthenticationEntryPoint entryPoint;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() {

        entryPoint =
                new CustomAuthenticationEntryPoint(
                        messageSource
                );

        responseWriter =
                new StringWriter();
    }

    // ========================================================================
    // API REQUEST
    // ========================================================================

    @Test
    void commence_shouldReturnJsonForApiRequest()
            throws Exception {

        PrintWriter writer =
                new PrintWriter(responseWriter);

        when(response.getWriter())
                .thenReturn(writer);

        when(request.getRequestURI())
                .thenReturn("/api/users");

        when(request.getContextPath())
                .thenReturn("");

        when(messageSource.getMessage(
                eq("error.unauthorized"),
                isNull(),
                anyString(),
                any(Locale.class)
        )).thenReturn(
                "Unauthorized"
        );

        when(messageSource.getMessage(
                eq("err.authentication.required"),
                isNull(),
                anyString(),
                any(Locale.class)
        )).thenReturn(
                "Authentication is required to access this resource."
        );

        entryPoint.commence(
                request,
                response,
                new BadCredentialsException(
                        "Authentication required"
                )
        );

        writer.flush();

        verify(response)
                .setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED
                );

        verify(response)
                .setContentType(
                        "application/json"
                );

        verify(response)
                .setCharacterEncoding(
                        "UTF-8"
                );

        verify(response)
                .getWriter();

        verify(response, never())
                .sendRedirect(anyString());

        String json =
                responseWriter.toString();

        assertTrue(
                json.contains(
                        "\"status\": 401"
                )
        );

        assertTrue(
                json.contains(
                        "\"error\": \"Unauthorized\""
                )
        );

        assertTrue(
                json.contains(
                        "\"message\": \"Authentication is required to access this resource.\""
                )
        );

        assertTrue(
                json.contains(
                        "\"timestamp\": \""
                )
        );
    }

    // ========================================================================
    // WEB REQUEST
    // ========================================================================

    @Test
    void commence_shouldRedirectToErrorPageForWebRequest()
            throws Exception {

        when(request.getRequestURI())
                .thenReturn("/admin/users");

        when(request.getContextPath())
                .thenReturn("");

        entryPoint.commence(
                request,
                response,
                new BadCredentialsException(
                        "Authentication required"
                )
        );

        verify(response)
                .sendRedirect(
                        "/error?status=401"
                );

        verify(response, never())
                .setStatus(
                        any(Integer.class)
                );

        verify(response, never())
                .setContentType(
                        anyString()
                );

        verify(response, never())
                .getWriter();
    }

    // ========================================================================
    // WEB REQUEST WITH CONTEXT PATH
    // ========================================================================

    @Test
    void commence_shouldIncludeContextPathForWebRequest()
            throws Exception {

        when(request.getRequestURI())
                .thenReturn(
                        "/travel-agency/admin/users"
                );

        when(request.getContextPath())
                .thenReturn(
                        "/travel-agency"
                );

        entryPoint.commence(
                request,
                response,
                new BadCredentialsException(
                        "Authentication required"
                )
        );

        verify(response)
                .sendRedirect(
                        "/travel-agency/error?status=401"
                );

        verify(response, never())
                .getWriter();
    }

    // ========================================================================
    // API REQUEST WITH CONTEXT PATH
    // ========================================================================

    @Test
    void commence_shouldReturnJsonForApiWithContextPath()
            throws Exception {

        PrintWriter writer =
                new PrintWriter(responseWriter);

        when(response.getWriter())
                .thenReturn(writer);

        when(request.getRequestURI())
                .thenReturn(
                        "/travel-agency/api/users"
                );

        when(request.getContextPath())
                .thenReturn(
                        "/travel-agency"
                );

        when(messageSource.getMessage(
                eq("error.unauthorized"),
                isNull(),
                anyString(),
                any(Locale.class)
        )).thenReturn(
                "Unauthorized"
        );

        when(messageSource.getMessage(
                eq("err.authentication.required"),
                isNull(),
                anyString(),
                any(Locale.class)
        )).thenReturn(
                "Authentication is required to access this resource."
        );

        entryPoint.commence(
                request,
                response,
                new BadCredentialsException(
                        "Authentication required"
                )
        );

        writer.flush();

        verify(response)
                .setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED
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
                        "\"status\": 401"
                )
        );

        assertTrue(
                json.contains(
                        "\"error\": \"Unauthorized\""
                )
        );

        assertTrue(
                json.contains(
                        "\"message\": \"Authentication is required to access this resource.\""
                )
        );
    }

    // ========================================================================
    // LOCALIZED MESSAGES
    // ========================================================================

    @Test
    void commence_shouldUseLocalizedMessages()
            throws Exception {

        PrintWriter writer =
                new PrintWriter(responseWriter);

        when(response.getWriter())
                .thenReturn(writer);

        when(request.getRequestURI())
                .thenReturn("/api/users");

        when(request.getContextPath())
                .thenReturn("");

        when(messageSource.getMessage(
                eq("error.unauthorized"),
                isNull(),
                anyString(),
                any(Locale.class)
        )).thenReturn(
                "Неавторизован"
        );

        when(messageSource.getMessage(
                eq("err.authentication.required"),
                isNull(),
                anyString(),
                any(Locale.class)
        )).thenReturn(
                "Для доступа к этому ресурсу необходимо войти в систему."
        );

        entryPoint.commence(
                request,
                response,
                new BadCredentialsException(
                        "Authentication required"
                )
        );

        writer.flush();

        String json =
                responseWriter.toString();

        assertTrue(
                json.contains(
                        "Неавторизован"
                )
        );

        assertTrue(
                json.contains(
                        "Для доступа к этому ресурсу необходимо войти в систему."
                )
        );
    }

    // ========================================================================
    // JSON ESCAPING
    // ========================================================================

    @Test
    void commence_shouldEscapeJsonSpecialCharacters()
            throws Exception {

        PrintWriter writer =
                new PrintWriter(responseWriter);

        when(response.getWriter())
                .thenReturn(writer);

        when(request.getRequestURI())
                .thenReturn("/api/users");

        when(request.getContextPath())
                .thenReturn("");

        when(messageSource.getMessage(
                eq("error.unauthorized"),
                isNull(),
                anyString(),
                any(Locale.class)
        )).thenReturn(
                "Error \"Unauthorized\""
        );

        when(messageSource.getMessage(
                eq("err.authentication.required"),
                isNull(),
                anyString(),
                any(Locale.class)
        )).thenReturn(
                "Authentication\nis required\rto continue"
        );

        entryPoint.commence(
                request,
                response,
                new BadCredentialsException(
                        "Authentication required"
                )
        );

        writer.flush();

        String json =
                responseWriter.toString();

        assertTrue(
                json.contains(
                        "Error \\\"Unauthorized\\\""
                )
        );

        assertTrue(
                json.contains(
                        "Authentication\\nis required\\rto continue"
                )
        );
    }

    // ========================================================================
    // DEFAULT MESSAGES
    // ========================================================================

    @Test
    void commence_shouldUseDefaultMessages()
            throws Exception {

        PrintWriter writer =
                new PrintWriter(responseWriter);

        when(response.getWriter())
                .thenReturn(writer);

        when(request.getRequestURI())
                .thenReturn("/api/users");

        when(request.getContextPath())
                .thenReturn("");

        /*
         * Return the default value passed to MessageSource.
         */
        when(messageSource.getMessage(
                eq("error.unauthorized"),
                isNull(),
                eq("Unauthorized"),
                any(Locale.class)
        )).thenAnswer(
                invocation -> invocation.getArgument(2)
        );

        when(messageSource.getMessage(
                eq("err.authentication.required"),
                isNull(),
                eq(
                        "Authentication is required to access this resource."
                ),
                any(Locale.class)
        )).thenAnswer(
                invocation -> invocation.getArgument(2)
        );

        entryPoint.commence(
                request,
                response,
                new BadCredentialsException(
                        "Authentication required"
                )
        );

        writer.flush();

        String json =
                responseWriter.toString();

        assertTrue(
                json.contains(
                        "\"error\": \"Unauthorized\""
                )
        );

        assertTrue(
                json.contains(
                        "\"message\": \"Authentication is required to access this resource.\""
                )
        );

        verify(messageSource)
                .getMessage(
                        eq("error.unauthorized"),
                        isNull(),
                        eq("Unauthorized"),
                        any(Locale.class)
                );

        verify(messageSource)
                .getMessage(
                        eq("err.authentication.required"),
                        isNull(),
                        eq(
                                "Authentication is required to access this resource."
                        ),
                        any(Locale.class)
                );
    }
}

