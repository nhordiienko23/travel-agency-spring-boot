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
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoggingAspectTest {

    private final AppLogService appLogService =
            mock(AppLogService.class);

    @AfterEach
    void tearDown() {
        AuditContext.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    void successfulAction_shouldLogInfo() throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("ORDER_TOUR");

        UUID voucherId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(joinPoint.getArgs())
                .thenReturn(new Object[]{
                        voucherId,
                        userId
                });

        when(joinPoint.proceed())
                .thenReturn("success");

        setAuthenticatedUser("admin");

        Object result =
                aspect.logBusinessAction(joinPoint);

        assertEquals("success", result);

        LogFormatDTO dto = captureLog();

        assertEquals("INFO", dto.level());

        assertEquals(
                LoggingTestTarget.class.getName()
                        + ".orderTour",
                dto.method()
        );

        assertTrue(dto.result().contains("User='admin'"));
        assertTrue(dto.result().contains("Action='ORDER_TOUR'"));
        assertTrue(
                dto.result().contains(
                        "voucherId=" + voucherId
                )
        );
        assertTrue(
                dto.result().contains(
                        "userId=" + userId
                )
        );
    }

    @Test
    void register_shouldUseUsernameFromRequest()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        RegisterRequestDTO request =
                mock(RegisterRequestDTO.class);

        when(request.username())
                .thenReturn("nikita");

        when(request.email())
                .thenReturn("nikita@test.com");

        when(request.lastName())
                .thenReturn("Ivanov");

        when(request.phoneNumber())
                .thenReturn("+48123123123");

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("REGISTER_USER");

        when(joinPoint.getArgs())
                .thenReturn(new Object[]{request});

        when(joinPoint.proceed())
                .thenReturn("registered");

        Object result =
                aspect.logBusinessAction(joinPoint);

        assertEquals("registered", result);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "User='nikita'"
                )
        );

        assertTrue(
                dto.result().contains(
                        "Action='REGISTER_USER'"
                )
        );

        assertTrue(
                dto.result().contains(
                        "username=nikita"
                )
        );

        assertTrue(
                dto.result().contains(
                        "email=nikita@test.com"
                )
        );

        assertFalse(
                dto.result().contains(
                        "password"
                )
        );
    }

    @Test
    void registerWithoutRequest_shouldUseFallback()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("REGISTER_USER");

        when(joinPoint.getArgs())
                .thenReturn(new Object[]{"something"});

        when(joinPoint.proceed())
                .thenReturn(null);

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "User='System/Anonymous'"
                )
        );

        assertTrue(
                dto.result().contains(
                        "Details='registration'"
                )
        );
    }

    @Test
    void login_shouldBeLoggedAsLoginSuccess()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        LoginRequestDTO request =
                mock(LoginRequestDTO.class);

        when(request.username())
                .thenReturn("nikita");

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("LOGIN");

        when(joinPoint.getArgs())
                .thenReturn(new Object[]{request});

        when(joinPoint.proceed())
                .thenReturn("JWT-TOKEN");

        Object result =
                aspect.logBusinessAction(joinPoint);

        assertEquals("JWT-TOKEN", result);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "Action='LOGIN_SUCCESS'"
                )
        );

        assertTrue(
                dto.result().contains(
                        "username=nikita"
                )
        );

        assertFalse(
                dto.result().contains(
                        "JWT-TOKEN"
                )
        );

        assertFalse(
                dto.result().contains(
                        "password"
                )
        );
    }

    @Test
    void login_shouldBeLoggedAsLoginFailed()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        LoginRequestDTO request =
                mock(LoginRequestDTO.class);

        when(request.username())
                .thenReturn("nikita");

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("LOGIN");

        when(joinPoint.getArgs())
                .thenReturn(new Object[]{request});

        when(joinPoint.proceed())
                .thenThrow(
                        new RuntimeException(
                                "Invalid credentials"
                        )
                );

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> aspect.logBusinessAction(
                                joinPoint
                        )
                );

        assertEquals(
                "Invalid credentials",
                exception.getMessage()
        );

        LogFormatDTO dto = captureLog();

        assertEquals(
                "WARN",
                dto.level()
        );

        assertTrue(
                dto.result().contains(
                        "Action='LOGIN_FAILED'"
                )
        );

        assertTrue(
                dto.result().contains(
                        "username=nikita"
                )
        );

        assertTrue(
                dto.result().contains(
                        "Error='Invalid credentials'"
                )
        );
    }

    @Test
    void loginWithoutRequest_shouldUseLoginFallback()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("LOGIN");

        when(joinPoint.getArgs())
                .thenReturn(new Object[]{"something"});

        when(joinPoint.proceed())
                .thenReturn(null);

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "Details='login'"
                )
        );

        assertTrue(
                dto.result().contains(
                        "User='System/Anonymous'"
                )
        );
    }

    @Test
    void anonymousAuthentication_shouldUseSystemAnonymous()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

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

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("DELETE_TOUR");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{"voucher-id"}
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "User='System/Anonymous'"
                )
        );
    }

    @Test
    void missingAuthentication_shouldUseSystemAnonymous()
            throws Throwable {

        SecurityContextHolder.clearContext();

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("DELETE_TOUR");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{"voucher-id"}
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "User='System/Anonymous'"
                )
        );
    }

    @Test
    void unauthenticatedUser_shouldUseSystemAnonymous()
            throws Throwable {

        Authentication authentication =
                mock(Authentication.class);

        when(authentication.isAuthenticated())
                .thenReturn(false);

        when(authentication.getName())
                .thenReturn("ignored");

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("DELETE_TOUR");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{"voucher-id"}
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "User='System/Anonymous'"
                )
        );
    }

    @Test
    void authenticatedUser_shouldUseAuthenticationName()
            throws Throwable {

        setAuthenticatedUser("manager");

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("DELETE_TOUR");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{"voucher-id"}
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "User='manager'"
                )
        );
    }

    @Test
    void depositByUser_shouldFormatAmount()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        DepositBalanceRequestDTO request =
                mock(DepositBalanceRequestDTO.class);

        when(request.amount())
                .thenReturn(123.45);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint(
                        "DEPOSIT_BALANCE_BY_USER"
                );

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{request}
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        setAuthenticatedUser("qqq");

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "amount=123.45"
                )
        );
    }

    @Test
    void depositByUserWithoutRequest_shouldUseFallback()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint(
                        "DEPOSIT_BALANCE_BY_USER"
                );

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{"something"}
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "Details='deposit'"
                )
        );
    }

    @Test
    void depositByAdmin_shouldFormatUserIdAndAmount()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        UUID userId = UUID.randomUUID();

        AdminDepositBalanceRequestDTO request =
                mock(
                        AdminDepositBalanceRequestDTO.class
                );

        when(request.userId())
                .thenReturn(userId);

        when(request.amount())
                .thenReturn(500.0);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint(
                        "DEPOSIT_BALANCE_BY_ADMIN"
                );

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{request}
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        setAuthenticatedUser("admin");

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "userId=" + userId
                )
        );

        assertTrue(
                dto.result().contains(
                        "amount=500.0"
                )
        );
    }

    @Test
    void depositByAdminWithoutRequest_shouldUseFallback()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint(
                        "DEPOSIT_BALANCE_BY_ADMIN"
                );

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{"something"}
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "Details='deposit'"
                )
        );
    }

    @Test
    void createVoucher_shouldFormatSafeFields()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        CreateVoucherRequestDTO request =
                mock(CreateVoucherRequestDTO.class);

        when(request.title())
                .thenReturn("Paris");

        when(request.price())
                .thenReturn(1200.0);

        when(request.tourType())
                .thenReturn("CULTURAL");

        when(request.transferType())
                .thenReturn("PLANE");

        when(request.hotelType())
                .thenReturn("FOUR_STARS");

        when(request.arrivalDate())
                .thenReturn(
                        LocalDate.of(2026, 11, 1)
                );

        when(request.evictionDate())
                .thenReturn(
                        LocalDate.of(2026, 11, 10)
                );

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("CREATE_NEW_TOUR");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{request}
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        setAuthenticatedUser("admin");

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "title=Paris"
                )
        );

        assertTrue(
                dto.result().contains(
                        "price=1200.0"
                )
        );

        assertTrue(
                dto.result().contains(
                        "tourType=CULTURAL"
                )
        );

        assertTrue(
                dto.result().contains(
                        "transferType=PLANE"
                )
        );

        assertTrue(
                dto.result().contains(
                        "hotelType=FOUR_STARS"
                )
        );

        assertTrue(
                dto.result().contains(
                        "arrivalDate=2026-11-01"
                )
        );

        assertTrue(
                dto.result().contains(
                        "evictionDate=2026-11-10"
                )
        );
    }

    @Test
    void createVoucherWithoutRequest_shouldUseFallback()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("CREATE_NEW_TOUR");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{"something"}
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "Details='createVoucher'"
                )
        );
    }

    @Test
    void order_shouldFormatIds()
            throws Throwable {

        UUID voucherId =
                UUID.randomUUID();

        UUID userId =
                UUID.randomUUID();

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("ORDER_TOUR");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{
                                voucherId,
                                userId
                        }
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        setAuthenticatedUser("qqq");

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "voucherId=" + voucherId
                )
        );

        assertTrue(
                dto.result().contains(
                        "userId=" + userId
                )
        );
    }

    @Test
    void orderWithNullArgument_shouldUseNullText()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("ORDER_TOUR");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{
                                null,
                                "user-id"
                        }
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "voucherId=null"
                )
        );
    }

    @Test
    void orderWithLessThanTwoArguments_shouldUseFallback()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("ORDER_TOUR");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{"voucher-id"}
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "Details='order'"
                )
        );
    }

    @Test
    void deleteTour_shouldFormatVoucherId()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("DELETE_TOUR");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{"voucher-id"}
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        setAuthenticatedUser("admin");

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "voucherId=voucher-id"
                )
        );
    }

    @Test
    void singleValueWithoutArguments_shouldReturnNull()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("DELETE_TOUR");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[0]
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "voucherId=null"
                )
        );
    }

    @Test
    void cancelTour_shouldFormatVoucherAndUsername()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("CANCEL_TOUR");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{
                                "voucher-id",
                                "qqq"
                        }
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        setAuthenticatedUser("qqq");

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "voucherId=voucher-id"
                )
        );

        assertTrue(
                dto.result().contains(
                        "username=qqq"
                )
        );
    }

    @Test
    void cancelWithLessThanTwoArguments_shouldUseFallback()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("CANCEL_TOUR");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{"voucher-id"}
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "Details='cancelTour'"
                )
        );
    }

    @Test
    void toggleUserBlockStatus_shouldFormatUsername()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint(
                        "TOGGLE_USER_BLOCK_STATUS"
                );

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{"john"}
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        setAuthenticatedUser("admin");

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "username=john"
                )
        );
    }

    @Test
    void deleteAccount_shouldFormatUsername()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("DELETE_ACCOUNT");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{"john"}
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        setAuthenticatedUser("john");

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "username=john"
                )
        );
    }

    @Test
    void changePassword_shouldHidePassword()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("CHANGE_PASSWORD");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{
                                "currentPassword",
                                "newPassword"
                        }
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        setAuthenticatedUser("john");

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "password=***"
                )
        );

        assertFalse(
                dto.result().contains(
                        "currentPassword"
                )
        );

        assertFalse(
                dto.result().contains(
                        "newPassword"
                )
        );
    }

    @Test
    void defaultAction_shouldUseFallbackDetails()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("UNKNOWN_ACTION");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[0]
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        setAuthenticatedUser("admin");

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "details=not_available"
                )
        );
    }

    @Test
    void genericDetails_shouldUseFallbackForUnknownAction()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("UNKNOWN_ACTION");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{null}
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "Action='UNKNOWN_ACTION'"
                )
        );

        assertTrue(
                dto.result().contains(
                        "Details='details=not_available'"
                )
        );
    }

    @Test
    void exceptionMessageShouldSanitizeSensitiveToken()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("ORDER_TOUR");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{
                                "voucher",
                                "user"
                        }
                );

        when(joinPoint.proceed())
                .thenThrow(
                        new RuntimeException(
                                "accessToken=secret123"
                        )
                );

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> aspect.logBusinessAction(
                                joinPoint
                        )
                );

        assertEquals(
                "accessToken=secret123",
                exception.getMessage()
        );

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "accessToken=***"
                )
        );

        assertFalse(
                dto.result().contains(
                        "secret123"
                )
        );
    }

    @Test
    void auditContext_shouldOverrideDefaultDetails()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        AuditContext.setDetails(
                "price: 100.00 -> 150.00"
        );

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("UPDATE_TOUR_INFO");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[0]
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        setAuthenticatedUser("admin");

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "price: 100.00 -> 150.00"
                )
        );

        assertNull(
                AuditContext.getDetails()
        );
    }

    @Test
    void auditContext_blankValue_shouldUseDefaultDetails()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        AuditContext.setDetails("   ");

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("UNKNOWN_ACTION");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[0]
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "details=not_available"
                )
        );
    }

    @Test
    void longAuditContext_shouldBeTruncated()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        String longDetails =
                "a".repeat(501);

        AuditContext.setDetails(
                longDetails
        );

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("UPDATE_TOUR_INFO");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[0]
                );

        when(joinPoint.proceed())
                .thenReturn(null);

        aspect.logBusinessAction(joinPoint);

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "a".repeat(500) + "..."
                )
        );

        assertFalse(
                dto.result().contains(
                        "a".repeat(501)
                )
        );
    }

    @Test
    void exceptionWithoutMessage_shouldUseExceptionClassName()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("ORDER_TOUR");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{
                                "voucher",
                                "user"
                        }
                );

        when(joinPoint.proceed())
                .thenThrow(
                        new RuntimeException()
                );

        assertThrows(
                RuntimeException.class,
                () -> aspect.logBusinessAction(
                        joinPoint
                )
        );

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "Error='RuntimeException'"
                )
        );
    }

    @Test
    void exceptionWithBlankMessage_shouldUseExceptionClassName()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("ORDER_TOUR");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{
                                "voucher",
                                "user"
                        }
                );

        when(joinPoint.proceed())
                .thenThrow(
                        new RuntimeException("   ")
                );

        assertThrows(
                RuntimeException.class,
                () -> aspect.logBusinessAction(
                        joinPoint
                )
        );

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "Error='RuntimeException'"
                )
        );
    }

    @Test
    void exceptionMessageShouldBeSanitized()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("ORDER_TOUR");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{
                                "voucher",
                                "user"
                        }
                );

        when(joinPoint.proceed())
                .thenThrow(
                        new RuntimeException(
                                "accessToken=secret123"
                        )
                );

        assertThrows(
                RuntimeException.class,
                () -> aspect.logBusinessAction(
                        joinPoint
                )
        );

        LogFormatDTO dto = captureLog();

        assertTrue(
                dto.result().contains(
                        "accessToken=***"
                )
        );

        assertFalse(
                dto.result().contains(
                        "secret123"
                )
        );
    }

    @Test
    void auditContext_shouldBeClearedAfterException()
            throws Throwable {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        AuditContext.setDetails(
                "temporary details"
        );

        ProceedingJoinPoint joinPoint =
                mockJoinPoint("ORDER_TOUR");

        when(joinPoint.getArgs())
                .thenReturn(
                        new Object[]{
                                "voucher",
                                "user"
                        }
                );

        when(joinPoint.proceed())
                .thenThrow(
                        new RuntimeException(
                                "failure"
                        )
                );

        assertThrows(
                RuntimeException.class,
                () -> aspect.logBusinessAction(
                        joinPoint
                )
        );

        assertNull(
                AuditContext.getDetails()
        );
    }

    @Test
    void resolveAction_shouldReturnSameActionForNonLogin() {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        String result =
                invokePrivate(
                        aspect,
                        "resolveAction",
                        "ORDER_TOUR",
                        true
                );

        assertEquals(
                "ORDER_TOUR",
                result
        );
    }

    @Test
    void resolveAction_shouldReturnLoginSuccess() {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        String result =
                invokePrivate(
                        aspect,
                        "resolveAction",
                        "LOGIN",
                        true
                );

        assertEquals(
                "LOGIN_SUCCESS",
                result
        );
    }

    @Test
    void resolveAction_shouldReturnLoginFailed() {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        String result =
                invokePrivate(
                        aspect,
                        "resolveAction",
                        "LOGIN",
                        false
                );

        assertEquals(
                "LOGIN_FAILED",
                result
        );
    }

    @Test
    void resolveUsername_shouldReturnRegistrationUsername() {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        RegisterRequestDTO request =
                mock(RegisterRequestDTO.class);

        when(request.username())
                .thenReturn("registerUser");

        String result =
                invokePrivate(
                        aspect,
                        "resolveUsername",
                        "REGISTER_USER",
                        new Object[]{request}
                );

        assertEquals(
                "registerUser",
                result
        );
    }

    @Test
    void resolveUsername_shouldUseAuthenticationForRegisterWithoutRequest() {

        setAuthenticatedUser("admin");

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        String result =
                invokePrivate(
                        aspect,
                        "resolveUsername",
                        "REGISTER_USER",
                        new Object[]{"invalid"}
                );

        assertEquals(
                "admin",
                result
        );
    }

    @Test
    void resolveUsername_shouldReturnLoginUsernameForLoginSuccess() {

        LoginRequestDTO request =
                mock(LoginRequestDTO.class);

        when(request.username())
                .thenReturn("successUser");

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        String result =
                invokePrivate(
                        aspect,
                        "resolveUsername",
                        "LOGIN_SUCCESS",
                        new Object[]{request}
                );

        assertEquals(
                "successUser",
                result
        );
    }

    @Test
    void resolveUsername_shouldReturnLoginUsernameForLoginFailed() {

        LoginRequestDTO request =
                mock(LoginRequestDTO.class);

        when(request.username())
                .thenReturn("failedUser");

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        String result =
                invokePrivate(
                        aspect,
                        "resolveUsername",
                        "LOGIN_FAILED",
                        new Object[]{request}
                );

        assertEquals(
                "failedUser",
                result
        );
    }

    @Test
    void resolveUsername_shouldUseAuthenticationForOtherAction() {

        setAuthenticatedUser("manager");

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        String result =
                invokePrivate(
                        aspect,
                        "resolveUsername",
                        "ORDER_TOUR",
                        new Object[0]
                );

        assertEquals(
                "manager",
                result
        );
    }

    @Test
    void resolveUsername_shouldUseAnonymousWhenAuthenticationIsNull() {

        SecurityContextHolder.clearContext();

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        String result =
                invokePrivate(
                        aspect,
                        "resolveUsername",
                        "ORDER_TOUR",
                        new Object[0]
                );

        assertEquals(
                "System/Anonymous",
                result
        );
    }

    @Test
    void resolveUsername_shouldUseAnonymousWhenAuthenticationIsNotAuthenticated() {

        Authentication authentication =
                mock(Authentication.class);

        when(authentication.isAuthenticated())
                .thenReturn(false);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        String result =
                invokePrivate(
                        aspect,
                        "resolveUsername",
                        "ORDER_TOUR",
                        new Object[0]
                );

        assertEquals(
                "System/Anonymous",
                result
        );
    }

    @Test
    void resolveUsername_shouldUseAnonymousForAnonymousAuthentication() {

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

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        String result =
                invokePrivate(
                        aspect,
                        "resolveUsername",
                        "ORDER_TOUR",
                        new Object[0]
                );

        assertEquals(
                "System/Anonymous",
                result
        );
    }

    @Test
    void safe_shouldHandleUuid() {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        UUID id =
                UUID.randomUUID();

        String result =
                invokePrivate(
                        aspect,
                        "safe",
                        id
                );

        assertEquals(
                id.toString(),
                result
        );
    }

    @Test
    void safe_shouldHandleNull() {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        String result =
                invokePrivate(
                        aspect,
                        "safe",
                        (Object) null
                );

        assertEquals(
                "null",
                result
        );
    }

    @Test
    void sanitize_shouldHandleNull() {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        String result =
                invokePrivate(
                        aspect,
                        "sanitize",
                        (String) null
                );

        assertEquals(
                "null",
                result
        );
    }

    @Test
    void sanitize_shouldHidePassword() {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        String result =
                invokePrivate(
                        aspect,
                        "sanitize",
                        "password=secret123"
                );

        assertEquals(
                "password=***",
                result
        );
    }

    @Test
    void sanitize_shouldHideToken() {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        String result =
                invokePrivate(
                        aspect,
                        "sanitize",
                        "token=secret123"
                );

        assertEquals(
                "token=***",
                result
        );
    }

    @Test
    void sanitize_shouldHideRefreshToken() {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        String result =
                invokePrivate(
                        aspect,
                        "sanitize",
                        "refreshToken=secret123"
                );

        assertEquals(
                "refreshToken=***",
                result
        );
    }

    @Test
    void sanitize_shouldHideAccessToken() {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        String result =
                invokePrivate(
                        aspect,
                        "sanitize",
                        "accessToken=secret123"
                );

        assertEquals(
                "accessToken=***",
                result
        );
    }

    @Test
    void truncate_shouldHandleNull() {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        String result =
                invokePrivate(
                        aspect,
                        "truncate",
                        (String) null
                );

        assertEquals(
                "null",
                result
        );
    }

    @Test
    void truncate_shouldNotChangeShortText() {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        String result =
                invokePrivate(
                        aspect,
                        "truncate",
                        "short text"
                );

        assertEquals(
                "short text",
                result
        );
    }

    @Test
    void truncate_shouldCutLongText() {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        String value =
                "x".repeat(501);

        String result =
                invokePrivate(
                        aspect,
                        "truncate",
                        value
                );

        assertEquals(
                "x".repeat(500) + "...",
                result
        );
    }

    @Test
    void writeLog_shouldSaveInfoLog() {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        Logger logger =
                mock(Logger.class);

        ReflectionTestUtils.setField(
                aspect,
                "log",
                logger
        );

        MethodSignature signature =
                mock(MethodSignature.class);

        when(signature.getDeclaringTypeName())
                .thenReturn("com.example.Test");

        when(signature.getName())
                .thenReturn("test");

        invokePrivate(
                aspect,
                "writeLog",
                "INFO",
                signature,
                "message"
        );

        verify(logger)
                .info("message");

        ArgumentCaptor<LogFormatDTO> captor =
                ArgumentCaptor.forClass(
                        LogFormatDTO.class
                );

        verify(appLogService)
                .logAsync(captor.capture());

        LogFormatDTO dto =
                captor.getValue();

        assertEquals(
                "INFO",
                dto.level()
        );

        assertEquals(
                "com.example.Test.test",
                dto.method()
        );

        assertEquals(
                "message",
                dto.result()
        );
    }

    @Test
    void writeLog_shouldExecuteDebugBranch() {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        Logger logger =
                mock(Logger.class);

        when(logger.isDebugEnabled())
                .thenReturn(true);

        ReflectionTestUtils.setField(
                aspect,
                "log",
                logger
        );

        MethodSignature signature =
                mock(MethodSignature.class);

        when(signature.getDeclaringTypeName())
                .thenReturn("com.example.Test");

        when(signature.getName())
                .thenReturn("test");

        invokePrivate(
                aspect,
                "writeLog",
                "DEBUG",
                signature,
                "debug message"
        );

        verify(logger)
                .isDebugEnabled();

        verify(logger)
                .debug("debug message");

        verify(appLogService)
                .logAsync(any(LogFormatDTO.class));
    }

    @Test
    void writeLog_shouldSkipDebugWhenDebugDisabled() {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        Logger logger =
                mock(Logger.class);

        when(logger.isDebugEnabled())
                .thenReturn(false);

        ReflectionTestUtils.setField(
                aspect,
                "log",
                logger
        );

        MethodSignature signature =
                mock(MethodSignature.class);

        when(signature.getDeclaringTypeName())
                .thenReturn("com.example.Test");

        when(signature.getName())
                .thenReturn("test");

        invokePrivate(
                aspect,
                "writeLog",
                "DEBUG",
                signature,
                "debug message"
        );

        verify(logger)
                .isDebugEnabled();

        verify(logger, never())
                .debug(anyString());

        verify(appLogService)
                .logAsync(any(LogFormatDTO.class));
    }

    @Test
    void writeLog_shouldExecuteWarnBranch() {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        Logger logger =
                mock(Logger.class);

        ReflectionTestUtils.setField(
                aspect,
                "log",
                logger
        );

        MethodSignature signature =
                mock(MethodSignature.class);

        when(signature.getDeclaringTypeName())
                .thenReturn("com.example.Test");

        when(signature.getName())
                .thenReturn("test");

        invokePrivate(
                aspect,
                "writeLog",
                "WARN",
                signature,
                "warning message"
        );

        verify(logger)
                .warn("warning message");

        verify(appLogService)
                .logAsync(any(LogFormatDTO.class));
    }

    @Test
    void writeLog_shouldExecuteErrorBranch() {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        Logger logger =
                mock(Logger.class);

        ReflectionTestUtils.setField(
                aspect,
                "log",
                logger
        );

        MethodSignature signature =
                mock(MethodSignature.class);

        when(signature.getDeclaringTypeName())
                .thenReturn("com.example.Test");

        when(signature.getName())
                .thenReturn("test");

        invokePrivate(
                aspect,
                "writeLog",
                "ERROR",
                signature,
                "error message"
        );

        verify(logger)
                .error("error message");

        verify(appLogService)
                .logAsync(any(LogFormatDTO.class));
    }

    @Test
    void writeLog_shouldUseInfoForUnknownLevel() {

        LoggingAspect aspect =
                new LoggingAspect(appLogService);

        Logger logger =
                mock(Logger.class);

        ReflectionTestUtils.setField(
                aspect,
                "log",
                logger
        );

        MethodSignature signature =
                mock(MethodSignature.class);

        when(signature.getDeclaringTypeName())
                .thenReturn("com.example.Test");

        when(signature.getName())
                .thenReturn("test");

        invokePrivate(
                aspect,
                "writeLog",
                "UNKNOWN",
                signature,
                "message"
        );

        verify(logger)
                .info("message");

        verify(appLogService)
                .logAsync(any(LogFormatDTO.class));
    }

    private ProceedingJoinPoint mockJoinPoint(
            String action
    ) throws Exception {

        ProceedingJoinPoint joinPoint =
                mock(ProceedingJoinPoint.class);

        MethodSignature signature =
                mock(MethodSignature.class);

        Method realMethod =
                findAnnotatedMethod(action);

        when(signature.getMethod())
                .thenReturn(realMethod);

        when(signature.getDeclaringTypeName())
                .thenReturn(
                        LoggingTestTarget.class.getName()
                );

        when(signature.getName())
                .thenReturn(
                        realMethod.getName()
                );

        when(joinPoint.getSignature())
                .thenReturn(signature);

        return joinPoint;
    }

    private Method findAnnotatedMethod(
            String action
    ) throws NoSuchMethodException {

        return switch (action) {

            case "REGISTER_USER" ->
                    LoggingTestTarget.class.getDeclaredMethod(
                            "register"
                    );

            case "LOGIN" ->
                    LoggingTestTarget.class.getDeclaredMethod(
                            "login"
                    );

            case "ORDER_TOUR" ->
                    LoggingTestTarget.class.getDeclaredMethod(
                            "orderTour"
                    );

            case "DELETE_TOUR" ->
                    LoggingTestTarget.class.getDeclaredMethod(
                            "deleteTour"
                    );

            case "CANCEL_TOUR" ->
                    LoggingTestTarget.class.getDeclaredMethod(
                            "cancelTour"
                    );

            case "TOGGLE_USER_BLOCK_STATUS" ->
                    LoggingTestTarget.class.getDeclaredMethod(
                            "toggleUser"
                    );

            case "DELETE_ACCOUNT" ->
                    LoggingTestTarget.class.getDeclaredMethod(
                            "deleteAccount"
                    );

            case "CHANGE_PASSWORD" ->
                    LoggingTestTarget.class.getDeclaredMethod(
                            "changePassword"
                    );

            case "DEPOSIT_BALANCE_BY_USER" ->
                    LoggingTestTarget.class.getDeclaredMethod(
                            "depositByUser"
                    );

            case "DEPOSIT_BALANCE_BY_ADMIN" ->
                    LoggingTestTarget.class.getDeclaredMethod(
                            "depositByAdmin"
                    );

            case "CREATE_NEW_TOUR" ->
                    LoggingTestTarget.class.getDeclaredMethod(
                            "createTour"
                    );

            case "UPDATE_TOUR_INFO" ->
                    LoggingTestTarget.class.getDeclaredMethod(
                            "updateTour"
                    );

            default ->
                    LoggingTestTarget.class.getDeclaredMethod(
                            "unknown"
                    );
        };
    }

    private LogFormatDTO captureLog() {

        ArgumentCaptor<LogFormatDTO> captor =
                ArgumentCaptor.forClass(
                        LogFormatDTO.class
                );

        verify(appLogService, atLeastOnce())
                .logAsync(captor.capture());

        return captor.getValue();
    }

    private void setAuthenticatedUser(
            String username
    ) {

        Authentication authentication =
                mock(Authentication.class);

        when(authentication.isAuthenticated())
                .thenReturn(true);

        when(authentication.getName())
                .thenReturn(username);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }

    @SuppressWarnings("unchecked")
    private <T> T invokePrivate(
            Object target,
            String methodName,
            Object... args
    ) {

        try {

            Method method =
                    findMethod(
                            target.getClass(),
                            methodName,
                            args.length
                    );

            method.setAccessible(true);

            return (T) method.invoke(
                    target,
                    args
            );

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    private Method findMethod(
            Class<?> type,
            String methodName,
            int parameterCount
    ) {

        Class<?> current = type;

        while (current != null) {

            for (Method method :
                    current.getDeclaredMethods()) {

                if (method.getName().equals(methodName)
                        && method.getParameterCount()
                        == parameterCount) {

                    return method;
                }
            }

            current = current.getSuperclass();
        }

        throw new IllegalArgumentException(
                "Method not found: "
                        + methodName
        );
    }

    static class LoggingTestTarget {

        @Loggable("REGISTER_USER")
        void register() {
        }

        @Loggable("LOGIN")
        void login() {
        }

        @Loggable("ORDER_TOUR")
        void orderTour() {
        }

        @Loggable("DELETE_TOUR")
        void deleteTour() {
        }

        @Loggable("CANCEL_TOUR")
        void cancelTour() {
        }

        @Loggable("TOGGLE_USER_BLOCK_STATUS")
        void toggleUser() {
        }

        @Loggable("DELETE_ACCOUNT")
        void deleteAccount() {
        }

        @Loggable("CHANGE_PASSWORD")
        void changePassword() {
        }

        @Loggable("DEPOSIT_BALANCE_BY_USER")
        void depositByUser() {
        }

        @Loggable("DEPOSIT_BALANCE_BY_ADMIN")
        void depositByAdmin() {
        }

        @Loggable("CREATE_NEW_TOUR")
        void createTour() {
        }

        @Loggable("UPDATE_TOUR_INFO")
        void updateTour() {
        }

        @Loggable("UNKNOWN_ACTION")
        void unknown() {
        }
    }
}

