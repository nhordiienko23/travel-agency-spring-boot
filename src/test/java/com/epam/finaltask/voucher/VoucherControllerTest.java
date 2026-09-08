package com.epam.finaltask.voucher;

import com.epam.finaltask.core.exception.notFound.ResourceNotFoundException;
import com.epam.finaltask.user.User;
import com.epam.finaltask.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoucherControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VoucherService voucherService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirects;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private VoucherController controller;

    private Principal principal() {
        return () -> "john";
    }

    private User user() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("john")
                .build();
    }

    private VoucherSearchRequestDTO emptySearchRequest() {
        return new VoucherSearchRequestDTO(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    @Test
    void dashboard_shouldReturnView() {
        User user = user();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(voucherService.findAvailableVouchers(
                any(VoucherSearchRequestDTO.class),
                eq(0),
                eq(8),
                eq("isHot"),
                eq("desc")
        )).thenReturn(
                new PageImpl<>(
                        List.of(VoucherDTO.builder().build())
                )
        );

        String result = controller.getDashboardPage(
                emptySearchRequest(),
                "isHot",
                "desc",
                0,
                principal(),
                model
        );

        assertEquals("user/dashboard", result);

        verify(userRepository)
                .findUserByUsername("john");

        verify(voucherService)
                .findAvailableVouchers(
                        any(VoucherSearchRequestDTO.class),
                        eq(0),
                        eq(8),
                        eq("isHot"),
                        eq("desc")
                );

        verify(model).addAttribute(
                "currentUser",
                user
        );

        verify(model).addAttribute(
                "currentPage",
                0
        );
    }

    @Test
    void dashboard_shouldCorrectOutOfRangePage() {
        User user = user();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(voucherService.findAvailableVouchers(
                any(VoucherSearchRequestDTO.class),
                eq(2),
                eq(8),
                eq("isHot"),
                eq("desc")
        )).thenReturn(
                new PageImpl<>(List.of())
        );

        when(voucherService.findAvailableVouchers(
                any(VoucherSearchRequestDTO.class),
                eq(0),
                eq(8),
                eq("isHot"),
                eq("desc")
        )).thenReturn(
                new PageImpl<>(
                        List.of(VoucherDTO.builder().build())
                )
        );

        String result = controller.getDashboardPage(
                emptySearchRequest(),
                "isHot",
                "desc",
                2,
                principal(),
                model
        );

        assertEquals("user/dashboard", result);

        verify(voucherService)
                .findAvailableVouchers(
                        any(VoucherSearchRequestDTO.class),
                        eq(2),
                        eq(8),
                        eq("isHot"),
                        eq("desc")
                );

        verify(voucherService)
                .findAvailableVouchers(
                        any(VoucherSearchRequestDTO.class),
                        eq(0),
                        eq(8),
                        eq("isHot"),
                        eq("desc")
                );

        verify(model).addAttribute(
                "currentPage",
                0
        );
    }

    @Test
    void dashboard_shouldKeepValidNonZeroPage() {
        User user = user();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        Page<VoucherDTO> page =
                new PageImpl<>(
                        List.of(
                                VoucherDTO.builder().build()
                        ),
                        org.springframework.data.domain.PageRequest.of(
                                1,
                                8
                        ),
                        16
                );

        when(voucherService.findAvailableVouchers(
                any(VoucherSearchRequestDTO.class),
                eq(1),
                eq(8),
                eq("price"),
                eq("asc")
        )).thenReturn(page);

        String result = controller.getDashboardPage(
                emptySearchRequest(),
                "price",
                "asc",
                1,
                principal(),
                model
        );

        assertEquals(
                "user/dashboard",
                result
        );

        verify(voucherService)
                .findAvailableVouchers(
                        any(VoucherSearchRequestDTO.class),
                        eq(1),
                        eq(8),
                        eq("price"),
                        eq("asc")
                );

        verify(model)
                .addAttribute(
                        "currentPage",
                        1
                );
    }

    @Test
    void dashboard_shouldHandleZeroTotalPages() {
        User user = user();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(voucherService.findAvailableVouchers(
                any(VoucherSearchRequestDTO.class),
                eq(3),
                eq(8),
                eq("price"),
                eq("asc")
        )).thenReturn(
                new PageImpl<>(List.of())
        );

        when(voucherService.findAvailableVouchers(
                any(VoucherSearchRequestDTO.class),
                eq(0),
                eq(8),
                eq("price"),
                eq("asc")
        )).thenReturn(
                new PageImpl<>(List.of())
        );

        String result = controller.getDashboardPage(
                emptySearchRequest(),
                "price",
                "asc",
                3,
                principal(),
                model
        );

        assertEquals(
                "user/dashboard",
                result
        );

        verify(voucherService)
                .findAvailableVouchers(
                        any(VoucherSearchRequestDTO.class),
                        eq(3),
                        eq(8),
                        eq("price"),
                        eq("asc")
                );

        verify(voucherService)
                .findAvailableVouchers(
                        any(VoucherSearchRequestDTO.class),
                        eq(0),
                        eq(8),
                        eq("price"),
                        eq("asc")
                );

        verify(model)
                .addAttribute(
                        "currentPage",
                        0
                );
    }

    @Test
    void dashboard_shouldThrowWhenUserNotFound() {
        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> controller.getDashboardPage(
                        emptySearchRequest(),
                        "isHot",
                        "desc",
                        0,
                        principal(),
                        model
                )
        );

        verify(userRepository)
                .findUserByUsername("john");

        verifyNoInteractions(voucherService);
        verifyNoInteractions(model);
    }

    @Test
    void orderVoucher_shouldHandleSuccessAndFallback() {
        User user = user();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(voucherService.order(
                "voucher-id",
                user.getId().toString()
        )).thenReturn(
                VoucherDTO.builder().build()
        );

        when(messageSource.getMessage(
                anyString(),
                isNull(),
                anyString(),
                any()
        )).thenReturn("success");

        when(request.getHeader("Referer"))
                .thenReturn(null);

        String result = controller.orderVoucher(
                "voucher-id",
                principal(),
                redirects,
                request
        );

        assertEquals(
                "redirect:/dashboard",
                result
        );

        verify(voucherService)
                .order(
                        "voucher-id",
                        user.getId().toString()
                );

        verify(redirects)
                .addFlashAttribute(
                        eq("successMessage"),
                        eq("success")
                );

        verify(messageSource)
                .getMessage(
                        eq("msg.tour.ordered"),
                        isNull(),
                        anyString(),
                        any()
                );

        verify(request)
                .getHeader("Referer");
    }

    @Test
    void orderVoucher_shouldRedirectToRefererOnSuccess() {
        User user = user();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(voucherService.order(
                "voucher-id",
                user.getId().toString()
        )).thenReturn(
                VoucherDTO.builder().build()
        );

        when(messageSource.getMessage(
                anyString(),
                isNull(),
                anyString(),
                any()
        )).thenReturn("success");

        when(request.getHeader("Referer"))
                .thenReturn("/dashboard?page=1");

        String result = controller.orderVoucher(
                "voucher-id",
                principal(),
                redirects,
                request
        );

        assertEquals(
                "redirect:/dashboard?page=1",
                result
        );

        verify(voucherService)
                .order(
                        "voucher-id",
                        user.getId().toString()
                );

        verify(redirects)
                .addFlashAttribute(
                        eq("successMessage"),
                        eq("success")
                );
    }

    @Test
    void orderVoucher_shouldHandleException() {
        User user = user();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(voucherService.order(
                "voucher-id",
                user.getId().toString()
        )).thenThrow(
                new IllegalArgumentException("err.tour.funds")
        );

        when(messageSource.getMessage(
                anyString(),
                isNull(),
                anyString(),
                any()
        )).thenReturn("error message");

        when(request.getHeader("Referer"))
                .thenReturn("/dashboard");

        String result = controller.orderVoucher(
                "voucher-id",
                principal(),
                redirects,
                request
        );

        assertEquals(
                "redirect:/dashboard",
                result
        );

        verify(redirects)
                .addFlashAttribute(
                        eq("errorMessage"),
                        eq("error message")
                );

        verify(messageSource)
                .getMessage(
                        eq("err.tour.funds"),
                        isNull(),
                        eq("err.tour.funds"),
                        any()
                );
    }

    @Test
    void orderVoucher_shouldHandleNullExceptionMessage() {
        User user = user();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(voucherService.order(
                "voucher-id",
                user.getId().toString()
        )).thenThrow(
                new IllegalArgumentException()
        );

        when(messageSource.getMessage(
                anyString(),
                isNull(),
                anyString(),
                any()
        )).thenReturn("unknown error");

        when(request.getHeader("Referer"))
                .thenReturn("/dashboard");

        String result = controller.orderVoucher(
                "voucher-id",
                principal(),
                redirects,
                request
        );

        assertEquals(
                "redirect:/dashboard",
                result
        );

        verify(redirects)
                .addFlashAttribute(
                        eq("errorMessage"),
                        eq("unknown error")
                );

        verify(messageSource)
                .getMessage(
                        eq("error.unknown"),
                        isNull(),
                        eq("error.unknown"),
                        any()
                );
    }

    @Test
    void orderVoucher_shouldHandleBlankExceptionMessage() {
        User user = user();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(voucherService.order(
                "voucher-id",
                user.getId().toString()
        )).thenThrow(
                new IllegalArgumentException("   ")
        );

        when(messageSource.getMessage(
                anyString(),
                isNull(),
                anyString(),
                any()
        )).thenReturn("unknown error");

        when(request.getHeader("Referer"))
                .thenReturn(null);

        String result = controller.orderVoucher(
                "voucher-id",
                principal(),
                redirects,
                request
        );

        assertEquals(
                "redirect:/dashboard",
                result
        );

        verify(redirects)
                .addFlashAttribute(
                        eq("errorMessage"),
                        eq("unknown error")
                );

        verify(messageSource)
                .getMessage(
                        eq("error.unknown"),
                        isNull(),
                        eq("error.unknown"),
                        any()
                );
    }

    @Test
    void orderVoucher_shouldThrowWhenUserNotFound() {
        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> controller.orderVoucher(
                        "voucher-id",
                        principal(),
                        redirects,
                        request
                )
        );

        verify(userRepository)
                .findUserByUsername("john");

        verifyNoInteractions(voucherService);
        verifyNoInteractions(messageSource);
        verifyNoInteractions(redirects);
        verifyNoInteractions(request);
    }

    @Test
    void cancelVoucher_shouldHandleSuccessAndFallback() {
        when(messageSource.getMessage(
                anyString(),
                isNull(),
                anyString(),
                any()
        )).thenReturn("success");

        when(request.getHeader("Referer"))
                .thenReturn(null);

        String result = controller.cancelVoucher(
                "voucher-id",
                principal(),
                redirects,
                request
        );

        assertEquals(
                "redirect:/dashboard",
                result
        );

        verify(voucherService)
                .cancelOrder(
                        "voucher-id",
                        "john"
                );

        verify(redirects)
                .addFlashAttribute(
                        eq("successMessage"),
                        eq("success")
                );

        verify(messageSource)
                .getMessage(
                        eq("msg.tour.canceled"),
                        isNull(),
                        eq("msg.tour.canceled"),
                        any()
                );

        verify(request)
                .getHeader("Referer");
    }

    @Test
    void cancelVoucher_shouldRedirectToRefererOnSuccess() {
        when(messageSource.getMessage(
                anyString(),
                isNull(),
                anyString(),
                any()
        )).thenReturn("success");

        when(request.getHeader("Referer"))
                .thenReturn("/dashboard?page=2");

        String result = controller.cancelVoucher(
                "voucher-id",
                principal(),
                redirects,
                request
        );

        assertEquals(
                "redirect:/dashboard?page=2",
                result
        );

        verify(voucherService)
                .cancelOrder(
                        "voucher-id",
                        "john"
                );

        verify(redirects)
                .addFlashAttribute(
                        eq("successMessage"),
                        eq("success")
                );
    }

    @Test
    void cancelVoucher_shouldHandleException() {
        doThrow(
                new IllegalArgumentException("err.tour.cancel.own")
        ).when(voucherService)
                .cancelOrder(
                        "voucher-id",
                        "john"
                );

        when(messageSource.getMessage(
                anyString(),
                isNull(),
                anyString(),
                any()
        )).thenReturn("error message");

        when(request.getHeader("Referer"))
                .thenReturn("/dashboard");

        String result = controller.cancelVoucher(
                "voucher-id",
                principal(),
                redirects,
                request
        );

        assertEquals(
                "redirect:/dashboard",
                result
        );

        verify(redirects)
                .addFlashAttribute(
                        eq("errorMessage"),
                        eq("error message")
                );

        verify(messageSource)
                .getMessage(
                        eq("err.tour.cancel.own"),
                        isNull(),
                        eq("err.tour.cancel.own"),
                        any()
                );
    }

    @Test
    void cancelVoucher_shouldHandleNullExceptionMessage() {
        doThrow(
                new IllegalArgumentException()
        ).when(voucherService)
                .cancelOrder(
                        "voucher-id",
                        "john"
                );

        when(messageSource.getMessage(
                anyString(),
                isNull(),
                anyString(),
                any()
        )).thenReturn("unknown error");

        when(request.getHeader("Referer"))
                .thenReturn(null);

        String result = controller.cancelVoucher(
                "voucher-id",
                principal(),
                redirects,
                request
        );

        assertEquals(
                "redirect:/dashboard",
                result
        );

        verify(redirects)
                .addFlashAttribute(
                        eq("errorMessage"),
                        eq("unknown error")
                );

        verify(messageSource)
                .getMessage(
                        eq("error.unknown"),
                        isNull(),
                        eq("error.unknown"),
                        any()
                );
    }

    @Test
    void cancelVoucher_shouldHandleBlankExceptionMessage() {
        doThrow(
                new IllegalArgumentException("   ")
        ).when(voucherService)
                .cancelOrder(
                        "voucher-id",
                        "john"
                );

        when(messageSource.getMessage(
                anyString(),
                isNull(),
                anyString(),
                any()
        )).thenReturn("unknown error");

        when(request.getHeader("Referer"))
                .thenReturn(null);

        String result = controller.cancelVoucher(
                "voucher-id",
                principal(),
                redirects,
                request
        );

        assertEquals(
                "redirect:/dashboard",
                result
        );

        verify(redirects)
                .addFlashAttribute(
                        eq("errorMessage"),
                        eq("unknown error")
                );

        verify(messageSource)
                .getMessage(
                        eq("error.unknown"),
                        isNull(),
                        eq("error.unknown"),
                        any()
                );
    }
}

