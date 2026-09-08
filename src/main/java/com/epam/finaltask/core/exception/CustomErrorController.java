package com.epam.finaltask.core.exception;

import com.epam.finaltask.log.AppLogService;
import com.epam.finaltask.log.LogFormatDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;

@Controller
@RequiredArgsConstructor
public class CustomErrorController implements ErrorController {

    private final MessageSource messageSource;
    private final AppLogService appLogService;

    @RequestMapping("/error")
    public String handleError(
            HttpServletRequest request,
            Model model
    ) {

        Object statusAttribute =
                request.getAttribute(
                        "jakarta.servlet.error.status_code"
                );

        int status;

        if (statusAttribute != null) {

            status = (Integer) statusAttribute;

        } else {

            String statusParameter =
                    request.getParameter("status");

            status =
                    statusParameter != null
                            ? parseStatus(statusParameter)
                            : 500;
        }

        /*
         * Log only actual internal server errors.
         */
        if (status == 500) {
            logServerError(request);
        }

        Locale locale =
                LocaleContextHolder.getLocale();

        String errorKey;
        String messageKey;

        String defaultError;
        String defaultMessage;

        switch (status) {

            case 401 -> {
                errorKey = "error.page.401.title";
                messageKey = "error.page.401.message";

                defaultError = "Unauthorized";
                defaultMessage =
                        "You must be authenticated to access this resource.";
            }

            case 403 -> {
                errorKey = "error.page.403.title";
                messageKey = "error.page.403.message";

                defaultError = "Access Denied";
                defaultMessage =
                        "You do not have permission to access this resource.";
            }

            case 404 -> {
                errorKey = "error.page.404.title";
                messageKey = "error.page.404.message";

                defaultError = "Page Not Found";
                defaultMessage =
                        "The requested page could not be found.";
            }

            default -> {
                status = 500;

                errorKey = "error.page.500.title";
                messageKey = "error.page.500.message";

                defaultError = "Internal Server Error";
                defaultMessage =
                        "An unexpected error occurred.";
            }
        }

        String error =
                getMessage(
                        errorKey,
                        defaultError,
                        locale
                );

        String message =
                getMessage(
                        messageKey,
                        defaultMessage,
                        locale
                );

        model.addAttribute(
                "status",
                status
        );

        model.addAttribute(
                "error",
                error
        );

        model.addAttribute(
                "message",
                message
        );

        return "error";
    }

    private void logServerError(
            HttpServletRequest request
    ) {

        Object exceptionAttribute =
                request.getAttribute(
                        "jakarta.servlet.error.exception"
                );

        String errorMessage =
                "Internal server error";

        String cause = null;

        if (exceptionAttribute instanceof Throwable throwable) {

            Throwable rootCause =
                    throwable;

            while (rootCause.getCause() != null) {
                rootCause =
                        rootCause.getCause();
            }

            errorMessage =
                    rootCause
                            .getClass()
                            .getSimpleName();

            cause =
                    rootCause.getMessage();
        }

        LogFormatDTO logDto =
                LogFormatDTO.builder()
                        .level("ERROR")
                        .method(
                                request.getMethod()
                                        + " "
                                        + request.getRequestURI()
                        )
                        .errorMessage(
                                errorMessage
                        )
                        .cause(cause)
                        .build();

        appLogService.logAsync(
                logDto
        );
    }

    private int parseStatus(
            String status
    ) {

        try {

            return Integer.parseInt(status);

        } catch (NumberFormatException e) {

            return 500;
        }
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

