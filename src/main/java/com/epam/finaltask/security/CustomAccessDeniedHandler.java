package com.epam.finaltask.security;

import com.epam.finaltask.log.AppLogService;
import com.epam.finaltask.log.LogFormatDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler
        implements AccessDeniedHandler {

    private final AppLogService appLogService;
    private final MessageSource messageSource;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String username =
                authentication != null
                        && authentication.isAuthenticated()
                        && !(authentication
                        instanceof AnonymousAuthenticationToken)
                        ? authentication.getName()
                        : "Anonymous/Unauthenticated";

        LogFormatDTO logDto =
                LogFormatDTO.builder()
                        .level("WARN")
                        .method(
                                request.getMethod()
                                        + " "
                                        + request.getRequestURI()
                        )
                        .errorMessage(
                                "Access denied for user '"
                                        + username
                                        + "'"
                        )
                        .build();

        appLogService.logAsync(logDto);

        /*
         * REST API:
         * return JSON instead of redirecting to the HTML error page.
         */
        if (request.getRequestURI().startsWith(
                request.getContextPath() + "/api/"
        )) {

            sendJsonResponse(
                    response
            );

            return;
        }

        /*
         * Web pages:
         * keep the existing HTML error page.
         */
        response.sendRedirect(
                request.getContextPath()
                        + "/error?status=403"
        );
    }

    private void sendJsonResponse(
            HttpServletResponse response
    ) throws IOException {

        Locale locale =
                LocaleContextHolder.getLocale();

        Map<String, Object> body =
                new HashMap<>();

        body.put(
                "timestamp",
                java.time.LocalDateTime.now()
        );

        body.put(
                "status",
                HttpServletResponse.SC_FORBIDDEN
        );

        body.put(
                "error",
                messageSource.getMessage(
                        "error.forbidden",
                        null,
                        "Forbidden",
                        locale
                )
        );

        body.put(
                "message",
                messageSource.getMessage(
                        "err.access.denied",
                        null,
                        "Access denied to this resource",
                        locale
                )
        );

        response.setStatus(
                HttpServletResponse.SC_FORBIDDEN
        );

        response.setContentType(
                "application/json"
        );

        response.setCharacterEncoding(
                "UTF-8"
        );

        String json = """
                {
                  "timestamp": "%s",
                  "status": %d,
                  "error": "%s",
                  "message": "%s"
                }
                """.formatted(
                body.get("timestamp"),
                body.get("status"),
                escapeJson(
                        String.valueOf(body.get("error"))
                ),
                escapeJson(
                        String.valueOf(body.get("message"))
                )
        );

        response.getWriter().write(json);
    }

    private String escapeJson(String value) {

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}

