package com.epam.finaltask.core.exception;

import com.epam.finaltask.log.AppLogService;
import com.epam.finaltask.log.LogFormatDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CustomErrorControllerTest {

    private MessageSource messageSource;
    private AppLogService appLogService;
    private CustomErrorController controller;
    private HttpServletRequest request;
    private Model model;

    @BeforeEach
    void setUp() {

        messageSource =
                mock(MessageSource.class);

        appLogService =
                mock(AppLogService.class);

        controller =
                new CustomErrorController(
                        messageSource,
                        appLogService
                );

        request =
                mock(HttpServletRequest.class);

        model =
                mock(Model.class);

        // ====================================================================
        // 401
        // ====================================================================

        when(messageSource.getMessage(
                eq("error.page.401.title"),
                isNull(),
                eq("Unauthorized"),
                any(Locale.class)
        )).thenReturn(
                "Unauthorized"
        );

        when(messageSource.getMessage(
                eq("error.page.401.message"),
                isNull(),
                eq(
                        "You must be authenticated to access this resource."
                ),
                any(Locale.class)
        )).thenReturn(
                "You must be authenticated to access this resource."
        );

        // ====================================================================
        // 403
        // ====================================================================

        when(messageSource.getMessage(
                eq("error.page.403.title"),
                isNull(),
                eq("Access Denied"),
                any(Locale.class)
        )).thenReturn(
                "Access Denied"
        );

        when(messageSource.getMessage(
                eq("error.page.403.message"),
                isNull(),
                eq(
                        "You do not have permission to access this resource."
                ),
                any(Locale.class)
        )).thenReturn(
                "You do not have permission to access this resource."
        );

        // ====================================================================
        // 404
        // ====================================================================

        when(messageSource.getMessage(
                eq("error.page.404.title"),
                isNull(),
                eq("Page Not Found"),
                any(Locale.class)
        )).thenReturn(
                "Page Not Found"
        );

        when(messageSource.getMessage(
                eq("error.page.404.message"),
                isNull(),
                eq(
                        "The requested page could not be found."
                ),
                any(Locale.class)
        )).thenReturn(
                "The requested page could not be found."
        );

        // ====================================================================
        // 500
        // ====================================================================

        when(messageSource.getMessage(
                eq("error.page.500.title"),
                isNull(),
                eq("Internal Server Error"),
                any(Locale.class)
        )).thenReturn(
                "Internal Server Error"
        );

        when(messageSource.getMessage(
                eq("error.page.500.message"),
                isNull(),
                eq("An unexpected error occurred."),
                any(Locale.class)
        )).thenReturn(
                "An unexpected error occurred."
        );
    }

    // ========================================================================
    // NO STATUS INFORMATION
    // ========================================================================

    @Test
    void handleError_shouldReturn500WhenNoStatusInformationExists() {

        when(request.getAttribute(
                "jakarta.servlet.error.status_code"
        )).thenReturn(null);

        when(request.getParameter("status"))
                .thenReturn(null);

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getRequestURI())
                .thenReturn("/error");

        String view =
                controller.handleError(
                        request,
                        model
                );

        assertEquals(
                "error",
                view
        );

        verify(model).addAttribute(
                "status",
                500
        );

        verify(model).addAttribute(
                "error",
                "Internal Server Error"
        );

        verify(model).addAttribute(
                "message",
                "An unexpected error occurred."
        );

        verify(appLogService).logAsync(
                any(LogFormatDTO.class)
        );
    }

    // ========================================================================
    // STATUS ATTRIBUTE 401
    // ========================================================================

    @Test
    void handleError_shouldUseStatusAttribute401() {

        when(request.getAttribute(
                "jakarta.servlet.error.status_code"
        )).thenReturn(401);

        String view =
                controller.handleError(
                        request,
                        model
                );

        assertEquals(
                "error",
                view
        );

        verify(model).addAttribute(
                "status",
                401
        );

        verify(model).addAttribute(
                "error",
                "Unauthorized"
        );

        verify(model).addAttribute(
                "message",
                "You must be authenticated to access this resource."
        );

        verifyNoInteractions(
                appLogService
        );
    }

    // ========================================================================
    // STATUS ATTRIBUTE 403
    // ========================================================================

    @Test
    void handleError_shouldUseStatusAttribute403() {

        when(request.getAttribute(
                "jakarta.servlet.error.status_code"
        )).thenReturn(403);

        String view =
                controller.handleError(
                        request,
                        model
                );

        assertEquals(
                "error",
                view
        );

        verify(model).addAttribute(
                "status",
                403
        );

        verify(model).addAttribute(
                "error",
                "Access Denied"
        );

        verify(model).addAttribute(
                "message",
                "You do not have permission to access this resource."
        );

        verifyNoInteractions(
                appLogService
        );
    }

    // ========================================================================
    // STATUS ATTRIBUTE 404
    // ========================================================================

    @Test
    void handleError_shouldUseStatusAttribute404() {

        when(request.getAttribute(
                "jakarta.servlet.error.status_code"
        )).thenReturn(404);

        String view =
                controller.handleError(
                        request,
                        model
                );

        assertEquals(
                "error",
                view
        );

        verify(model).addAttribute(
                "status",
                404
        );

        verify(model).addAttribute(
                "error",
                "Page Not Found"
        );

        verify(model).addAttribute(
                "message",
                "The requested page could not be found."
        );

        verifyNoInteractions(
                appLogService
        );
    }

    // ========================================================================
    // STATUS PARAMETER 401
    // ========================================================================

    @Test
    void handleError_shouldUseStatusParameter401WhenAttributeIsNull() {

        when(request.getAttribute(
                "jakarta.servlet.error.status_code"
        )).thenReturn(null);

        when(request.getParameter("status"))
                .thenReturn("401");

        String view =
                controller.handleError(
                        request,
                        model
                );

        assertEquals(
                "error",
                view
        );

        verify(model).addAttribute(
                "status",
                401
        );

        verify(model).addAttribute(
                "error",
                "Unauthorized"
        );

        verify(model).addAttribute(
                "message",
                "You must be authenticated to access this resource."
        );

        verifyNoInteractions(
                appLogService
        );
    }

    // ========================================================================
    // STATUS PARAMETER 403
    // ========================================================================

    @Test
    void handleError_shouldUseStatusParameter403WhenAttributeIsNull() {

        when(request.getAttribute(
                "jakarta.servlet.error.status_code"
        )).thenReturn(null);

        when(request.getParameter("status"))
                .thenReturn("403");

        String view =
                controller.handleError(
                        request,
                        model
                );

        assertEquals(
                "error",
                view
        );

        verify(model).addAttribute(
                "status",
                403
        );

        verify(model).addAttribute(
                "error",
                "Access Denied"
        );

        verify(model).addAttribute(
                "message",
                "You do not have permission to access this resource."
        );

        verifyNoInteractions(
                appLogService
        );
    }

    // ========================================================================
    // STATUS PARAMETER 404
    // ========================================================================

    @Test
    void handleError_shouldUseStatusParameter404WhenAttributeIsNull() {

        when(request.getAttribute(
                "jakarta.servlet.error.status_code"
        )).thenReturn(null);

        when(request.getParameter("status"))
                .thenReturn("404");

        String view =
                controller.handleError(
                        request,
                        model
                );

        assertEquals(
                "error",
                view
        );

        verify(model).addAttribute(
                "status",
                404
        );

        verify(model).addAttribute(
                "error",
                "Page Not Found"
        );

        verify(model).addAttribute(
                "message",
                "The requested page could not be found."
        );

        verifyNoInteractions(
                appLogService
        );
    }

    // ========================================================================
    // INVALID STATUS PARAMETER
    // ========================================================================

    @Test
    void handleError_shouldFallbackTo500ForInvalidStatusParameter() {

        when(request.getAttribute(
                "jakarta.servlet.error.status_code"
        )).thenReturn(null);

        when(request.getParameter("status"))
                .thenReturn("abc");

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getRequestURI())
                .thenReturn("/error");

        String view =
                controller.handleError(
                        request,
                        model
                );

        assertEquals(
                "error",
                view
        );

        verify(model).addAttribute(
                "status",
                500
        );

        verify(model).addAttribute(
                "error",
                "Internal Server Error"
        );

        verify(model).addAttribute(
                "message",
                "An unexpected error occurred."
        );

        verify(appLogService).logAsync(
                any(LogFormatDTO.class)
        );
    }

    // ========================================================================
    // UNSUPPORTED STATUS
    // ========================================================================

    @Test
    void handleError_shouldFallbackTo500ForUnsupportedStatus() {

        when(request.getAttribute(
                "jakarta.servlet.error.status_code"
        )).thenReturn(418);

        String view =
                controller.handleError(
                        request,
                        model
                );

        assertEquals(
                "error",
                view
        );

        verify(model).addAttribute(
                "status",
                500
        );

        verify(model).addAttribute(
                "error",
                "Internal Server Error"
        );

        verify(model).addAttribute(
                "message",
                "An unexpected error occurred."
        );

        /*
         * 418 is converted to 500 only for the page.
         * It is not logged as an actual internal server error.
         */
        verifyNoInteractions(
                appLogService
        );
    }

    // ========================================================================
    // EMPTY STATUS PARAMETER
    // ========================================================================

    @Test
    void handleError_shouldFallbackTo500ForEmptyStatusParameter() {

        when(request.getAttribute(
                "jakarta.servlet.error.status_code"
        )).thenReturn(null);

        when(request.getParameter("status"))
                .thenReturn("");

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getRequestURI())
                .thenReturn("/error");

        String view =
                controller.handleError(
                        request,
                        model
                );

        assertEquals(
                "error",
                view
        );

        verify(model).addAttribute(
                "status",
                500
        );

        verify(model).addAttribute(
                "error",
                "Internal Server Error"
        );

        verify(model).addAttribute(
                "message",
                "An unexpected error occurred."
        );

        verify(appLogService).logAsync(
                any(LogFormatDTO.class)
        );
    }

    // ========================================================================
    // 500 WITH ROOT CAUSE
    // ========================================================================

    @Test
    void handleError_shouldLogRootCauseFor500Error() {

        when(request.getAttribute(
                "jakarta.servlet.error.status_code"
        )).thenReturn(500);

        when(request.getAttribute(
                "jakarta.servlet.error.exception"
        )).thenReturn(
                new ServletException(
                        "Request processing failed",
                        new RuntimeException(
                                "Test error"
                        )
                )
        );

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getRequestURI())
                .thenReturn("/error");

        String view =
                controller.handleError(
                        request,
                        model
                );

        assertEquals(
                "error",
                view
        );

        verify(appLogService).logAsync(
                argThat(dto ->
                        "ERROR".equals(
                                dto.level()
                        )
                                && "GET /error".equals(
                                dto.method()
                        )
                                && "RuntimeException".equals(
                                dto.errorMessage()
                        )
                                && "Test error".equals(
                                dto.cause()
                        )
                )
        );

        verify(model).addAttribute(
                "status",
                500
        );

        verify(model).addAttribute(
                "error",
                "Internal Server Error"
        );

        verify(model).addAttribute(
                "message",
                "An unexpected error occurred."
        );
    }

    // ========================================================================
    // 500 WITHOUT EXCEPTION
    // ========================================================================

    @Test
    void handleError_shouldLogInternalServerErrorWhenExceptionIsAbsent() {

        when(request.getAttribute(
                "jakarta.servlet.error.status_code"
        )).thenReturn(500);

        when(request.getAttribute(
                "jakarta.servlet.error.exception"
        )).thenReturn(null);

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getRequestURI())
                .thenReturn("/error");

        String view =
                controller.handleError(
                        request,
                        model
                );

        assertEquals(
                "error",
                view
        );

        verify(appLogService).logAsync(
                argThat(dto ->
                        "ERROR".equals(
                                dto.level()
                        )
                                && "GET /error".equals(
                                dto.method()
                        )
                                && "Internal server error".equals(
                                dto.errorMessage()
                        )
                                && dto.cause() == null
                )
        );

        verify(model).addAttribute(
                "status",
                500
        );

        verify(model).addAttribute(
                "error",
                "Internal Server Error"
        );

        verify(model).addAttribute(
                "message",
                "An unexpected error occurred."
        );
    }

    // ========================================================================
    // DEEPEST ROOT CAUSE
    // ========================================================================

    @Test
    void handleError_shouldFindDeepestRootCause() {

        RuntimeException rootCause =
                new RuntimeException(
                        "Root cause"
                );

        IllegalStateException middleCause =
                new IllegalStateException(
                        "Middle cause",
                        rootCause
                );

        ServletException outerException =
                new ServletException(
                        "Outer exception",
                        middleCause
                );

        when(request.getAttribute(
                "jakarta.servlet.error.status_code"
        )).thenReturn(500);

        when(request.getAttribute(
                "jakarta.servlet.error.exception"
        )).thenReturn(
                outerException
        );

        when(request.getMethod())
                .thenReturn("GET");

        when(request.getRequestURI())
                .thenReturn("/error");

        controller.handleError(
                request,
                model
        );

        verify(appLogService).logAsync(
                argThat(dto ->
                        "ERROR".equals(
                                dto.level()
                        )
                                && "GET /error".equals(
                                dto.method()
                        )
                                && "RuntimeException".equals(
                                dto.errorMessage()
                        )
                                && "Root cause".equals(
                                dto.cause()
                        )
                )
        );
    }
}

