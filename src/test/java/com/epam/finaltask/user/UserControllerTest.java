package com.epam.finaltask.user;

import com.epam.finaltask.auth.AuthService;
import com.epam.finaltask.core.exception.invalidData.IllegalUserArgumentException;
import com.epam.finaltask.core.exception.notFound.ResourceNotFoundException;
import com.epam.finaltask.voucher.VoucherDTO;
import com.epam.finaltask.voucher.VoucherService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private VoucherService voucherService;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthService authService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Principal principal;

    @InjectMocks
    private UserController controller;

    // ========================================================================
    // HELPERS
    // ========================================================================

    private User createUser() {

        return User.builder()
                .id(UUID.randomUUID())
                .username("john")
                .email("john@test.com")
                .lastName("Smith")
                .phoneNumber("+48123456789")
                .balance(100.0)
                .active(true)
                .build();
    }

    private UserResponseDTO createUserResponse(User user) {

        return UserResponseDTO.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .balance(user.getBalance())
                .active(user.isActive())
                .build();
    }

    private VoucherDTO createVoucher() {

        return VoucherDTO.builder()
                .title("Tour")
                .build();
    }

    private UpdateProfileRequestDTO updateProfileRequest(
            String username,
            String email
    ) {

        return UpdateProfileRequestDTO.builder()
                .username(username)
                .email(email)
                .phoneNumber("+48123456789")
                .build();
    }

    private ChangePasswordRequestDTO changePasswordRequest() {

        return ChangePasswordRequestDTO.builder()
                .currentPassword("OldPassword1!")
                .newPassword("NewPassword1!")
                .build();
    }

    private DepositBalanceRequestDTO depositRequest() {

        return DepositBalanceRequestDTO.builder()
                .amount(20.0)
                .build();
    }

    /**
     * Prepares everything required when UserController
     * rebuilds the profile page after an error.
     */
    private void mockProfileDependencies(User user) {

        UserResponseDTO userResponse =
                createUserResponse(user);

        when(principal.getName())
                .thenReturn("john");

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userMapper.toUserResponseDTO(user))
                .thenReturn(userResponse);

        when(voucherService.findAllByUserIdPaged(
                eq(user.getId().toString()),
                anyInt(),
                eq(10)
        )).thenReturn(
                new PageImpl<>(
                        List.of(createVoucher())
                )
        );

        when(model.containsAttribute(anyString()))
                .thenReturn(true);
    }

    // ========================================================================
    // PROFILE
    // ========================================================================

    @Test
    void getProfilePage_shouldReturnProfile() {

        User user = createUser();

        UserResponseDTO userResponse =
                createUserResponse(user);

        when(principal.getName())
                .thenReturn("john");

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userMapper.toUserResponseDTO(user))
                .thenReturn(userResponse);

        Page<VoucherDTO> vouchersPage =
                new PageImpl<>(
                        List.of(createVoucher())
                );

        when(voucherService.findAllByUserIdPaged(
                user.getId().toString(),
                0,
                10
        )).thenReturn(vouchersPage);

        when(model.containsAttribute(
                "updateProfileRequest"
        )).thenReturn(false);

        when(model.containsAttribute(
                "changePasswordRequest"
        )).thenReturn(false);

        when(model.containsAttribute(
                "depositBalanceRequest"
        )).thenReturn(false);

        String result =
                controller.getProfilePage(
                        0,
                        principal,
                        model
                );

        assertEquals(
                "user/profile",
                result
        );

        verify(userRepository)
                .findUserByUsername("john");

        verify(userMapper)
                .toUserResponseDTO(user);

        verify(voucherService)
                .findAllByUserIdPaged(
                        user.getId().toString(),
                        0,
                        10
                );

        verify(model)
                .addAttribute(
                        eq("user"),
                        eq(userResponse)
                );

        verify(model)
                .addAttribute(
                        eq("myVouchersPage"),
                        eq(vouchersPage)
                );

        verify(model)
                .addAttribute(
                        eq("updateProfileRequest"),
                        any(UpdateProfileRequestDTO.class)
                );

        verify(model)
                .addAttribute(
                        eq("changePasswordRequest"),
                        any(ChangePasswordRequestDTO.class)
                );

        verify(model)
                .addAttribute(
                        eq("depositBalanceRequest"),
                        any(DepositBalanceRequestDTO.class)
                );
    }

    @Test
    void getProfilePage_shouldKeepExistingAttributes() {

        User user = createUser();

        UserResponseDTO userResponse =
                createUserResponse(user);

        when(principal.getName())
                .thenReturn("john");

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userMapper.toUserResponseDTO(user))
                .thenReturn(userResponse);

        when(voucherService.findAllByUserIdPaged(
                user.getId().toString(),
                0,
                10
        )).thenReturn(
                new PageImpl<>(
                        List.of(createVoucher())
                )
        );

        when(model.containsAttribute(anyString()))
                .thenReturn(true);

        String result =
                controller.getProfilePage(
                        0,
                        principal,
                        model
                );

        assertEquals(
                "user/profile",
                result
        );

        verify(model)
                .addAttribute(
                        eq("user"),
                        eq(userResponse)
                );

        verify(model)
                .addAttribute(
                        eq("myVouchersPage"),
                        any(Page.class)
                );

        verify(model, never())
                .addAttribute(
                        eq("updateProfileRequest"),
                        any()
                );

        verify(model, never())
                .addAttribute(
                        eq("changePasswordRequest"),
                        any()
                );

        verify(model, never())
                .addAttribute(
                        eq("depositBalanceRequest"),
                        any()
                );
    }

    @Test
    void getProfilePage_shouldCorrectOutOfRangePage() {

        User user = createUser();

        UserResponseDTO userResponse =
                createUserResponse(user);

        when(principal.getName())
                .thenReturn("john");

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userMapper.toUserResponseDTO(user))
                .thenReturn(userResponse);

        Page<VoucherDTO> emptyPage =
                new PageImpl<>(List.of());

        Page<VoucherDTO> validPage =
                new PageImpl<>(
                        List.of(createVoucher()),
                        PageRequest.of(0, 10),
                        1
                );

        when(voucherService.findAllByUserIdPaged(
                user.getId().toString(),
                3,
                10
        )).thenReturn(emptyPage);

        when(voucherService.findAllByUserIdPaged(
                user.getId().toString(),
                0,
                10
        )).thenReturn(validPage);

        when(model.containsAttribute(anyString()))
                .thenReturn(true);

        String result =
                controller.getProfilePage(
                        3,
                        principal,
                        model
                );

        assertEquals(
                "user/profile",
                result
        );

        verify(voucherService)
                .findAllByUserIdPaged(
                        user.getId().toString(),
                        3,
                        10
                );

        verify(voucherService)
                .findAllByUserIdPaged(
                        user.getId().toString(),
                        0,
                        10
                );

        verify(model)
                .addAttribute(
                        eq("user"),
                        eq(userResponse)
                );

        verify(model)
                .addAttribute(
                        eq("myVouchersPage"),
                        eq(validPage)
                );
    }

    @Test
    void getProfilePage_shouldKeepValidNonZeroPage() {

        User user = createUser();

        UserResponseDTO userResponse =
                createUserResponse(user);

        when(principal.getName())
                .thenReturn("john");

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userMapper.toUserResponseDTO(user))
                .thenReturn(userResponse);

        Page<VoucherDTO> validPage =
                new PageImpl<>(
                        List.of(createVoucher()),
                        PageRequest.of(1, 10),
                        25
                );

        when(voucherService.findAllByUserIdPaged(
                user.getId().toString(),
                1,
                10
        )).thenReturn(validPage);

        when(model.containsAttribute(anyString()))
                .thenReturn(true);

        String result =
                controller.getProfilePage(
                        1,
                        principal,
                        model
                );

        assertEquals(
                "user/profile",
                result
        );

        verify(voucherService)
                .findAllByUserIdPaged(
                        user.getId().toString(),
                        1,
                        10
                );

        verify(model)
                .addAttribute(
                        eq("user"),
                        eq(userResponse)
                );

        verify(model)
                .addAttribute(
                        eq("myVouchersPage"),
                        eq(validPage)
                );
    }

    @Test
    void getProfilePage_shouldThrowWhenUserNotFound() {

        when(principal.getName())
                .thenReturn("john");

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> controller.getProfilePage(
                        0,
                        principal,
                        model
                )
        );

        verify(voucherService, never())
                .findAllByUserIdPaged(
                        anyString(),
                        anyInt(),
                        anyInt()
                );

        verify(userMapper, never())
                .toUserResponseDTO(any(User.class));

        verify(model, never())
                .addAttribute(
                        eq("user"),
                        any()
                );
    }

    // ========================================================================
    // UPDATE PROFILE
    // ========================================================================

    @Test
    void updateProfile_shouldHandleValidationErrors() {

        User user = createUser();

        mockProfileDependencies(user);

        when(bindingResult.hasErrors())
                .thenReturn(true);

        UpdateProfileRequestDTO request =
                updateProfileRequest(
                        "john",
                        "john@test.com"
                );

        String result =
                controller.updateProfile(
                        request,
                        bindingResult,
                        principal,
                        model,
                        response
                );

        assertEquals(
                "user/profile",
                result
        );

        verify(userService, never())
                .updateUserProfile(
                        anyString(),
                        any(UpdateProfileRequestDTO.class)
                );

        verify(model)
                .addAttribute(
                        eq("user"),
                        eq(createUserResponse(user))
                );

        verify(userMapper)
                .toUserResponseDTO(user);

        verify(model)
                .addAttribute(
                        eq("myVouchersPage"),
                        any(Page.class)
                );
    }

    @Test
    void updateProfile_shouldSuccessWithoutUsernameChange() {

        when(bindingResult.hasErrors())
                .thenReturn(false);

        when(principal.getName())
                .thenReturn("john");

        UpdateProfileRequestDTO request =
                updateProfileRequest(
                        "john",
                        "john@test.com"
                );

        when(userService.updateUserProfile(
                "john",
                request
        )).thenReturn(false);

        String result =
                controller.updateProfile(
                        request,
                        bindingResult,
                        principal,
                        model,
                        response
                );

        assertEquals(
                "redirect:/profile?success",
                result
        );

        verify(userService)
                .updateUserProfile(
                        "john",
                        request
                );

        verify(authService, never())
                .refreshToken(anyString());

        verify(response, never())
                .addCookie(any(Cookie.class));
    }

    @Test
    void updateProfile_shouldRefreshTokenWhenUsernameChanges() {

        when(bindingResult.hasErrors())
                .thenReturn(false);

        when(principal.getName())
                .thenReturn("john");

        UpdateProfileRequestDTO request =
                updateProfileRequest(
                        "newjohn",
                        "john@test.com"
                );

        when(userService.updateUserProfile(
                "john",
                request
        )).thenReturn(true);

        when(authService.refreshToken("newjohn"))
                .thenReturn("new-token");

        String result =
                controller.updateProfile(
                        request,
                        bindingResult,
                        principal,
                        model,
                        response
                );

        assertEquals(
                "redirect:/profile?success",
                result
        );

        verify(userService)
                .updateUserProfile(
                        "john",
                        request
                );

        verify(authService)
                .refreshToken("newjohn");

        ArgumentCaptor<Cookie> captor =
                ArgumentCaptor.forClass(
                        Cookie.class
                );

        verify(response)
                .addCookie(captor.capture());

        Cookie cookie =
                captor.getValue();

        assertEquals(
                "JWT",
                cookie.getName()
        );

        assertEquals(
                "new-token",
                cookie.getValue()
        );

        assertTrue(
                cookie.isHttpOnly()
        );

        assertEquals(
                "/",
                cookie.getPath()
        );

        assertEquals(
                24 * 60 * 60,
                cookie.getMaxAge()
        );
    }

    @Test
    void updateProfile_shouldHandleEmailError() {

        User user = createUser();

        UserResponseDTO userResponse =
                createUserResponse(user);

        mockProfileDependencies(user);

        when(bindingResult.hasErrors())
                .thenReturn(false);

        UpdateProfileRequestDTO request =
                updateProfileRequest(
                        "john",
                        "taken@test.com"
                );

        when(userService.updateUserProfile(
                "john",
                request
        )).thenThrow(
                new IllegalUserArgumentException(
                        "err.email.taken"
                )
        );

        String result =
                controller.updateProfile(
                        request,
                        bindingResult,
                        principal,
                        model,
                        response
                );

        assertEquals(
                "user/profile",
                result
        );

        verify(bindingResult)
                .rejectValue(
                        eq("email"),
                        eq("err.email.taken")
                );

        verify(model)
                .addAttribute(
                        eq("user"),
                        eq(userResponse)
                );
    }

    @Test
    void updateProfile_shouldHandleUsernameError() {

        User user = createUser();

        UserResponseDTO userResponse =
                createUserResponse(user);

        mockProfileDependencies(user);

        when(bindingResult.hasErrors())
                .thenReturn(false);

        UpdateProfileRequestDTO request =
                updateProfileRequest(
                        "taken",
                        "john@test.com"
                );

        when(userService.updateUserProfile(
                "john",
                request
        )).thenThrow(
                new IllegalUserArgumentException(
                        "err.username.taken"
                )
        );

        String result =
                controller.updateProfile(
                        request,
                        bindingResult,
                        principal,
                        model,
                        response
                );

        assertEquals(
                "user/profile",
                result
        );

        verify(bindingResult)
                .rejectValue(
                        eq("username"),
                        eq("err.username.taken")
                );

        verify(model)
                .addAttribute(
                        eq("user"),
                        eq(userResponse)
                );
    }

    // ========================================================================
    // CHANGE PASSWORD
    // ========================================================================

    @Test
    void changePassword_shouldHandleValidationErrors() {

        User user = createUser();

        UserResponseDTO userResponse =
                createUserResponse(user);

        mockProfileDependencies(user);

        when(bindingResult.hasErrors())
                .thenReturn(true);

        ChangePasswordRequestDTO request =
                changePasswordRequest();

        String result =
                controller.changePassword(
                        request,
                        bindingResult,
                        principal,
                        model
                );

        assertEquals(
                "user/profile",
                result
        );

        verify(userService, never())
                .changePassword(
                        anyString(),
                        any(ChangePasswordRequestDTO.class)
                );

        verify(model)
                .addAttribute(
                        eq("user"),
                        eq(userResponse)
                );
    }

    @Test
    void changePassword_shouldHandleException() {

        User user = createUser();

        UserResponseDTO userResponse =
                createUserResponse(user);

        mockProfileDependencies(user);

        when(bindingResult.hasErrors())
                .thenReturn(false);

        ChangePasswordRequestDTO request =
                changePasswordRequest();

        doThrow(
                new IllegalUserArgumentException(
                        "err.password.current.invalid"
                )
        ).when(userService)
                .changePassword(
                        "john",
                        request
                );

        String result =
                controller.changePassword(
                        request,
                        bindingResult,
                        principal,
                        model
                );

        assertEquals(
                "user/profile",
                result
        );

        verify(bindingResult)
                .rejectValue(
                        eq("currentPassword"),
                        eq("err.password.current.invalid")
                );

        verify(model)
                .addAttribute(
                        eq("user"),
                        eq(userResponse)
                );
    }

    @Test
    void changePassword_shouldHandleExceptionWithNullMessage() {

        User user = createUser();

        UserResponseDTO userResponse =
                createUserResponse(user);

        mockProfileDependencies(user);

        when(bindingResult.hasErrors())
                .thenReturn(false);

        ChangePasswordRequestDTO request =
                changePasswordRequest();

        doThrow(
                new IllegalUserArgumentException(
                        (String) null
                )
        ).when(userService)
                .changePassword(
                        "john",
                        request
                );

        String result =
                controller.changePassword(
                        request,
                        bindingResult,
                        principal,
                        model
                );

        assertEquals(
                "user/profile",
                result
        );

        verify(bindingResult)
                .rejectValue(
                        eq("currentPassword"),
                        eq("error.unknown")
                );

        verify(model)
                .addAttribute(
                        eq("user"),
                        eq(userResponse)
                );
    }

    @Test
    void changePassword_shouldHandleExceptionWithBlankMessage() {

        User user = createUser();

        UserResponseDTO userResponse =
                createUserResponse(user);

        mockProfileDependencies(user);

        when(bindingResult.hasErrors())
                .thenReturn(false);

        ChangePasswordRequestDTO request =
                changePasswordRequest();

        doThrow(
                new IllegalUserArgumentException(
                        "   "
                )
        ).when(userService)
                .changePassword(
                        "john",
                        request
                );

        String result =
                controller.changePassword(
                        request,
                        bindingResult,
                        principal,
                        model
                );

        assertEquals(
                "user/profile",
                result
        );

        verify(bindingResult)
                .rejectValue(
                        eq("currentPassword"),
                        eq("error.unknown")
                );

        verify(model)
                .addAttribute(
                        eq("user"),
                        eq(userResponse)
                );
    }

    @Test
    void changePassword_shouldSuccess() {

        when(bindingResult.hasErrors())
                .thenReturn(false);

        when(principal.getName())
                .thenReturn("john");

        ChangePasswordRequestDTO request =
                changePasswordRequest();

        String result =
                controller.changePassword(
                        request,
                        bindingResult,
                        principal,
                        model
                );

        assertEquals(
                "redirect:/profile?passwordChanged",
                result
        );

        verify(userService)
                .changePassword(
                        "john",
                        request
                );
    }

    // ========================================================================
    // BALANCE
    // ========================================================================

    @Test
    void depositBalance_shouldHandleValidationErrors() {

        User user = createUser();

        UserResponseDTO userResponse =
                createUserResponse(user);

        mockProfileDependencies(user);

        when(bindingResult.hasErrors())
                .thenReturn(true);

        DepositBalanceRequestDTO request =
                depositRequest();

        String result =
                controller.depositBalance(
                        request,
                        bindingResult,
                        principal,
                        model
                );

        assertEquals(
                "user/profile",
                result
        );

        verify(userService, never())
                .depositBalance(
                        anyString(),
                        any(DepositBalanceRequestDTO.class)
                );

        verify(model)
                .addAttribute(
                        eq("user"),
                        eq(userResponse)
                );
    }

    @Test
    void depositBalance_shouldHandleException() {

        User user = createUser();

        UserResponseDTO userResponse =
                createUserResponse(user);

        mockProfileDependencies(user);

        when(bindingResult.hasErrors())
                .thenReturn(false);

        DepositBalanceRequestDTO request =
                depositRequest();

        doThrow(
                new IllegalArgumentException(
                        "Amount is invalid"
                )
        ).when(userService)
                .depositBalance(
                        "john",
                        request
                );

        String result =
                controller.depositBalance(
                        request,
                        bindingResult,
                        principal,
                        model
                );

        assertEquals(
                "user/profile",
                result
        );

        verify(bindingResult)
                .rejectValue(
                        eq("amount"),
                        eq("Amount is invalid")
                );

        verify(model)
                .addAttribute(
                        eq("user"),
                        eq(userResponse)
                );
    }

    @Test
    void depositBalance_shouldHandleExceptionWithNullMessage() {

        User user = createUser();

        UserResponseDTO userResponse =
                createUserResponse(user);

        mockProfileDependencies(user);

        when(bindingResult.hasErrors())
                .thenReturn(false);

        DepositBalanceRequestDTO request =
                depositRequest();

        doThrow(
                new IllegalArgumentException(
                        (String) null
                )
        ).when(userService)
                .depositBalance(
                        "john",
                        request
                );

        String result =
                controller.depositBalance(
                        request,
                        bindingResult,
                        principal,
                        model
                );

        assertEquals(
                "user/profile",
                result
        );

        verify(bindingResult)
                .rejectValue(
                        eq("amount"),
                        eq("error.unknown")
                );

        verify(model)
                .addAttribute(
                        eq("user"),
                        eq(userResponse)
                );
    }

    @Test
    void depositBalance_shouldHandleExceptionWithBlankMessage() {

        User user = createUser();

        UserResponseDTO userResponse =
                createUserResponse(user);

        mockProfileDependencies(user);

        when(bindingResult.hasErrors())
                .thenReturn(false);

        DepositBalanceRequestDTO request =
                depositRequest();

        doThrow(
                new IllegalArgumentException(
                        "   "
                )
        ).when(userService)
                .depositBalance(
                        "john",
                        request
                );

        String result =
                controller.depositBalance(
                        request,
                        bindingResult,
                        principal,
                        model
                );

        assertEquals(
                "user/profile",
                result
        );

        verify(bindingResult)
                .rejectValue(
                        eq("amount"),
                        eq("error.unknown")
                );

        verify(model)
                .addAttribute(
                        eq("user"),
                        eq(userResponse)
                );
    }

    @Test
    void depositBalance_shouldSuccess() {

        when(bindingResult.hasErrors())
                .thenReturn(false);

        when(principal.getName())
                .thenReturn("john");

        DepositBalanceRequestDTO request =
                depositRequest();

        String result =
                controller.depositBalance(
                        request,
                        bindingResult,
                        principal,
                        model
                );

        assertEquals(
                "redirect:/profile?balanceSuccess",
                result
        );

        verify(userService)
                .depositBalance(
                        "john",
                        request
                );
    }

    // ========================================================================
    // DELETE
    // ========================================================================

    @Test
    void deleteAccount_shouldDeleteUserAndClearCookie() {

        when(principal.getName())
                .thenReturn("john");

        String result =
                controller.deleteAccount(
                        principal,
                        response
                );

        assertEquals(
                "redirect:/",
                result
        );

        verify(userService)
                .deleteAccount("john");

        ArgumentCaptor<Cookie> captor =
                ArgumentCaptor.forClass(
                        Cookie.class
                );

        verify(response)
                .addCookie(captor.capture());

        Cookie cookie =
                captor.getValue();

        assertEquals(
                "JWT",
                cookie.getName()
        );

        assertNull(
                cookie.getValue()
        );

        assertEquals(
                0,
                cookie.getMaxAge()
        );

        assertEquals(
                "/",
                cookie.getPath()
        );

        assertTrue(
                cookie.isHttpOnly()
        );
    }

    // ========================================================================
    // RESOLVE ERROR CODE
    // ========================================================================

    @Test
    void updateProfile_shouldHandleBlankErrorMessage() {

        User user = createUser();

        UserResponseDTO userResponse =
                createUserResponse(user);

        mockProfileDependencies(user);

        when(bindingResult.hasErrors())
                .thenReturn(false);

        UpdateProfileRequestDTO request =
                updateProfileRequest(
                        "taken",
                        "john@test.com"
                );

        when(userService.updateUserProfile(
                "john",
                request
        )).thenThrow(
                new IllegalUserArgumentException(
                        "   "
                )
        );

        String result =
                controller.updateProfile(
                        request,
                        bindingResult,
                        principal,
                        model,
                        response
                );

        assertEquals(
                "user/profile",
                result
        );

        verify(bindingResult)
                .rejectValue(
                        eq("username"),
                        eq("error.unknown")
                );

        verify(model)
                .addAttribute(
                        eq("user"),
                        eq(userResponse)
                );
    }

    @Test
    void updateProfile_shouldHandleNullErrorMessage() {

        User user = createUser();

        UserResponseDTO userResponse =
                createUserResponse(user);

        mockProfileDependencies(user);

        when(bindingResult.hasErrors())
                .thenReturn(false);

        UpdateProfileRequestDTO request =
                updateProfileRequest(
                        "taken",
                        "john@test.com"
                );

        when(userService.updateUserProfile(
                "john",
                request
        )).thenThrow(
                new IllegalUserArgumentException(
                        (String) null
                )
        );

        String result =
                controller.updateProfile(
                        request,
                        bindingResult,
                        principal,
                        model,
                        response
                );

        assertEquals(
                "user/profile",
                result
        );

        verify(bindingResult)
                .rejectValue(
                        eq("username"),
                        eq("error.unknown")
                );

        verify(model)
                .addAttribute(
                        eq("user"),
                        eq(userResponse)
                );
    }
}