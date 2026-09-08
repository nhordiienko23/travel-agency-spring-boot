package com.epam.finaltask.user;

import com.epam.finaltask.voucher.Voucher;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoBuilderTest {

    // ========================================================================
    // USER BUILDER
    // ========================================================================

    @Test
    void userBuilder_shouldBuildUserWithAllFields() {

        UUID id = UUID.randomUUID();
        List<Voucher> vouchers = new ArrayList<>();

        User user = User.builder()
                .id(id)
                .username("john")
                .email("john@gmail.com")
                .password("Password123!")
                .lastName("Smith")
                .phoneNumber("+48123456789")
                .balance(100.0)
                .role(Role.USER)
                .active(false)
                .vouchers(vouchers)
                .build();

        assertEquals(id, user.getId());
        assertEquals("john", user.getUsername());
        assertEquals("john@gmail.com", user.getEmail());
        assertEquals("Password123!", user.getPassword());
        assertEquals("Smith", user.getLastName());
        assertEquals("+48123456789", user.getPhoneNumber());
        assertEquals(100.0, user.getBalance());
        assertEquals(Role.USER, user.getRole());
        assertFalse(user.isActive());
        assertSame(vouchers, user.getVouchers());
    }

    @Test
    void userBuilder_shouldUseActiveTrueByDefault() {

        User user = User.builder()
                .username("john")
                .email("john@gmail.com")
                .build();

        assertTrue(user.isActive());
    }

    @Test
    void userBuilder_toString_shouldReturnString() {

        User.UserBuilder builder = User.builder()
                .username("john")
                .email("john@gmail.com");

        assertNotNull(builder.toString());
    }

    // ========================================================================
    // USER GETTERS AND SETTERS
    // ========================================================================

    @Test
    void user_shouldSupportAllGettersAndSetters() {

        User user = new User();

        UUID id = UUID.randomUUID();
        List<Voucher> vouchers = new ArrayList<>();

        user.setId(id);
        user.setUsername("john");
        user.setEmail("john@gmail.com");
        user.setPassword("Password123!");
        user.setLastName("Smith");
        user.setPhoneNumber("+48123456789");
        user.setBalance(150.0);
        user.setRole(Role.MANAGER);
        user.setActive(false);
        user.setVouchers(vouchers);

        assertEquals(id, user.getId());
        assertEquals("john", user.getUsername());
        assertEquals("john@gmail.com", user.getEmail());
        assertEquals("Password123!", user.getPassword());
        assertEquals("Smith", user.getLastName());
        assertEquals("+48123456789", user.getPhoneNumber());
        assertEquals(150.0, user.getBalance());
        assertEquals(Role.MANAGER, user.getRole());
        assertFalse(user.isActive());
        assertSame(vouchers, user.getVouchers());
    }

    // ========================================================================
    // USER RESPONSE DTO
    // ========================================================================

    @Test
    void userResponseDtoBuilder_shouldBuildDtoWithAllFields() {

        UserResponseDTO dto = UserResponseDTO.builder()
                .id("123")
                .username("john")
                .email("john@gmail.com")
                .lastName("Smith")
                .phoneNumber("+48123456789")
                .balance(100.0)
                .role("USER")
                .active(true)
                .build();

        assertEquals("123", dto.id());
        assertEquals("john", dto.username());
        assertEquals("john@gmail.com", dto.email());
        assertEquals("Smith", dto.lastName());
        assertEquals("+48123456789", dto.phoneNumber());
        assertEquals(100.0, dto.balance());
        assertEquals("USER", dto.role());
        assertTrue(dto.active());
    }

    @Test
    void userResponseDtoBuilder_toString_shouldReturnString() {

        UserResponseDTO.UserResponseDTOBuilder builder =
                UserResponseDTO.builder();

        assertNotNull(builder.toString());
    }

    // ========================================================================
    // UPDATE PROFILE DTO
    // ========================================================================

    @Test
    void updateProfileRequestDtoBuilder_shouldBuildDtoWithAllFields() {

        UpdateProfileRequestDTO dto =
                UpdateProfileRequestDTO.builder()
                        .username("john")
                        .email("john@gmail.com")
                        .lastName("Smith")
                        .phoneNumber("+48123456789")
                        .build();

        assertEquals("john", dto.username());
        assertEquals("john@gmail.com", dto.email());
        assertEquals("Smith", dto.lastName());
        assertEquals("+48123456789", dto.phoneNumber());
    }

    @Test
    void updateProfileRequestDtoBuilder_toString_shouldReturnString() {

        UpdateProfileRequestDTO.UpdateProfileRequestDTOBuilder builder =
                UpdateProfileRequestDTO.builder();

        assertNotNull(builder.toString());
    }

    // ========================================================================
    // CHANGE ACCOUNT STATUS DTO
    // ========================================================================

    @Test
    void changeAccountStatusRequestDtoBuilder_shouldBuildDtoWithAllFields() {

        UUID userId = UUID.randomUUID();

        ChangeAccountStatusRequestDTO dto =
                ChangeAccountStatusRequestDTO.builder()
                        .id(userId)
                        .active(false)
                        .build();

        assertEquals(userId, dto.id());
        assertFalse(dto.active());
    }

    @Test
    void changeAccountStatusRequestDtoBuilder_toString_shouldReturnString() {

        ChangeAccountStatusRequestDTO.ChangeAccountStatusRequestDTOBuilder builder =
                ChangeAccountStatusRequestDTO.builder();

        assertNotNull(builder.toString());
    }

    // ========================================================================
    // ADMIN DEPOSIT BALANCE DTO
    // ========================================================================

    @Test
    void adminDepositBalanceRequestDtoBuilder_shouldBuildDtoWithAllFields() {

        UUID userId = UUID.randomUUID();

        AdminDepositBalanceRequestDTO dto =
                AdminDepositBalanceRequestDTO.builder()
                        .userId(userId)
                        .amount(250.0)
                        .build();

        assertEquals(userId, dto.userId());
        assertEquals(250.0, dto.amount());
    }

    @Test
    void adminDepositBalanceRequestDtoBuilder_toString_shouldReturnString() {

        AdminDepositBalanceRequestDTO.AdminDepositBalanceRequestDTOBuilder builder =
                AdminDepositBalanceRequestDTO.builder();

        assertNotNull(builder.toString());
    }

    // ========================================================================
    // CHANGE PASSWORD DTO
    // ========================================================================

    @Test
    void changePasswordRequestDtoBuilder_shouldBuildDtoWithAllFields() {

        ChangePasswordRequestDTO dto =
                ChangePasswordRequestDTO.builder()
                        .currentPassword("OldPassword1!")
                        .newPassword("NewPassword1!")
                        .build();

        assertEquals("OldPassword1!", dto.currentPassword());
        assertEquals("NewPassword1!", dto.newPassword());
    }

    @Test
    void changePasswordRequestDtoBuilder_toString_shouldReturnString() {

        ChangePasswordRequestDTO.ChangePasswordRequestDTOBuilder builder =
                ChangePasswordRequestDTO.builder();

        assertNotNull(builder.toString());
    }

    // ========================================================================
    // DEPOSIT BALANCE DTO
    // ========================================================================

    @Test
    void depositBalanceRequestDtoBuilder_shouldBuildDtoWithAmount() {

        DepositBalanceRequestDTO dto =
                DepositBalanceRequestDTO.builder()
                        .amount(50.0)
                        .build();

        assertEquals(50.0, dto.amount());
    }

    @Test
    void depositBalanceRequestDtoBuilder_toString_shouldReturnString() {

        DepositBalanceRequestDTO.DepositBalanceRequestDTOBuilder builder =
                DepositBalanceRequestDTO.builder();

        assertNotNull(builder.toString());
    }

    // ========================================================================
    // USER SEARCH REQUEST DTO
    // ========================================================================

    @Test
    void userSearchRequestDto_shouldStoreAllValues() {

        UserSearchRequestDTO dto =
                new UserSearchRequestDTO(
                        "john",
                        "+48123456789",
                        "USER",
                        true
                );

        assertEquals("john", dto.username());
        assertEquals("+48123456789", dto.phoneNumber());
        assertEquals("USER", dto.role());
        assertTrue(dto.active());
    }

    @Test
    void userSearchRequestDto_shouldAllowNullValues() {

        UserSearchRequestDTO dto =
                new UserSearchRequestDTO(
                        null,
                        null,
                        null,
                        null
                );

        assertNull(dto.username());
        assertNull(dto.phoneNumber());
        assertNull(dto.role());
        assertNull(dto.active());
    }

    // ========================================================================
    // RECORD EQUALITY / TO STRING
    // ========================================================================

    @Test
    void userResponseDto_shouldSupportEqualsAndHashCode() {

        UserResponseDTO first =
                UserResponseDTO.builder()
                        .id("1")
                        .username("john")
                        .email("john@gmail.com")
                        .lastName("Smith")
                        .phoneNumber("+48123456789")
                        .balance(100.0)
                        .role("USER")
                        .active(true)
                        .build();

        UserResponseDTO second =
                UserResponseDTO.builder()
                        .id("1")
                        .username("john")
                        .email("john@gmail.com")
                        .lastName("Smith")
                        .phoneNumber("+48123456789")
                        .balance(100.0)
                        .role("USER")
                        .active(true)
                        .build();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotNull(first.toString());
    }

    @Test
    void updateProfileRequestDto_shouldSupportEqualsAndHashCode() {

        UpdateProfileRequestDTO first =
                new UpdateProfileRequestDTO(
                        "john",
                        "john@gmail.com",
                        "Smith",
                        "+48123456789"
                );

        UpdateProfileRequestDTO second =
                new UpdateProfileRequestDTO(
                        "john",
                        "john@gmail.com",
                        "Smith",
                        "+48123456789"
                );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotNull(first.toString());
    }

    @Test
    void changePasswordRequestDto_shouldSupportEqualsAndHashCode() {

        ChangePasswordRequestDTO first =
                new ChangePasswordRequestDTO(
                        "OldPassword1!",
                        "NewPassword1!"
                );

        ChangePasswordRequestDTO second =
                new ChangePasswordRequestDTO(
                        "OldPassword1!",
                        "NewPassword1!"
                );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotNull(first.toString());
    }

    @Test
    void changeAccountStatusRequestDto_shouldSupportEqualsAndHashCode() {

        UUID id = UUID.randomUUID();

        ChangeAccountStatusRequestDTO first =
                new ChangeAccountStatusRequestDTO(id, true);

        ChangeAccountStatusRequestDTO second =
                new ChangeAccountStatusRequestDTO(id, true);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotNull(first.toString());
    }

    @Test
    void adminDepositBalanceRequestDto_shouldSupportEqualsAndHashCode() {

        UUID id = UUID.randomUUID();

        AdminDepositBalanceRequestDTO first =
                new AdminDepositBalanceRequestDTO(id, 100.0);

        AdminDepositBalanceRequestDTO second =
                new AdminDepositBalanceRequestDTO(id, 100.0);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotNull(first.toString());
    }

    @Test
    void depositBalanceRequestDto_shouldSupportEqualsAndHashCode() {

        DepositBalanceRequestDTO first =
                new DepositBalanceRequestDTO(100.0);

        DepositBalanceRequestDTO second =
                new DepositBalanceRequestDTO(100.0);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotNull(first.toString());
    }

    @Test
    void userSearchRequestDto_shouldSupportEqualsAndHashCode() {

        UserSearchRequestDTO first =
                new UserSearchRequestDTO(
                        "john",
                        "+48123456789",
                        "USER",
                        true
                );

        UserSearchRequestDTO second =
                new UserSearchRequestDTO(
                        "john",
                        "+48123456789",
                        "USER",
                        true
                );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotNull(first.toString());
    }
}

