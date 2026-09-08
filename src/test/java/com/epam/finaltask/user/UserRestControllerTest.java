package com.epam.finaltask.user;

import com.epam.finaltask.core.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRestControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private Principal principal;

    @InjectMocks
    private UserRestController controller;

    // ========================================================================
    // HELPERS
    // ========================================================================

    private void mockMessage(
            String key,
            String message
    ) {

        when(messageSource.getMessage(
                eq(key),
                isNull(),
                any(Locale.class)
        )).thenReturn(message);
    }

    private UserResponseDTO userResponse() {

        return UserResponseDTO.builder()
                .id(UUID.randomUUID().toString())
                .username("john")
                .email("john@test.com")
                .lastName("Smith")
                .phoneNumber("+48123456789")
                .balance(100.0)
                .role("USER")
                .active(true)
                .build();
    }

    // ========================================================================
    // SEARCH USERS
    // ========================================================================


    @Test
    void searchUsers_shouldReturnUsersPage() {

        UserResponseDTO dto = userResponse();

        Page<UserResponseDTO> userPage =
                new PageImpl<>(
                        List.of(dto),
                        PageRequest.of(0, 10),
                        1
                );

        UserSearchRequestDTO requestDTO =
                mock(UserSearchRequestDTO.class);

        mockMessage(
                "msg.user.fetchedUsers",
                "Users fetched successfully"
        );

        when(userService.findUsers(
                requestDTO,
                0,
                10
        )).thenReturn(userPage);

        ResponseEntity<
                ApiResponse<Page<UserResponseDTO>>
                > response =
                controller.searchUsers(
                        requestDTO,
                        PageRequest.of(0, 10)
                );

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertNotNull(
                response.getBody()
        );

        assertEquals(
                "OK",
                response.getBody().getStatusCode()
        );

        assertEquals(
                "Users fetched successfully",
                response.getBody().getStatusMessage()
        );

        assertSame(
                userPage,
                response.getBody().getResults()
        );

        assertEquals(
                1,
                response.getBody()
                        .getResults()
                        .getTotalElements()
        );

        assertEquals(
                1,
                response.getBody()
                        .getResults()
                        .getContent()
                        .size()
        );

        assertSame(
                dto,
                response.getBody()
                        .getResults()
                        .getContent()
                        .get(0)
        );

        verify(userService)
                .findUsers(
                        requestDTO,
                        0,
                        10
                );

        verify(messageSource)
                .getMessage(
                        eq("msg.user.fetchedUsers"),
                        isNull(),
                        any(Locale.class)
                );
    }



    // ========================================================================
    // GET USER BY ID
    // ========================================================================

    @Test
    void getUserById_shouldReturnOkWithUser() {

        UUID id = UUID.randomUUID();

        UserResponseDTO dto =
                UserResponseDTO.builder()
                        .id(id.toString())
                        .username("john")
                        .email("john@test.com")
                        .build();

        mockMessage(
                "msg.user.fetched",
                "User fetched successfully"
        );

        when(userService.getUserById(id))
                .thenReturn(dto);

        ResponseEntity<
                ApiResponse<UserResponseDTO>
                > response =
                controller.getUserById(id);

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertNotNull(
                response.getBody()
        );

        assertEquals(
                "OK",
                response.getBody().getStatusCode()
        );

        assertEquals(
                "User fetched successfully",
                response.getBody().getStatusMessage()
        );

        assertSame(
                dto,
                response.getBody().getResults()
        );

        verify(userService)
                .getUserById(id);

        verify(messageSource)
                .getMessage(
                        eq("msg.user.fetched"),
                        isNull(),
                        any(Locale.class)
                );
    }

    // ========================================================================
    // GET USER BY USERNAME
    // ========================================================================

    @Test
    void getUserByUsername_shouldReturnOkWithUser() {

        UserResponseDTO dto =
                UserResponseDTO.builder()
                        .id("123")
                        .username("john")
                        .email("john@test.com")
                        .build();

        mockMessage(
                "msg.user.fetched",
                "User fetched successfully"
        );

        when(userService.getUserByUsername("john"))
                .thenReturn(dto);

        ResponseEntity<
                ApiResponse<UserResponseDTO>
                > response =
                controller.getUserByUsername("john");

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertNotNull(
                response.getBody()
        );

        assertEquals(
                "OK",
                response.getBody().getStatusCode()
        );

        assertEquals(
                "User fetched successfully",
                response.getBody().getStatusMessage()
        );

        assertSame(
                dto,
                response.getBody().getResults()
        );

        verify(userService)
                .getUserByUsername("john");

        verify(messageSource)
                .getMessage(
                        eq("msg.user.fetched"),
                        isNull(),
                        any(Locale.class)
                );
    }

    // ========================================================================
    // CHANGE ACCOUNT STATUS
    // ========================================================================

    @Test
    void changeAccountStatus_shouldReturnUpdatedUser() {

        UUID id = UUID.randomUUID();

        ChangeAccountStatusRequestDTO request =
                ChangeAccountStatusRequestDTO.builder()
                        .id(id)
                        .active(false)
                        .build();

        UserResponseDTO dto =
                UserResponseDTO.builder()
                        .id(id.toString())
                        .username("john")
                        .active(false)
                        .build();

        mockMessage(
                "msg.user.status.changed",
                "Account status successfully changed"
        );

        when(userService.changeAccountStatus(request))
                .thenReturn(dto);

        ResponseEntity<
                ApiResponse<UserResponseDTO>
                > response =
                controller.changeAccountStatus(request);

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertNotNull(
                response.getBody()
        );

        assertEquals(
                "OK",
                response.getBody().getStatusCode()
        );

        assertEquals(
                "Account status successfully changed",
                response.getBody().getStatusMessage()
        );

        assertSame(
                dto,
                response.getBody().getResults()
        );

        verify(userService)
                .changeAccountStatus(request);

        verify(messageSource)
                .getMessage(
                        eq("msg.user.status.changed"),
                        isNull(),
                        any(Locale.class)
                );
    }

    // ========================================================================
    // UPDATE PROFILE
    // ========================================================================

    @Test
    void updateProfile_shouldUpdateAndReturnFreshUser() {

        when(principal.getName())
                .thenReturn("john");

        UpdateProfileRequestDTO request =
                UpdateProfileRequestDTO.builder()
                        .username("john")
                        .email("john@test.com")
                        .lastName("Doe")
                        .phoneNumber("+48123456789")
                        .build();

        UserResponseDTO dto =
                UserResponseDTO.builder()
                        .id("123")
                        .username("john")
                        .email("john@test.com")
                        .lastName("Doe")
                        .phoneNumber("+48123456789")
                        .build();

        mockMessage(
                "msg.user.profile.updated",
                "Profile updated successfully"
        );

        when(userService.getUserByUsername("john"))
                .thenReturn(dto);

        ResponseEntity<
                ApiResponse<UserResponseDTO>
                > response =
                controller.updateProfile(
                        request,
                        principal
                );

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertNotNull(
                response.getBody()
        );

        assertEquals(
                "OK",
                response.getBody().getStatusCode()
        );

        assertEquals(
                "Profile updated successfully",
                response.getBody().getStatusMessage()
        );

        assertSame(
                dto,
                response.getBody().getResults()
        );

        verify(principal, times(2))
                .getName();

        verify(userService)
                .updateUserProfile(
                        "john",
                        request
                );

        verify(userService)
                .getUserByUsername("john");

        verify(messageSource)
                .getMessage(
                        eq("msg.user.profile.updated"),
                        isNull(),
                        any(Locale.class)
                );
    }

    // ========================================================================
    // CHANGE PASSWORD
    // ========================================================================

    @Test
    void changePassword_shouldDelegateAndReturnSuccess() {

        when(principal.getName())
                .thenReturn("john");

        ChangePasswordRequestDTO request =
                ChangePasswordRequestDTO.builder()
                        .currentPassword("OldPassword1!")
                        .newPassword("NewPassword1!")
                        .build();

        mockMessage(
                "msg.user.password.changed",
                "Password changed successfully"
        );

        ResponseEntity<
                ApiResponse<Void>
                > response =
                controller.changePassword(
                        request,
                        principal
                );

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertNotNull(
                response.getBody()
        );

        assertEquals(
                "OK",
                response.getBody().getStatusCode()
        );

        assertEquals(
                "Password changed successfully",
                response.getBody().getStatusMessage()
        );

        assertNull(
                response.getBody().getResults()
        );

        verify(principal)
                .getName();

        verify(userService)
                .changePassword(
                        "john",
                        request
                );

        verify(messageSource)
                .getMessage(
                        eq("msg.user.password.changed"),
                        isNull(),
                        any(Locale.class)
                );
    }

    // ========================================================================
    // DEPOSIT BALANCE
    // ========================================================================

    @Test
    void depositBalance_shouldReturnUpdatedUser() {

        when(principal.getName())
                .thenReturn("john");

        DepositBalanceRequestDTO request =
                DepositBalanceRequestDTO.builder()
                        .amount(20.0)
                        .build();

        UserResponseDTO dto =
                UserResponseDTO.builder()
                        .username("john")
                        .balance(120.0)
                        .build();

        mockMessage(
                "msg.user.balance.toppedUp",
                "Balance topped up successfully"
        );

        when(userService.depositBalance(
                "john",
                request
        )).thenReturn(dto);

        ResponseEntity<
                ApiResponse<UserResponseDTO>
                > response =
                controller.depositBalance(
                        request,
                        principal
                );

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertNotNull(
                response.getBody()
        );

        assertEquals(
                "OK",
                response.getBody().getStatusCode()
        );

        assertEquals(
                "Balance topped up successfully",
                response.getBody().getStatusMessage()
        );

        assertSame(
                dto,
                response.getBody().getResults()
        );

        verify(principal)
                .getName();

        verify(userService)
                .depositBalance(
                        "john",
                        request
                );

        verify(messageSource)
                .getMessage(
                        eq("msg.user.balance.toppedUp"),
                        isNull(),
                        any(Locale.class)
                );
    }

    // ========================================================================
    // ADMIN DEPOSIT BALANCE
    // ========================================================================

    @Test
    void depositBalanceByAdmin_shouldReturnUpdatedUser() {

        UUID userId = UUID.randomUUID();

        AdminDepositBalanceRequestDTO request =
                AdminDepositBalanceRequestDTO.builder()
                        .userId(userId)
                        .amount(50.0)
                        .build();

        UserResponseDTO dto =
                UserResponseDTO.builder()
                        .id(userId.toString())
                        .username("john")
                        .balance(150.0)
                        .build();

        mockMessage(
                "msg.user.balance.adminToppedUp",
                "User balance topped up successfully"
        );

        when(userService.depositBalanceByAdmin(request))
                .thenReturn(dto);

        ResponseEntity<
                ApiResponse<UserResponseDTO>
                > response =
                controller.depositBalanceByAdmin(request);

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertNotNull(
                response.getBody()
        );

        assertEquals(
                "OK",
                response.getBody().getStatusCode()
        );

        assertEquals(
                "User balance topped up successfully",
                response.getBody().getStatusMessage()
        );

        assertSame(
                dto,
                response.getBody().getResults()
        );

        verify(userService)
                .depositBalanceByAdmin(request);

        verify(messageSource)
                .getMessage(
                        eq("msg.user.balance.adminToppedUp"),
                        isNull(),
                        any(Locale.class)
                );
    }

    // ========================================================================
    // DELETE ACCOUNT
    // ========================================================================

    @Test
    void deleteAccount_shouldDeleteAndReturnSuccess() {

        when(principal.getName())
                .thenReturn("john");

        mockMessage(
                "msg.user.account.deleted",
                "Account deleted successfully"
        );

        ResponseEntity<
                ApiResponse<Void>
                > response =
                controller.deleteAccount(principal);

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertNotNull(
                response.getBody()
        );

        assertEquals(
                "OK",
                response.getBody().getStatusCode()
        );

        assertEquals(
                "Account deleted successfully",
                response.getBody().getStatusMessage()
        );

        assertNull(
                response.getBody().getResults()
        );

        verify(principal)
                .getName();

        verify(userService)
                .deleteAccount("john");

        verify(messageSource)
                .getMessage(
                        eq("msg.user.account.deleted"),
                        isNull(),
                        any(Locale.class)
                );
    }

    // ========================================================================
    // API RESPONSE CONTENT
    // ========================================================================

    @Test
    void allMethods_shouldReturnOkStatusCode() {

        UUID id = UUID.randomUUID();

        UserResponseDTO dto =
                UserResponseDTO.builder()
                        .id(id.toString())
                        .username("john")
                        .build();

        mockMessage(
                "msg.user.fetched",
                "User fetched successfully"
        );

        when(userService.getUserById(id))
                .thenReturn(dto);

        ResponseEntity<
                ApiResponse<UserResponseDTO>
                > response =
                controller.getUserById(id);

        assertEquals(
                "OK",
                response.getBody().getStatusCode()
        );
    }

    // ========================================================================
    // MESSAGE SOURCE
    // ========================================================================

    @Test
    void getMsg_shouldUseCurrentLocale() {

        UUID id = UUID.randomUUID();

        UserResponseDTO dto =
                UserResponseDTO.builder()
                        .id(id.toString())
                        .build();

        when(messageSource.getMessage(
                eq("msg.user.fetched"),
                isNull(),
                any(Locale.class)
        )).thenReturn(
                "Пользователь успешно получен"
        );

        when(userService.getUserById(id))
                .thenReturn(dto);

        ResponseEntity<
                ApiResponse<UserResponseDTO>
                > response =
                controller.getUserById(id);

        assertEquals(
                "Пользователь успешно получен",
                response.getBody().getStatusMessage()
        );

        verify(messageSource)
                .getMessage(
                        eq("msg.user.fetched"),
                        isNull(),
                        any(Locale.class)
                );
    }
}

