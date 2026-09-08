package com.epam.finaltask.user;

import com.epam.finaltask.voucher.ChangeVoucherStatusRequestDTO;
import com.epam.finaltask.voucher.VoucherDTO;
import com.epam.finaltask.voucher.VoucherSearchRequestDTO;
import com.epam.finaltask.voucher.VoucherService;
import com.epam.finaltask.voucher.VoucherStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagerControllerTest {

    @Mock
    private VoucherService voucherService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ManagerController controller;

    // ========================================================================
    // HELPERS
    // ========================================================================

    private VoucherDTO voucher() {
        return VoucherDTO.builder()
                .title("Tour")
                .description("Description")
                .price(100.0)
                .tourType("LEISURE")
                .transferType("BUS")
                .hotelType("THREE_STARS")
                .arrivalDate(LocalDate.now().plusDays(1))
                .evictionDate(LocalDate.now().plusDays(2))
                .status(VoucherStatus.REGISTERED)
                .isHot(false)
                .build();
    }

    private Page<VoucherDTO> page() {
        return new PageImpl<>(
                List.of(voucher())
        );
    }

    private VoucherSearchRequestDTO searchRequest() {
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

    // ========================================================================
    // MANAGE VOUCHERS
    // ========================================================================

    @Test
    void manageVouchers_shouldReturnView() {

        Page<VoucherDTO> voucherPage = page();

        VoucherSearchRequestDTO searchRequest =
                searchRequest();

        when(voucherService.findAllVouchersPaged(
                eq(searchRequest),
                eq(0),
                eq(5),
                eq("isHot"),
                eq("desc")
        )).thenReturn(voucherPage);

        String result =
                controller.manageVouchers(
                        searchRequest,
                        "isHot",
                        "desc",
                        0,
                        model
                );

        assertEquals(
                "manager/vouchers",
                result
        );

        verify(voucherService)
                .findAllVouchersPaged(
                        eq(searchRequest),
                        eq(0),
                        eq(5),
                        eq("isHot"),
                        eq("desc")
                );

        verify(model)
                .addAttribute(
                        eq("vouchersPage"),
                        eq(voucherPage)
                );

        verify(model)
                .addAttribute(
                        eq("searchRequest"),
                        eq(searchRequest)
                );

        verify(model)
                .addAttribute(
                        eq("sortField"),
                        eq("isHot")
                );

        verify(model)
                .addAttribute(
                        eq("sortDir"),
                        eq("desc")
                );

        verify(model)
                .addAttribute(
                        eq("currentPage"),
                        eq(0)
                );
    }

    @Test
    void manageVouchers_shouldHandleOutOfRangePage() {

        Page<VoucherDTO> emptyPage =
                new PageImpl<>(List.of());

        Page<VoucherDTO> validPage =
                page();

        VoucherSearchRequestDTO searchRequest =
                searchRequest();

        when(voucherService.findAllVouchersPaged(
                eq(searchRequest),
                eq(3),
                eq(5),
                eq("isHot"),
                eq("desc")
        )).thenReturn(emptyPage);

        when(voucherService.findAllVouchersPaged(
                eq(searchRequest),
                eq(0),
                eq(5),
                eq("isHot"),
                eq("desc")
        )).thenReturn(validPage);

        String result =
                controller.manageVouchers(
                        searchRequest,
                        "isHot",
                        "desc",
                        3,
                        model
                );

        assertEquals(
                "manager/vouchers",
                result
        );

        verify(voucherService)
                .findAllVouchersPaged(
                        eq(searchRequest),
                        eq(3),
                        eq(5),
                        eq("isHot"),
                        eq("desc")
                );

        verify(voucherService)
                .findAllVouchersPaged(
                        eq(searchRequest),
                        eq(0),
                        eq(5),
                        eq("isHot"),
                        eq("desc")
                );

        verify(model)
                .addAttribute(
                        eq("vouchersPage"),
                        eq(validPage)
                );

        verify(model)
                .addAttribute(
                        eq("searchRequest"),
                        eq(searchRequest)
                );

        verify(model)
                .addAttribute(
                        eq("sortField"),
                        eq("isHot")
                );

        verify(model)
                .addAttribute(
                        eq("sortDir"),
                        eq("desc")
                );

        verify(model)
                .addAttribute(
                        eq("currentPage"),
                        eq(0)
                );
    }

    @Test
    void manageVouchers_shouldKeepValidNonZeroPage() {

        Page<VoucherDTO> voucherPage =
                new PageImpl<>(
                        List.of(voucher()),
                        PageRequest.of(1, 5),
                        15
                );

        VoucherSearchRequestDTO searchRequest =
                searchRequest();

        when(voucherService.findAllVouchersPaged(
                eq(searchRequest),
                eq(1),
                eq(5),
                eq("title"),
                eq("asc")
        )).thenReturn(voucherPage);

        String result =
                controller.manageVouchers(
                        searchRequest,
                        "title",
                        "asc",
                        1,
                        model
                );

        assertEquals(
                "manager/vouchers",
                result
        );

        verify(voucherService)
                .findAllVouchersPaged(
                        eq(searchRequest),
                        eq(1),
                        eq(5),
                        eq("title"),
                        eq("asc")
                );

        verify(model)
                .addAttribute(
                        eq("vouchersPage"),
                        eq(voucherPage)
                );

        verify(model)
                .addAttribute(
                        eq("searchRequest"),
                        eq(searchRequest)
                );

        verify(model)
                .addAttribute(
                        eq("sortField"),
                        eq("title")
                );

        verify(model)
                .addAttribute(
                        eq("sortDir"),
                        eq("asc")
                );

        verify(model)
                .addAttribute(
                        eq("currentPage"),
                        eq(1)
                );
    }

    // ========================================================================
    // TOGGLE HOT STATUS - SUCCESS
    // ========================================================================

    @Test
    void toggleHotStatus_shouldHandleSuccessWithNullReferer() {

        when(voucherService.changeHotStatus(
                eq("id"),
                any(VoucherDTO.class)
        )).thenReturn(voucher());

        when(messageSource.getMessage(
                eq("msg.status.hot"),
                isNull(),
                any(Locale.class)
        )).thenReturn(
                "Hot status successfully changed!"
        );

        when(request.getHeader("Referer"))
                .thenReturn(null);

        String result =
                controller.toggleHotStatus(
                        "id",
                        true,
                        redirectAttributes,
                        request
                );

        assertEquals(
                "redirect:/manager/vouchers",
                result
        );

        verify(voucherService)
                .changeHotStatus(
                        eq("id"),
                        any(VoucherDTO.class)
                );

        verify(messageSource)
                .getMessage(
                        eq("msg.status.hot"),
                        isNull(),
                        any(Locale.class)
                );

        verify(redirectAttributes)
                .addFlashAttribute(
                        eq("successMessage"),
                        eq("Hot status successfully changed!")
                );
    }

    @Test
    void toggleHotStatus_shouldUseRefererWhenPresent() {

        when(voucherService.changeHotStatus(
                eq("id"),
                any(VoucherDTO.class)
        )).thenReturn(voucher());

        when(messageSource.getMessage(
                eq("msg.status.hot"),
                isNull(),
                any(Locale.class)
        )).thenReturn(
                "Hot status successfully changed!"
        );

        when(request.getHeader("Referer"))
                .thenReturn(
                        "/manager/vouchers?page=1"
                );

        String result =
                controller.toggleHotStatus(
                        "id",
                        true,
                        redirectAttributes,
                        request
                );

        assertEquals(
                "redirect:/manager/vouchers?page=1",
                result
        );

        verify(redirectAttributes)
                .addFlashAttribute(
                        eq("successMessage"),
                        eq("Hot status successfully changed!")
                );
    }

    @Test
    void toggleHotStatus_shouldSendOppositeHotValue_whenCurrentlyFalse() {

        when(voucherService.changeHotStatus(
                eq("id"),
                any(VoucherDTO.class)
        )).thenReturn(voucher());

        when(messageSource.getMessage(
                eq("msg.status.hot"),
                isNull(),
                any(Locale.class)
        )).thenReturn("success");

        when(request.getHeader("Referer"))
                .thenReturn("/manager/vouchers");

        controller.toggleHotStatus(
                "id",
                false,
                redirectAttributes,
                request
        );

        ArgumentCaptor<VoucherDTO> captor =
                ArgumentCaptor.forClass(
                        VoucherDTO.class
                );

        verify(voucherService)
                .changeHotStatus(
                        eq("id"),
                        captor.capture()
                );

        assertNotNull(
                captor.getValue()
        );

        assertTrue(
                captor.getValue().isHot()
        );
    }

    @Test
    void toggleHotStatus_shouldSendOppositeHotValue_whenCurrentlyTrue() {

        when(voucherService.changeHotStatus(
                eq("id"),
                any(VoucherDTO.class)
        )).thenReturn(voucher());

        when(messageSource.getMessage(
                eq("msg.status.hot"),
                isNull(),
                any(Locale.class)
        )).thenReturn("success");

        when(request.getHeader("Referer"))
                .thenReturn("/manager/vouchers");

        controller.toggleHotStatus(
                "id",
                true,
                redirectAttributes,
                request
        );

        ArgumentCaptor<VoucherDTO> captor =
                ArgumentCaptor.forClass(
                        VoucherDTO.class
                );

        verify(voucherService)
                .changeHotStatus(
                        eq("id"),
                        captor.capture()
                );

        assertNotNull(
                captor.getValue()
        );

        assertFalse(
                captor.getValue().isHot()
        );
    }

    // ========================================================================
    // TOGGLE HOT STATUS - EXCEPTION
    // ========================================================================

    @Test
    void toggleHotStatus_shouldResolveKnownMessageKey() {

        when(voucherService.changeHotStatus(
                eq("id"),
                any(VoucherDTO.class)
        )).thenThrow(
                new IllegalArgumentException(
                        "err.tour.notFound"
                )
        );

        when(messageSource.getMessage(
                eq("err.tour.notFound"),
                isNull(),
                eq("err.tour.notFound"),
                any(Locale.class)
        )).thenReturn(
                "Tour not found"
        );

        when(request.getHeader("Referer"))
                .thenReturn(
                        "/manager/vouchers?page=1"
                );

        String result =
                controller.toggleHotStatus(
                        "id",
                        false,
                        redirectAttributes,
                        request
                );

        assertEquals(
                "redirect:/manager/vouchers?page=1",
                result
        );

        verify(messageSource)
                .getMessage(
                        eq("err.tour.notFound"),
                        isNull(),
                        eq("err.tour.notFound"),
                        any(Locale.class)
                );

        verify(redirectAttributes)
                .addFlashAttribute(
                        eq("errorMessage"),
                        eq("Tour not found")
                );
    }

    @Test
    void toggleHotStatus_shouldUseFallbackForUnknownExceptionMessage() {

        when(voucherService.changeHotStatus(
                eq("id"),
                any(VoucherDTO.class)
        )).thenThrow(
                new RuntimeException(
                        "Something went wrong"
                )
        );

        when(messageSource.getMessage(
                eq("Something went wrong"),
                isNull(),
                eq("Something went wrong"),
                any(Locale.class)
        )).thenReturn(
                "Something went wrong"
        );

        when(request.getHeader("Referer"))
                .thenReturn(
                        "/manager/vouchers?page=1"
                );

        String result =
                controller.toggleHotStatus(
                        "id",
                        false,
                        redirectAttributes,
                        request
                );

        assertEquals(
                "redirect:/manager/vouchers?page=1",
                result
        );

        verify(messageSource)
                .getMessage(
                        eq("Something went wrong"),
                        isNull(),
                        eq("Something went wrong"),
                        any(Locale.class)
                );

        verify(redirectAttributes)
                .addFlashAttribute(
                        eq("errorMessage"),
                        eq("Something went wrong")
                );
    }

    @Test
    void toggleHotStatus_shouldUseUnknownMessageForNullExceptionMessage() {

        when(voucherService.changeHotStatus(
                eq("id"),
                any(VoucherDTO.class)
        )).thenThrow(
                new RuntimeException(
                        (String) null
                )
        );

        when(messageSource.getMessage(
                eq("error.unknown"),
                isNull(),
                any(Locale.class)
        )).thenReturn(
                "An error occurred"
        );

        when(request.getHeader("Referer"))
                .thenReturn(null);

        String result =
                controller.toggleHotStatus(
                        "id",
                        true,
                        redirectAttributes,
                        request
                );

        assertEquals(
                "redirect:/manager/vouchers",
                result
        );

        verify(messageSource)
                .getMessage(
                        eq("error.unknown"),
                        isNull(),
                        any(Locale.class)
                );

        verify(redirectAttributes)
                .addFlashAttribute(
                        eq("errorMessage"),
                        eq("An error occurred")
                );
    }

    @Test
    void toggleHotStatus_shouldUseUnknownMessageForBlankExceptionMessage() {

        when(voucherService.changeHotStatus(
                eq("id"),
                any(VoucherDTO.class)
        )).thenThrow(
                new RuntimeException("   ")
        );

        when(messageSource.getMessage(
                eq("error.unknown"),
                isNull(),
                any(Locale.class)
        )).thenReturn(
                "An error occurred"
        );

        when(request.getHeader("Referer"))
                .thenReturn(null);

        String result =
                controller.toggleHotStatus(
                        "id",
                        true,
                        redirectAttributes,
                        request
                );

        assertEquals(
                "redirect:/manager/vouchers",
                result
        );

        verify(messageSource)
                .getMessage(
                        eq("error.unknown"),
                        isNull(),
                        any(Locale.class)
                );

        verify(redirectAttributes)
                .addFlashAttribute(
                        eq("errorMessage"),
                        eq("An error occurred")
                );
    }

    // ========================================================================
    // UPDATE STATUS - SUCCESS
    // ========================================================================

    @Test
    void updateStatus_shouldHandleSuccessWithNullReferer() {

        when(voucherService.changeStatus(
                eq("id"),
                any(ChangeVoucherStatusRequestDTO.class)
        )).thenReturn(voucher());

        when(messageSource.getMessage(
                eq("msg.status.tour"),
                isNull(),
                any(Locale.class)
        )).thenReturn(
                "Tour status successfully changed!"
        );

        when(request.getHeader("Referer"))
                .thenReturn(null);

        String result =
                controller.updateStatus(
                        "id",
                        VoucherStatus.PAID,
                        redirectAttributes,
                        request
                );

        assertEquals(
                "redirect:/manager/vouchers",
                result
        );

        verify(voucherService)
                .changeStatus(
                        eq("id"),
                        any(ChangeVoucherStatusRequestDTO.class)
                );

        verify(messageSource)
                .getMessage(
                        eq("msg.status.tour"),
                        isNull(),
                        any(Locale.class)
                );

        verify(redirectAttributes)
                .addFlashAttribute(
                        eq("successMessage"),
                        eq("Tour status successfully changed!")
                );
    }

    @Test
    void updateStatus_shouldHandleSuccessWithReferer() {

        when(voucherService.changeStatus(
                eq("id"),
                any(ChangeVoucherStatusRequestDTO.class)
        )).thenReturn(voucher());

        when(messageSource.getMessage(
                eq("msg.status.tour"),
                isNull(),
                any(Locale.class)
        )).thenReturn(
                "Tour status successfully changed!"
        );

        when(request.getHeader("Referer"))
                .thenReturn(
                        "/manager/vouchers?page=2"
                );

        String result =
                controller.updateStatus(
                        "id",
                        VoucherStatus.PAID,
                        redirectAttributes,
                        request
                );

        assertEquals(
                "redirect:/manager/vouchers?page=2",
                result
        );

        verify(redirectAttributes)
                .addFlashAttribute(
                        eq("successMessage"),
                        eq("Tour status successfully changed!")
                );
    }

    @Test
    void updateStatus_shouldPassCorrectStatus() {

        when(voucherService.changeStatus(
                eq("id"),
                any(ChangeVoucherStatusRequestDTO.class)
        )).thenReturn(voucher());

        when(messageSource.getMessage(
                eq("msg.status.tour"),
                isNull(),
                any(Locale.class)
        )).thenReturn("success");

        when(request.getHeader("Referer"))
                .thenReturn(
                        "/manager/vouchers"
                );

        controller.updateStatus(
                "id",
                VoucherStatus.CANCELED,
                redirectAttributes,
                request
        );

        ArgumentCaptor<ChangeVoucherStatusRequestDTO> captor =
                ArgumentCaptor.forClass(
                        ChangeVoucherStatusRequestDTO.class
                );

        verify(voucherService)
                .changeStatus(
                        eq("id"),
                        captor.capture()
                );

        assertEquals(
                VoucherStatus.CANCELED,
                captor.getValue().status()
        );
    }

    // ========================================================================
    // UPDATE STATUS - EXCEPTION
    // ========================================================================

    @Test
    void updateStatus_shouldResolveKnownMessageKey() {

        when(voucherService.changeStatus(
                eq("id"),
                any(ChangeVoucherStatusRequestDTO.class)
        )).thenThrow(
                new IllegalArgumentException(
                        "err.tour.notFound"
                )
        );

        when(messageSource.getMessage(
                eq("err.tour.notFound"),
                isNull(),
                eq("err.tour.notFound"),
                any(Locale.class)
        )).thenReturn(
                "Tour not found"
        );

        when(request.getHeader("Referer"))
                .thenReturn(null);

        String result =
                controller.updateStatus(
                        "id",
                        VoucherStatus.CANCELED,
                        redirectAttributes,
                        request
                );

        assertEquals(
                "redirect:/manager/vouchers",
                result
        );

        verify(messageSource)
                .getMessage(
                        eq("err.tour.notFound"),
                        isNull(),
                        eq("err.tour.notFound"),
                        any(Locale.class)
                );

        verify(redirectAttributes)
                .addFlashAttribute(
                        eq("errorMessage"),
                        eq("Tour not found")
                );
    }

    @Test
    void updateStatus_shouldUseFallbackForUnknownExceptionMessage() {

        when(voucherService.changeStatus(
                eq("id"),
                any(ChangeVoucherStatusRequestDTO.class)
        )).thenThrow(
                new RuntimeException(
                        "Something went wrong"
                )
        );

        when(messageSource.getMessage(
                eq("Something went wrong"),
                isNull(),
                eq("Something went wrong"),
                any(Locale.class)
        )).thenReturn(
                "Something went wrong"
        );

        when(request.getHeader("Referer"))
                .thenReturn(
                        "/manager/vouchers?page=2"
                );

        String result =
                controller.updateStatus(
                        "id",
                        VoucherStatus.PAID,
                        redirectAttributes,
                        request
                );

        assertEquals(
                "redirect:/manager/vouchers?page=2",
                result
        );

        verify(messageSource)
                .getMessage(
                        eq("Something went wrong"),
                        isNull(),
                        eq("Something went wrong"),
                        any(Locale.class)
                );

        verify(redirectAttributes)
                .addFlashAttribute(
                        eq("errorMessage"),
                        eq("Something went wrong")
                );
    }

    @Test
    void updateStatus_shouldUseUnknownMessageForNullExceptionMessage() {

        when(voucherService.changeStatus(
                eq("id"),
                any(ChangeVoucherStatusRequestDTO.class)
        )).thenThrow(
                new RuntimeException(
                        (String) null
                )
        );

        when(messageSource.getMessage(
                eq("error.unknown"),
                isNull(),
                any(Locale.class)
        )).thenReturn(
                "An error occurred"
        );

        when(request.getHeader("Referer"))
                .thenReturn(null);

        String result =
                controller.updateStatus(
                        "id",
                        VoucherStatus.PAID,
                        redirectAttributes,
                        request
                );

        assertEquals(
                "redirect:/manager/vouchers",
                result
        );

        verify(messageSource)
                .getMessage(
                        eq("error.unknown"),
                        isNull(),
                        any(Locale.class)
                );

        verify(redirectAttributes)
                .addFlashAttribute(
                        eq("errorMessage"),
                        eq("An error occurred")
                );
    }

    @Test
    void updateStatus_shouldUseUnknownMessageForBlankExceptionMessage() {

        when(voucherService.changeStatus(
                eq("id"),
                any(ChangeVoucherStatusRequestDTO.class)
        )).thenThrow(
                new RuntimeException("   ")
        );

        when(messageSource.getMessage(
                eq("error.unknown"),
                isNull(),
                any(Locale.class)
        )).thenReturn(
                "An error occurred"
        );

        when(request.getHeader("Referer"))
                .thenReturn(null);

        String result =
                controller.updateStatus(
                        "id",
                        VoucherStatus.PAID,
                        redirectAttributes,
                        request
                );

        assertEquals(
                "redirect:/manager/vouchers",
                result
        );

        verify(messageSource)
                .getMessage(
                        eq("error.unknown"),
                        isNull(),
                        any(Locale.class)
                );

        verify(redirectAttributes)
                .addFlashAttribute(
                        eq("errorMessage"),
                        eq("An error occurred")
                );
    }
}

