package com.epam.finaltask.user;

import com.epam.finaltask.voucher.CreateVoucherRequestDTO;
import com.epam.finaltask.voucher.UpdateVoucherRequestDTO;
import com.epam.finaltask.voucher.VoucherDTO;
import com.epam.finaltask.voucher.VoucherSearchRequestDTO;
import com.epam.finaltask.voucher.VoucherService;
import com.epam.finaltask.voucher.VoucherStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private VoucherService voucherService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AdminController controller;

    // ========================================================================
    // HELPERS
    // ========================================================================

    private VoucherDTO voucher() {

        return VoucherDTO.builder()
                .id(UUID.randomUUID().toString())
                .title("Tour")
                .description("Description")
                .price(100.0)
                .tourType("LEISURE")
                .transferType("BUS")
                .hotelType("THREE_STARS")
                .arrivalDate(LocalDate.now().plusDays(5))
                .evictionDate(LocalDate.now().plusDays(10))
                .status(VoucherStatus.REGISTERED)
                .isHot(false)
                .build();
    }

    private CreateVoucherRequestDTO validCreateRequest(
            LocalDate arrival,
            LocalDate eviction
    ) {

        return CreateVoucherRequestDTO.builder()
                .title("Tour")
                .description("Description")
                .price(100.0)
                .tourType("LEISURE")
                .transferType("BUS")
                .hotelType("THREE_STARS")
                .arrivalDate(arrival)
                .evictionDate(eviction)
                .build();
    }

    private UpdateVoucherRequestDTO validUpdateRequest(
            LocalDate arrival,
            LocalDate eviction
    ) {

        return UpdateVoucherRequestDTO.builder()
                .title("Tour")
                .description("Description")
                .price(100.0)
                .tourType("LEISURE")
                .transferType("BUS")
                .hotelType("THREE_STARS")
                .arrivalDate(arrival)
                .evictionDate(eviction)
                .isHot(false)
                .status(VoucherStatus.REGISTERED)
                .build();
    }

    private Page<UserResponseDTO> userPage() {

        UserResponseDTO user =
                UserResponseDTO.builder()
                        .id(UUID.randomUUID().toString())
                        .username("john")
                        .email("john@test.com")
                        .lastName("Smith")
                        .phoneNumber("+380123456789")
                        .balance(500.0)
                        .role("USER")
                        .active(true)
                        .build();

        return new PageImpl<>(
                List.of(user)
        );
    }

    private Page<VoucherDTO> voucherPage() {

        return new PageImpl<>(
                List.of(voucher())
        );
    }

    private AdminDepositBalanceRequestDTO depositRequest() {

        return AdminDepositBalanceRequestDTO.builder()
                .userId(UUID.randomUUID())
                .amount(100.0)
                .build();
    }

    private UserResponseDTO userResponse() {

        return UserResponseDTO.builder()
                .id(UUID.randomUUID().toString())
                .username("john")
                .email("john@test.com")
                .lastName("Smith")
                .phoneNumber("+380123456789")
                .balance(500.0)
                .role("USER")
                .active(true)
                .build();
    }

    private UserSearchRequestDTO emptyUserSearchRequest() {

        return new UserSearchRequestDTO(
                null,
                null,
                null,
                null
        );
    }

    private VoucherSearchRequestDTO emptyVoucherSearchRequest() {

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
    // MANAGE USERS
    // ========================================================================

    @Test
    void manageUsers_shouldReturnUsersView() {

        Page<UserResponseDTO> page =
                userPage();

        UserSearchRequestDTO searchRequest =
                emptyUserSearchRequest();

        when(userService.findUsers(
                searchRequest,
                0,
                5,
                "username",
                "asc"
        )).thenReturn(page);

        String result =
                controller.manageUsers(
                        searchRequest,
                        0,
                        "username",
                        "asc",
                        model
                );

        assertEquals(
                "admin/users",
                result
        );

        verify(userService)
                .findUsers(
                        searchRequest,
                        0,
                        5,
                        "username",
                        "asc"
                );

        verify(model)
                .addAttribute(
                        "userPage",
                        page
                );

        verify(model)
                .addAttribute(
                        "currentPage",
                        0
                );

        verify(model)
                .addAttribute(
                        "totalPages",
                        page.getTotalPages()
                );

        verify(model)
                .addAttribute(
                        "sortBy",
                        "username"
                );

        verify(model)
                .addAttribute(
                        "sortDir",
                        "asc"
                );
    }

    @Test
    void manageUsers_shouldNormalizeInvalidSortDirectionToAsc() {

        Page<UserResponseDTO> page =
                userPage();

        UserSearchRequestDTO searchRequest =
                emptyUserSearchRequest();

        when(userService.findUsers(
                searchRequest,
                0,
                5,
                "username",
                "asc"
        )).thenReturn(page);

        String result =
                controller.manageUsers(
                        searchRequest,
                        0,
                        "username",
                        "something",
                        model
                );

        assertEquals(
                "admin/users",
                result
        );

        verify(userService)
                .findUsers(
                        searchRequest,
                        0,
                        5,
                        "username",
                        "asc"
                );

        verify(model)
                .addAttribute(
                        "sortBy",
                        "username"
                );

        verify(model)
                .addAttribute(
                        "sortDir",
                        "asc"
                );
    }

    @Test
    void manageUsers_shouldUseDescSorting() {

        Page<UserResponseDTO> page =
                userPage();

        UserSearchRequestDTO searchRequest =
                emptyUserSearchRequest();

        when(userService.findUsers(
                searchRequest,
                0,
                5,
                "username",
                "desc"
        )).thenReturn(page);

        String result =
                controller.manageUsers(
                        searchRequest,
                        0,
                        "username",
                        "desc",
                        model
                );

        assertEquals(
                "admin/users",
                result
        );

        verify(userService)
                .findUsers(
                        searchRequest,
                        0,
                        5,
                        "username",
                        "desc"
                );

        verify(model)
                .addAttribute(
                        "sortBy",
                        "username"
                );

        verify(model)
                .addAttribute(
                        "sortDir",
                        "desc"
                );
    }

    @Test
    void manageUsers_shouldCorrectOutOfRangePage() {

        Page<UserResponseDTO> emptyPage =
                new PageImpl<>(
                        List.of()
                );

        Page<UserResponseDTO> validPage =
                userPage();

        UserSearchRequestDTO searchRequest =
                emptyUserSearchRequest();

        when(userService.findUsers(
                searchRequest,
                3,
                5,
                "username",
                "asc"
        )).thenReturn(emptyPage);

        when(userService.findUsers(
                searchRequest,
                0,
                5,
                "username",
                "asc"
        )).thenReturn(validPage);

        String result =
                controller.manageUsers(
                        searchRequest,
                        3,
                        "username",
                        "asc",
                        model
                );

        assertEquals(
                "admin/users",
                result
        );

        verify(userService)
                .findUsers(
                        searchRequest,
                        3,
                        5,
                        "username",
                        "asc"
                );

        verify(userService)
                .findUsers(
                        searchRequest,
                        0,
                        5,
                        "username",
                        "asc"
                );

        verify(model)
                .addAttribute(
                        "currentPage",
                        0
                );

        verify(model)
                .addAttribute(
                        "userPage",
                        validPage
                );

        verify(model)
                .addAttribute(
                        "totalPages",
                        validPage.getTotalPages()
                );

        verify(model)
                .addAttribute(
                        "sortBy",
                        "username"
                );

        verify(model)
                .addAttribute(
                        "sortDir",
                        "asc"
                );
    }

    @Test
    void manageUsers_shouldKeepValidNonZeroPage() {

        UserResponseDTO user =
                userResponse();

        Page<UserResponseDTO> page =
                new PageImpl<>(
                        List.of(user),
                        PageRequest.of(1, 5),
                        15
                );

        UserSearchRequestDTO searchRequest =
                emptyUserSearchRequest();

        when(userService.findUsers(
                searchRequest,
                1,
                5,
                "username",
                "desc"
        )).thenReturn(page);

        String result =
                controller.manageUsers(
                        searchRequest,
                        1,
                        "username",
                        "desc",
                        model
                );

        assertEquals(
                "admin/users",
                result
        );

        verify(userService)
                .findUsers(
                        searchRequest,
                        1,
                        5,
                        "username",
                        "desc"
                );

        verify(model)
                .addAttribute(
                        "currentPage",
                        1
                );

        verify(model)
                .addAttribute(
                        "totalPages",
                        page.getTotalPages()
                );

        verify(model)
                .addAttribute(
                        "sortBy",
                        "username"
                );

        verify(model)
                .addAttribute(
                        "sortDir",
                        "desc"
                );
    }

    @Test
    void manageUsers_shouldForceUsernameSortWhenAnotherFieldIsProvided() {

        Page<UserResponseDTO> page =
                userPage();

        UserSearchRequestDTO searchRequest =
                emptyUserSearchRequest();

        when(userService.findUsers(
                searchRequest,
                0,
                5,
                "username",
                "asc"
        )).thenReturn(page);

        String result =
                controller.manageUsers(
                        searchRequest,
                        0,
                        "email",
                        "asc",
                        model
                );

        assertEquals(
                "admin/users",
                result
        );

        verify(userService)
                .findUsers(
                        searchRequest,
                        0,
                        5,
                        "username",
                        "asc"
                );

        verify(model)
                .addAttribute(
                        "sortBy",
                        "username"
                );
    }

    // ========================================================================
    // TOGGLE USER STATUS
    // ========================================================================

    @Test
    void toggleUserStatus_shouldUseFallbackWhenRefererIsNull() {

        when(request.getHeader("Referer"))
                .thenReturn(null);

        String result =
                controller.toggleUserStatus(
                        "john",
                        request
                );

        assertEquals(
                "redirect:/admin/users",
                result
        );

        verify(userService)
                .toggleUserStatus("john");
    }

    @Test
    void toggleUserStatus_shouldUseRefererWhenPresent() {

        when(request.getHeader("Referer"))
                .thenReturn(
                        "/admin/users?page=2"
                );

        String result =
                controller.toggleUserStatus(
                        "john",
                        request
                );

        assertEquals(
                "redirect:/admin/users?page=2",
                result
        );

        verify(userService)
                .toggleUserStatus("john");
    }

    // ========================================================================
    // SHOW DEPOSIT FORM
    // ========================================================================

    @Test
    void showDepositForm_shouldAddUserAndRequest() {

        UUID userId =
                UUID.randomUUID();

        UserResponseDTO user =
                UserResponseDTO.builder()
                        .id(userId.toString())
                        .username("john")
                        .email("john@test.com")
                        .lastName("Smith")
                        .phoneNumber("+380123456789")
                        .balance(500.0)
                        .role("USER")
                        .active(true)
                        .build();

        when(userService.getUserById(userId))
                .thenReturn(user);

        when(model.containsAttribute(
                "depositRequest"
        )).thenReturn(false);

        String result =
                controller.showDepositForm(
                        userId.toString(),
                        model
                );

        assertEquals(
                "admin/deposit",
                result
        );

        verify(userService)
                .getUserById(userId);

        verify(model)
                .addAttribute(
                        "user",
                        user
                );

        verify(model)
                .addAttribute(
                        eq("depositRequest"),
                        any(AdminDepositBalanceRequestDTO.class)
                );
    }

    @Test
    void showDepositForm_shouldKeepExistingRequest() {

        UUID userId =
                UUID.randomUUID();

        UserResponseDTO user =
                userResponse();

        when(userService.getUserById(userId))
                .thenReturn(user);

        when(model.containsAttribute(
                "depositRequest"
        )).thenReturn(true);

        String result =
                controller.showDepositForm(
                        userId.toString(),
                        model
                );

        assertEquals(
                "admin/deposit",
                result
        );

        verify(userService)
                .getUserById(userId);

        verify(model)
                .addAttribute(
                        "user",
                        user
                );

        verify(model, never())
                .addAttribute(
                        eq("depositRequest"),
                        any()
                );
    }

    // ========================================================================
    // DEPOSIT BALANCE
    // ========================================================================

    @Test
    void depositBalance_shouldUseFallbackWhenRefererIsNull() {

        AdminDepositBalanceRequestDTO requestDto =
                depositRequest();

        when(bindingResult.hasErrors())
                .thenReturn(false);

        when(request.getHeader("Referer"))
                .thenReturn(null);

        when(messageSource.getMessage(
                eq("msg.user.balance.adminToppedUp"),
                isNull(),
                any(Locale.class)
        )).thenReturn(
                "User balance topped up successfully"
        );

        String result =
                controller.depositBalance(
                        requestDto,
                        bindingResult,
                        request,
                        model,
                        redirectAttributes
                );

        assertEquals(
                "redirect:/admin/users",
                result
        );

        verify(userService)
                .depositBalanceByAdmin(
                        requestDto
                );

        verify(messageSource)
                .getMessage(
                        eq("msg.user.balance.adminToppedUp"),
                        isNull(),
                        any(Locale.class)
                );

        verify(redirectAttributes)
                .addFlashAttribute(
                        "successMessage",
                        "User balance topped up successfully"
                );
    }

    @Test
    void depositBalance_shouldUseRefererWhenPresent() {

        AdminDepositBalanceRequestDTO requestDto =
                depositRequest();

        when(bindingResult.hasErrors())
                .thenReturn(false);

        when(request.getHeader("Referer"))
                .thenReturn(
                        "/admin/users?page=4"
                );

        when(messageSource.getMessage(
                eq("msg.user.balance.adminToppedUp"),
                isNull(),
                any(Locale.class)
        )).thenReturn(
                "User balance topped up successfully"
        );

        String result =
                controller.depositBalance(
                        requestDto,
                        bindingResult,
                        request,
                        model,
                        redirectAttributes
                );

        assertEquals(
                "redirect:/admin/users?page=4",
                result
        );

        verify(userService)
                .depositBalanceByAdmin(
                        requestDto
                );

        verify(messageSource)
                .getMessage(
                        eq("msg.user.balance.adminToppedUp"),
                        isNull(),
                        any(Locale.class)
                );

        verify(redirectAttributes)
                .addFlashAttribute(
                        "successMessage",
                        "User balance topped up successfully"
                );
    }

    @Test
    void depositBalance_shouldReturnFormWhenBindingHasErrors() {

        AdminDepositBalanceRequestDTO requestDto =
                depositRequest();

        when(bindingResult.hasErrors())
                .thenReturn(true);

        UserResponseDTO user =
                userResponse();

        when(userService.getUserById(
                requestDto.userId()
        )).thenReturn(user);

        String result =
                controller.depositBalance(
                        requestDto,
                        bindingResult,
                        request,
                        model,
                        redirectAttributes
                );

        assertEquals(
                "admin/deposit",
                result
        );

        verify(userService)
                .getUserById(
                        requestDto.userId()
                );

        verify(model)
                .addAttribute(
                        "user",
                        user
                );

        verify(userService, never())
                .depositBalanceByAdmin(any());

        verifyNoInteractions(
                redirectAttributes
        );

        verifyNoInteractions(
                messageSource
        );
    }

    // ========================================================================
    // MANAGE VOUCHERS
    // ========================================================================

    @Test
    void manageVouchers_shouldReturnVouchersView() {

        Page<VoucherDTO> page =
                voucherPage();

        VoucherSearchRequestDTO searchRequest =
                emptyVoucherSearchRequest();

        when(voucherService.findAllVouchersPaged(
                searchRequest,
                0,
                5,
                "isHot",
                "desc"
        )).thenReturn(page);

        String result =
                controller.manageVouchers(
                        searchRequest,
                        "isHot",
                        "desc",
                        0,
                        model
                );

        assertEquals(
                "admin/vouchers",
                result
        );

        verify(voucherService)
                .findAllVouchersPaged(
                        searchRequest,
                        0,
                        5,
                        "isHot",
                        "desc"
                );

        verify(model)
                .addAttribute(
                        "vouchersPage",
                        page
                );

        verify(model)
                .addAttribute(
                        "currentPage",
                        0
                );
    }

    @Test
    void manageVouchers_shouldCorrectOutOfRangePage() {

        Page<VoucherDTO> emptyPage =
                new PageImpl<>(
                        List.of()
                );

        Page<VoucherDTO> validPage =
                voucherPage();

        VoucherSearchRequestDTO searchRequest =
                emptyVoucherSearchRequest();

        when(voucherService.findAllVouchersPaged(
                searchRequest,
                4,
                5,
                "isHot",
                "desc"
        )).thenReturn(emptyPage);

        when(voucherService.findAllVouchersPaged(
                searchRequest,
                0,
                5,
                "isHot",
                "desc"
        )).thenReturn(validPage);

        String result =
                controller.manageVouchers(
                        searchRequest,
                        "isHot",
                        "desc",
                        4,
                        model
                );

        assertEquals(
                "admin/vouchers",
                result
        );

        verify(voucherService)
                .findAllVouchersPaged(
                        searchRequest,
                        4,
                        5,
                        "isHot",
                        "desc"
                );

        verify(voucherService)
                .findAllVouchersPaged(
                        searchRequest,
                        0,
                        5,
                        "isHot",
                        "desc"
                );

        verify(model)
                .addAttribute(
                        "currentPage",
                        0
                );
    }

    @Test
    void manageVouchers_shouldKeepValidNonZeroPage() {

        Page<VoucherDTO> page =
                new PageImpl<>(
                        List.of(voucher()),
                        PageRequest.of(1, 5),
                        15
                );

        VoucherSearchRequestDTO searchRequest =
                emptyVoucherSearchRequest();

        when(voucherService.findAllVouchersPaged(
                searchRequest,
                1,
                5,
                "title",
                "asc"
        )).thenReturn(page);

        String result =
                controller.manageVouchers(
                        searchRequest,
                        "title",
                        "asc",
                        1,
                        model
                );

        assertEquals(
                "admin/vouchers",
                result
        );

        verify(voucherService)
                .findAllVouchersPaged(
                        searchRequest,
                        1,
                        5,
                        "title",
                        "asc"
                );

        verify(model)
                .addAttribute(
                        "currentPage",
                        1
                );
    }

    // ========================================================================
    // CREATE FORM
    // ========================================================================

    @Test
    void showCreateForm_shouldAddRequestWhenMissing() {

        when(model.containsAttribute(
                "createRequest"
        )).thenReturn(false);

        String result =
                controller.showCreateForm(model);

        assertEquals(
                "admin/voucher-form",
                result
        );

        verify(model)
                .addAttribute(
                        eq("createRequest"),
                        any(CreateVoucherRequestDTO.class)
                );
    }

    @Test
    void showCreateForm_shouldKeepExistingRequest() {

        when(model.containsAttribute(
                "createRequest"
        )).thenReturn(true);

        String result =
                controller.showCreateForm(model);

        assertEquals(
                "admin/voucher-form",
                result
        );

        verify(model, never())
                .addAttribute(
                        eq("createRequest"),
                        any()
                );
    }

    // ========================================================================
    // CREATE VOUCHER
    // ========================================================================

    @Test
    void processCreateForm_shouldReturnFormWhenBindingHasErrors() {

        CreateVoucherRequestDTO requestDto =
                validCreateRequest(
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(2)
                );

        when(bindingResult.hasErrors())
                .thenReturn(true);

        String result =
                controller.processCreateForm(
                        requestDto,
                        bindingResult,
                        redirectAttributes
                );

        assertEquals(
                "admin/voucher-form",
                result
        );

        verify(voucherService, never())
                .create(any());
    }

    @Test
    void processCreateForm_shouldCreateAndRedirectOnSuccess() {

        CreateVoucherRequestDTO requestDto =
                validCreateRequest(
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(2)
                );

        when(bindingResult.hasErrors())
                .thenReturn(false);

        when(messageSource.getMessage(
                eq("msg.tour.created"),
                isNull(),
                any(Locale.class)
        )).thenReturn(
                "Tour created"
        );

        String result =
                controller.processCreateForm(
                        requestDto,
                        bindingResult,
                        redirectAttributes
                );

        assertEquals(
                "redirect:/admin/vouchers",
                result
        );

        verify(voucherService)
                .create(requestDto);

        verify(redirectAttributes)
                .addFlashAttribute(
                        "successMessage",
                        "Tour created"
                );
    }

    @Test
    void processCreateForm_shouldDetectArrivalYearError() {

        CreateVoucherRequestDTO requestDto =
                validCreateRequest(
                        LocalDate.of(2101, 1, 1),
                        LocalDate.of(2101, 2, 1)
                );

        when(bindingResult.hasErrors())
                .thenReturn(true);

        when(messageSource.getMessage(
                eq("err.date.year"),
                isNull(),
                any(Locale.class)
        )).thenReturn(
                "Year must not exceed 2100."
        );

        String result =
                controller.processCreateForm(
                        requestDto,
                        bindingResult,
                        redirectAttributes
                );

        assertEquals(
                "admin/voucher-form",
                result
        );

        verify(messageSource, times(2))
                .getMessage(
                        eq("err.date.year"),
                        isNull(),
                        any(Locale.class)
                );

        verify(bindingResult)
                .rejectValue(
                        eq("arrivalDate"),
                        eq("error.arrivalDate"),
                        eq("Year must not exceed 2100.")
                );

        verify(voucherService, never())
                .create(any());
    }

    @Test
    void processCreateForm_shouldDetectEvictionYearError() {

        CreateVoucherRequestDTO requestDto =
                validCreateRequest(
                        LocalDate.of(2100, 1, 1),
                        LocalDate.of(2101, 2, 1)
                );

        when(bindingResult.hasErrors())
                .thenReturn(true);

        when(messageSource.getMessage(
                eq("err.date.year"),
                isNull(),
                any(Locale.class)
        )).thenReturn(
                "Year must not exceed 2100."
        );

        String result =
                controller.processCreateForm(
                        requestDto,
                        bindingResult,
                        redirectAttributes
                );

        assertEquals(
                "admin/voucher-form",
                result
        );

        verify(messageSource)
                .getMessage(
                        eq("err.date.year"),
                        isNull(),
                        any(Locale.class)
                );

        verify(bindingResult)
                .rejectValue(
                        eq("evictionDate"),
                        eq("error.evictionDate"),
                        eq("Year must not exceed 2100.")
                );

        verify(voucherService, never())
                .create(any());
    }

    @Test
    void processCreateForm_shouldDetectWrongDateOrder() {

        CreateVoucherRequestDTO requestDto =
                validCreateRequest(
                        LocalDate.of(2026, 5, 10),
                        LocalDate.of(2026, 5, 1)
                );

        when(bindingResult.hasErrors())
                .thenReturn(true);

        when(messageSource.getMessage(
                eq("err.date.order"),
                isNull(),
                any(Locale.class)
        )).thenReturn(
                "Eviction date must be equal to or after the arrival date."
        );

        String result =
                controller.processCreateForm(
                        requestDto,
                        bindingResult,
                        redirectAttributes
                );

        assertEquals(
                "admin/voucher-form",
                result
        );

        verify(messageSource)
                .getMessage(
                        eq("err.date.order"),
                        isNull(),
                        any(Locale.class)
                );

        verify(bindingResult)
                .rejectValue(
                        eq("evictionDate"),
                        eq("error.evictionDate"),
                        eq(
                                "Eviction date must be equal to or after the arrival date."
                        )
                );

        verify(voucherService, never())
                .create(any());
    }

    @Test
    void processCreateForm_shouldHandleNullArrivalDate() {

        CreateVoucherRequestDTO requestDto =
                validCreateRequest(
                        null,
                        LocalDate.of(2026, 5, 10)
                );

        when(bindingResult.hasErrors())
                .thenReturn(true);

        String result =
                controller.processCreateForm(
                        requestDto,
                        bindingResult,
                        redirectAttributes
                );

        assertEquals(
                "admin/voucher-form",
                result
        );

        verify(bindingResult, never())
                .rejectValue(
                        anyString(),
                        anyString(),
                        anyString()
                );

        verify(voucherService, never())
                .create(any());

        verifyNoInteractions(
                messageSource
        );
    }

    @Test
    void processCreateForm_shouldHandleNullEvictionDate() {

        CreateVoucherRequestDTO requestDto =
                validCreateRequest(
                        LocalDate.of(2026, 5, 10),
                        null
                );

        when(bindingResult.hasErrors())
                .thenReturn(true);

        String result =
                controller.processCreateForm(
                        requestDto,
                        bindingResult,
                        redirectAttributes
                );

        assertEquals(
                "admin/voucher-form",
                result
        );

        verify(bindingResult, never())
                .rejectValue(
                        anyString(),
                        anyString(),
                        anyString()
                );

        verify(voucherService, never())
                .create(any());

        verifyNoInteractions(
                messageSource
        );
    }

    // ========================================================================
    // EDIT FORM
    // ========================================================================

    @Test
    void showEditForm_shouldLoadVoucherWhenRequestMissing() {

        VoucherDTO dto =
                voucher();

        when(model.containsAttribute(
                "updateRequest"
        )).thenReturn(false);

        when(voucherService.findById("id"))
                .thenReturn(dto);

        String result =
                controller.showEditForm(
                        "id",
                        model
                );

        assertEquals(
                "admin/voucher-edit",
                result
        );

        verify(voucherService)
                .findById("id");

        verify(model)
                .addAttribute(
                        eq("updateRequest"),
                        any(UpdateVoucherRequestDTO.class)
                );

        verify(model)
                .addAttribute(
                        "voucherId",
                        "id"
                );
    }

    @Test
    void showEditForm_shouldKeepExistingRequest() {

        when(model.containsAttribute(
                "updateRequest"
        )).thenReturn(true);

        String result =
                controller.showEditForm(
                        "id",
                        model
                );

        assertEquals(
                "admin/voucher-edit",
                result
        );

        verify(voucherService, never())
                .findById(anyString());

        verify(model)
                .addAttribute(
                        "voucherId",
                        "id"
                );
    }

    // ========================================================================
    // EDIT VOUCHER
    // ========================================================================

    @Test
    void processEditForm_shouldReturnFormWhenBindingHasErrors() {

        UpdateVoucherRequestDTO requestDto =
                validUpdateRequest(
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(2)
                );

        when(bindingResult.hasErrors())
                .thenReturn(true);

        String result =
                controller.processEditForm(
                        "id",
                        requestDto,
                        bindingResult,
                        model,
                        redirectAttributes
                );

        assertEquals(
                "admin/voucher-edit",
                result
        );

        verify(model)
                .addAttribute(
                        "voucherId",
                        "id"
                );

        verify(voucherService, never())
                .update(anyString(), any());
    }

    @Test
    void processEditForm_shouldUpdateAndRedirectOnSuccess() {

        UpdateVoucherRequestDTO requestDto =
                validUpdateRequest(
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(2)
                );

        when(bindingResult.hasErrors())
                .thenReturn(false);

        when(messageSource.getMessage(
                eq("msg.tour.updated"),
                isNull(),
                any(Locale.class)
        )).thenReturn(
                "Tour updated"
        );

        String result =
                controller.processEditForm(
                        "id",
                        requestDto,
                        bindingResult,
                        model,
                        redirectAttributes
                );

        assertEquals(
                "redirect:/admin/vouchers",
                result
        );

        verify(voucherService)
                .update(
                        "id",
                        requestDto
                );

        verify(redirectAttributes)
                .addFlashAttribute(
                        "successMessage",
                        "Tour updated"
                );
    }

    @Test
    void processEditForm_shouldDetectEvictionYearError() {

        UpdateVoucherRequestDTO requestDto =
                validUpdateRequest(
                        LocalDate.of(2100, 1, 1),
                        LocalDate.of(2101, 2, 1)
                );

        when(bindingResult.hasErrors())
                .thenReturn(true);

        when(messageSource.getMessage(
                eq("err.date.year"),
                isNull(),
                any(Locale.class)
        )).thenReturn(
                "Year must not exceed 2100."
        );

        String result =
                controller.processEditForm(
                        "id",
                        requestDto,
                        bindingResult,
                        model,
                        redirectAttributes
                );

        assertEquals(
                "admin/voucher-edit",
                result
        );

        verify(messageSource)
                .getMessage(
                        eq("err.date.year"),
                        isNull(),
                        any(Locale.class)
                );

        verify(bindingResult)
                .rejectValue(
                        eq("evictionDate"),
                        eq("error.evictionDate"),
                        eq("Year must not exceed 2100.")
                );

        verify(voucherService, never())
                .update(anyString(), any());
    }

    @Test
    void processEditForm_shouldDetectWrongDateOrder() {

        UpdateVoucherRequestDTO requestDto =
                validUpdateRequest(
                        LocalDate.of(2026, 5, 10),
                        LocalDate.of(2026, 5, 1)
                );

        when(bindingResult.hasErrors())
                .thenReturn(true);

        when(messageSource.getMessage(
                eq("err.date.order"),
                isNull(),
                any(Locale.class)
        )).thenReturn(
                "Eviction date must be equal to or after the arrival date."
        );

        String result =
                controller.processEditForm(
                        "id",
                        requestDto,
                        bindingResult,
                        model,
                        redirectAttributes
                );

        assertEquals(
                "admin/voucher-edit",
                result
        );

        verify(messageSource)
                .getMessage(
                        eq("err.date.order"),
                        isNull(),
                        any(Locale.class)
                );

        verify(bindingResult)
                .rejectValue(
                        eq("evictionDate"),
                        eq("error.evictionDate"),
                        eq(
                                "Eviction date must be equal to or after the arrival date."
                        )
                );

        verify(voucherService, never())
                .update(anyString(), any());
    }

    @Test
    void processEditForm_shouldHandleNullArrivalDate() {

        UpdateVoucherRequestDTO requestDto =
                validUpdateRequest(
                        null,
                        LocalDate.of(2026, 5, 10)
                );

        when(bindingResult.hasErrors())
                .thenReturn(true);

        String result =
                controller.processEditForm(
                        "id",
                        requestDto,
                        bindingResult,
                        model,
                        redirectAttributes
                );

        assertEquals(
                "admin/voucher-edit",
                result
        );

        verify(bindingResult, never())
                .rejectValue(
                        anyString(),
                        anyString(),
                        anyString()
                );

        verify(model)
                .addAttribute(
                        "voucherId",
                        "id"
                );

        verify(voucherService, never())
                .update(anyString(), any());

        verifyNoInteractions(
                messageSource
        );
    }

    @Test
    void processEditForm_shouldHandleNullEvictionDate() {

        UpdateVoucherRequestDTO requestDto =
                validUpdateRequest(
                        LocalDate.of(2026, 5, 10),
                        null
                );

        when(bindingResult.hasErrors())
                .thenReturn(true);

        String result =
                controller.processEditForm(
                        "id",
                        requestDto,
                        bindingResult,
                        model,
                        redirectAttributes
                );

        assertEquals(
                "admin/voucher-edit",
                result
        );

        verify(bindingResult, never())
                .rejectValue(
                        anyString(),
                        anyString(),
                        anyString()
                );

        verify(model)
                .addAttribute(
                        "voucherId",
                        "id"
                );

        verify(voucherService, never())
                .update(anyString(), any());

        verifyNoInteractions(
                messageSource
        );
    }

    // ========================================================================
    // DELETE VOUCHER
    // ========================================================================

    @Test
    void deleteVoucher_shouldUseFallbackWhenRefererIsNull() {

        when(request.getHeader("Referer"))
                .thenReturn(null);

        when(messageSource.getMessage(
                eq("msg.tour.deleted"),
                isNull(),
                any(Locale.class)
        )).thenReturn(
                "Tour deleted"
        );

        String result =
                controller.deleteVoucher(
                        "id",
                        request,
                        redirectAttributes
                );

        assertEquals(
                "redirect:/admin/vouchers",
                result
        );

        verify(voucherService)
                .delete("id");

        verify(redirectAttributes)
                .addFlashAttribute(
                        "successMessage",
                        "Tour deleted"
                );
    }

    @Test
    void deleteVoucher_shouldUseRefererWhenPresent() {

        when(request.getHeader("Referer"))
                .thenReturn(
                        "/admin/vouchers?page=2"
                );

        when(messageSource.getMessage(
                eq("msg.tour.deleted"),
                isNull(),
                any(Locale.class)
        )).thenReturn(
                "Tour deleted"
        );

        String result =
                controller.deleteVoucher(
                        "id",
                        request,
                        redirectAttributes
                );

        assertEquals(
                "redirect:/admin/vouchers?page=2",
                result
        );

        verify(voucherService)
                .delete("id");

        verify(redirectAttributes)
                .addFlashAttribute(
                        "successMessage",
                        "Tour deleted"
                );
    }
}
