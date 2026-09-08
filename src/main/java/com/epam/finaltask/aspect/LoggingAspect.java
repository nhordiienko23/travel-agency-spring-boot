package com.epam.finaltask.aspect;

import com.epam.finaltask.auth.LoginRequestDTO;
import com.epam.finaltask.auth.RegisterRequestDTO;
import com.epam.finaltask.log.AppLogService;
import com.epam.finaltask.log.AuditContext;
import com.epam.finaltask.log.LogFormatDTO;
import com.epam.finaltask.log.Loggable;
import com.epam.finaltask.user.AdminDepositBalanceRequestDTO;
import com.epam.finaltask.user.DepositBalanceRequestDTO;
import com.epam.finaltask.voucher.CreateVoucherRequestDTO;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private static final int MAX_LENGTH = 500;

    private final AppLogService appLogService;

    private final Logger log =
            LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@annotation(com.epam.finaltask.log.Loggable)")
    public Object logBusinessAction(
            ProceedingJoinPoint joinPoint
    ) throws Throwable {

        MethodSignature signature =
                (MethodSignature) joinPoint.getSignature();

        Method method =
                signature.getMethod();

        Loggable loggable =
                method.getAnnotation(Loggable.class);

        String originalAction =
                loggable.value();

        Object[] args =
                joinPoint.getArgs();

        String username =
                resolveUsername(
                        originalAction,
                        args
                );

        /*
         * DEBUG:
         * technical information about the beginning
         * of a business operation.
         */
        String debugMessage =
                String.format(
                        "Starting business action: User='%s' | Action='%s' | Method='%s'",
                        username,
                        originalAction,
                        signature.getName()
                );

        writeLog(
                "DEBUG",
                signature,
                debugMessage
        );

        try {

            Object result =
                    joinPoint.proceed();

            String action =
                    resolveAction(
                            originalAction,
                            true
                    );

            String details =
                    resolveDetails(
                            action,
                            args
                    );

            String message =
                    buildMessage(
                            username,
                            action,
                            details
                    );

            /*
             * INFO:
             * successful business operation.
             */
            writeLog(
                    "INFO",
                    signature,
                    message
            );

            return result;

        } catch (Throwable exception) {

            String action =
                    resolveAction(
                            originalAction,
                            false
                    );

            String details =
                    resolveDetails(
                            action,
                            args
                    );

            String message =
                    buildErrorMessage(
                            username,
                            action,
                            details,
                            exception
                    );

            /*
             * WARN:
             * expected business/security failure,
             * for example invalid credentials,
             * insufficient balance, etc.
             */
            writeLog(
                    "WARN",
                    signature,
                    message
            );

            throw exception;

        } finally {

            AuditContext.clear();
        }
    }

    private String resolveAction(
            String action,
            boolean successful
    ) {

        if ("LOGIN".equals(action)) {

            return successful
                    ? "LOGIN_SUCCESS"
                    : "LOGIN_FAILED";
        }

        return action;
    }

    private void writeLog(
            String level,
            MethodSignature signature,
            String message
    ) {

        String fullMethodName =
                signature.getDeclaringTypeName()
                        + "."
                        + signature.getName();

        /*
         * Write the message to the normal application log
         * using the correct SLF4J level.
         */
        switch (level) {

            case "DEBUG" -> {
                if (log.isDebugEnabled()) {
                    log.debug(message);
                }
            }

            case "INFO" ->
                    log.info(message);

            case "WARN" ->
                    log.warn(message);

            case "ERROR" ->
                    log.error(message);

            default ->
                    log.info(message);
        }

        /*
         * Also persist the log entry in the database.
         */
        LogFormatDTO logDto =
                LogFormatDTO.builder()
                        .level(level)
                        .method(fullMethodName)
                        .result(message)
                        .build();

        appLogService.logAsync(logDto);
    }

    private String buildMessage(
            String username,
            String action,
            String details
    ) {

        return String.format(
                "User='%s' | Action='%s' | Details='%s'",
                username,
                action,
                details
        );
    }

    private String buildErrorMessage(
            String username,
            String action,
            String details,
            Throwable exception
    ) {

        String errorMessage =
                exception.getMessage();

        if (errorMessage == null
                || errorMessage.isBlank()) {

            errorMessage =
                    exception
                            .getClass()
                            .getSimpleName();
        }

        return String.format(
                "User='%s' | Action='%s' | Details='%s' | Error='%s'",
                username,
                action,
                details,
                sanitize(errorMessage)
        );
    }

    private String resolveUsername(
            String action,
            Object[] args
    ) {

        if ("REGISTER_USER".equals(action)) {

            for (Object arg : args) {

                if (arg instanceof RegisterRequestDTO request) {

                    return safe(
                            request.username()
                    );
                }
            }
        }

        if ("LOGIN".equals(action)
                || "LOGIN_SUCCESS".equals(action)
                || "LOGIN_FAILED".equals(action)) {

            for (Object arg : args) {

                if (arg instanceof LoginRequestDTO request) {

                    return safe(
                            request.username()
                    );
                }
            }
        }

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication
                instanceof AnonymousAuthenticationToken)) {

            return safe(
                    authentication.getName()
            );
        }

        return "System/Anonymous";
    }

    private String resolveDetails(
            String action,
            Object[] args
    ) {

        String contextDetails =
                AuditContext.getDetails();

        if (contextDetails != null
                && !contextDetails.isBlank()) {

            return truncate(
                    contextDetails
            );
        }

        return truncate(
                formatSafeDetails(
                        action,
                        args
                )
        );
    }

    private String formatSafeDetails(
            String action,
            Object[] args
    ) {

        return switch (action) {

            case "REGISTER_USER" ->
                    formatRegister(args);

            case "LOGIN_SUCCESS",
                 "LOGIN_FAILED" ->
                    formatLogin(args);

            case "DEPOSIT_BALANCE_BY_USER" ->
                    formatDepositByUser(args);

            case "DEPOSIT_BALANCE_BY_ADMIN" ->
                    formatDepositByAdmin(args);

            case "CREATE_NEW_TOUR" ->
                    formatCreateVoucher(args);

            case "ORDER_TOUR" ->
                    formatOrder(args);

            case "DELETE_TOUR" ->
                    formatSingleValue(
                            args,
                            "voucherId"
                    );

            case "CANCEL_TOUR" ->
                    formatCancelTour(args);

            case "TOGGLE_USER_BLOCK_STATUS" ->
                    formatSingleValue(
                            args,
                            "username"
                    );

            case "DELETE_ACCOUNT" ->
                    formatSingleValue(
                            args,
                            "username"
                    );

            case "CHANGE_PASSWORD" ->
                    "password=***";

            default ->
                    "details=not_available";
        };
    }

    private String formatRegister(
            Object[] args
    ) {

        for (Object arg : args) {

            if (arg instanceof RegisterRequestDTO request) {

                return String.format(
                        "username=%s, email=%s, lastName=%s, phoneNumber=%s",
                        safe(request.username()),
                        safe(request.email()),
                        safe(request.lastName()),
                        safe(request.phoneNumber())
                );
            }
        }

        return "registration";
    }

    private String formatLogin(
            Object[] args
    ) {

        for (Object arg : args) {

            if (arg instanceof LoginRequestDTO request) {

                return String.format(
                        "username=%s",
                        safe(request.username())
                );
            }
        }

        return "login";
    }

    private String formatDepositByUser(
            Object[] args
    ) {

        for (Object arg : args) {

            if (arg instanceof DepositBalanceRequestDTO request) {

                return String.format(
                        "amount=%s",
                        request.amount()
                );
            }
        }

        return "deposit";
    }

    private String formatDepositByAdmin(
            Object[] args
    ) {

        for (Object arg : args) {

            if (arg instanceof AdminDepositBalanceRequestDTO request) {

                return String.format(
                        "userId=%s, amount=%s",
                        request.userId(),
                        request.amount()
                );
            }
        }

        return "deposit";
    }

    private String formatCreateVoucher(
            Object[] args
    ) {

        for (Object arg : args) {

            if (arg instanceof CreateVoucherRequestDTO request) {

                return String.format(
                        "title=%s, price=%s, tourType=%s, transferType=%s, hotelType=%s, arrivalDate=%s, evictionDate=%s",
                        safe(request.title()),
                        request.price(),
                        safe(request.tourType()),
                        safe(request.transferType()),
                        safe(request.hotelType()),
                        request.arrivalDate(),
                        request.evictionDate()
                );
            }
        }

        return "createVoucher";
    }

    private String formatOrder(
            Object[] args
    ) {

        if (args.length >= 2) {

            return String.format(
                    "voucherId=%s, userId=%s",
                    safe(args[0]),
                    safe(args[1])
            );
        }

        return "order";
    }

    private String formatCancelTour(
            Object[] args
    ) {

        if (args.length >= 2) {

            return String.format(
                    "voucherId=%s, username=%s",
                    safe(args[0]),
                    safe(args[1])
            );
        }

        return "cancelTour";
    }

    private String formatSingleValue(
            Object[] args,
            String fieldName
    ) {

        if (args.length == 0) {
            return fieldName + "=null";
        }

        return fieldName
                + "="
                + safe(args[0]);
    }

    private String safe(
            Object value
    ) {

        if (value == null) {
            return "null";
        }

        if (value instanceof UUID) {
            return value.toString();
        }

        return sanitize(
                String.valueOf(value)
        );
    }

    private String sanitize(
            String value
    ) {

        if (value == null) {
            return "null";
        }

        return value.replaceAll(
                "(?i)(password|token|refreshToken|accessToken)=([^,\\]\\s]+)",
                "$1=***"
        );
    }

    private String truncate(
            String value
    ) {

        if (value == null) {
            return "null";
        }

        return value.length() > MAX_LENGTH
                ? value.substring(0, MAX_LENGTH) + "..."
                : value;
    }
}

