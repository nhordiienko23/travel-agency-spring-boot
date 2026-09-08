package com.epam.finaltask.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    private final MessageSource messageSource;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        /*
         * REST API:
         * return JSON 401.
         */
        if (request.getRequestURI().startsWith(
                request.getContextPath() + "/api/"
        )) {

            sendJsonResponse(response);

            return;
        }

        /*
         * Web pages:
         * open the custom HTML error page.
         */
        response.sendRedirect(
                request.getContextPath()
                        + "/error?status=401"
        );
    }

    private void sendJsonResponse(
            HttpServletResponse response
    ) throws IOException {

        Locale locale =
                LocaleContextHolder.getLocale();

        String error =
                messageSource.getMessage(
                        "error.unauthorized",
                        null,
                        "Unauthorized",
                        locale
                );

        String message =
                messageSource.getMessage(
                        "err.authentication.required",
                        null,
                        "Authentication is required to access this resource.",
                        locale
                );

        response.setStatus(
                HttpServletResponse.SC_UNAUTHORIZED
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
                  "status": 401,
                  "error": "%s",
                  "message": "%s"
                }
                """.formatted(
                LocalDateTime.now(),
                escapeJson(error),
                escapeJson(message)
        );

        response.getWriter().write(json);
    }

    private String escapeJson(
            String value
    ) {

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}

