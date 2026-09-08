package com.epam.finaltask.user;

import com.epam.finaltask.core.exception.invalidData.IllegalUserArgumentException;
import com.epam.finaltask.core.exception.notFound.ResourceNotFoundException;
import com.epam.finaltask.voucher.Voucher;
import com.epam.finaltask.voucher.VoucherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UUID userId;
    private User user;
    private UserResponseDTO userResponse;

    @BeforeEach
    void setUp() {

        userId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .username("john")
                .email("john@gmail.com")
                .password("encoded-password")
                .lastName("Smith")
                .phoneNumber("+48123456789")
                .balance(100.0)
                .role(Role.USER)
                .active(true)
                .vouchers(Collections.emptyList())
                .build();

        userResponse = UserResponseDTO.builder()
                .id(userId.toString())
                .username("john")
                .email("john@gmail.com")
                .lastName("Smith")
                .phoneNumber("+48123456789")
                .balance(100.0)
                .role("USER")
                .active(true)
                .build();
    }

    // ========================================================================
    // getUserById
    // ========================================================================

    @Test
    void getUserById_shouldReturnUserResponseDTO() {

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userMapper.toUserResponseDTO(user))
                .thenReturn(userResponse);

        UserResponseDTO result =
                userService.getUserById(userId);

        assertEquals(
                userResponse,
                result
        );

        verify(userRepository)
                .findById(userId);

        verify(userMapper)
                .toUserResponseDTO(user);
    }

    @Test
    void getUserById_shouldThrowWhenUserNotFound() {

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> userService.getUserById(userId)
                );

        assertEquals(
                "err.user.notFound",
                exception.getMessage()
        );

        verify(userRepository)
                .findById(userId);

        verifyNoInteractions(
                userMapper
        );
    }

    // ========================================================================
    // getUserByUsername
    // ========================================================================

    @Test
    void getUserByUsername_shouldReturnUserResponseDTO() {

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userMapper.toUserResponseDTO(user))
                .thenReturn(userResponse);

        UserResponseDTO result =
                userService.getUserByUsername("john");

        assertEquals(
                userResponse,
                result
        );

        verify(userRepository)
                .findUserByUsername("john");

        verify(userMapper)
                .toUserResponseDTO(user);
    }

    @Test
    void getUserByUsername_shouldThrowWhenUserNotFound() {

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> userService.getUserByUsername("john")
                );

        assertEquals(
                "err.user.notFound",
                exception.getMessage()
        );

        verify(userRepository)
                .findUserByUsername("john");

        verifyNoInteractions(
                userMapper
        );
    }

    // ========================================================================
    // changeAccountStatus
    // ========================================================================

    @Test
    void changeAccountStatus_shouldChangeStatusAndReturnDTO() {

        ChangeAccountStatusRequestDTO request =
                new ChangeAccountStatusRequestDTO(
                        userId,
                        false
                );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toUserResponseDTO(user))
                .thenReturn(userResponse);

        UserResponseDTO result =
                userService.changeAccountStatus(request);

        assertFalse(
                user.isActive()
        );

        assertEquals(
                userResponse,
                result
        );

        verify(userRepository)
                .findById(userId);

        verify(userRepository)
                .save(user);

        verify(userMapper)
                .toUserResponseDTO(user);
    }

    @Test
    void changeAccountStatus_shouldAllowChangingInactiveToActive() {

        user.setActive(false);

        ChangeAccountStatusRequestDTO request =
                new ChangeAccountStatusRequestDTO(
                        userId,
                        true
                );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toUserResponseDTO(user))
                .thenReturn(userResponse);

        UserResponseDTO result =
                userService.changeAccountStatus(request);

        assertTrue(
                user.isActive()
        );

        assertEquals(
                userResponse,
                result
        );

        verify(userRepository)
                .save(user);

        verify(userMapper)
                .toUserResponseDTO(user);
    }

    @Test
    void changeAccountStatus_shouldThrowWhenUserNotFound() {

        ChangeAccountStatusRequestDTO request =
                new ChangeAccountStatusRequestDTO(
                        userId,
                        false
                );

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> userService.changeAccountStatus(request)
                );

        assertEquals(
                "err.user.notFound",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));

        verifyNoInteractions(
                userMapper
        );
    }

    // ========================================================================
    // findUsers
    // ========================================================================

    @Test
    void findUsers_shouldReturnPageOfUserResponseDTO() {

        UserSearchRequestDTO request =
                new UserSearchRequestDTO(
                        "john",
                        null,
                        null,
                        null
                );

        Page<User> userPage =
                new PageImpl<>(
                        List.of(user)
                );

        when(userRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(userPage);

        when(userMapper.toUserResponseDTO(user))
                .thenReturn(userResponse);

        Page<UserResponseDTO> result =
                userService.findUsers(
                        request,
                        0,
                        5
                );

        assertNotNull(result);

        assertEquals(
                1,
                result.getTotalElements()
        );

        assertEquals(
                1,
                result.getContent().size()
        );

        assertEquals(
                userResponse,
                result.getContent().get(0)
        );

        verify(userRepository)
                .findAll(
                        any(Specification.class),
                        any(Pageable.class)
                );

        verify(userMapper)
                .toUserResponseDTO(user);
    }

    @Test
    void findUsers_shouldReturnEmptyPageWhenNoUsersFound() {

        UserSearchRequestDTO request =
                new UserSearchRequestDTO(
                        "unknown",
                        null,
                        null,
                        null
                );

        Page<User> userPage =
                new PageImpl<>(
                        Collections.emptyList()
                );

        when(userRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(userPage);

        Page<UserResponseDTO> result =
                userService.findUsers(
                        request,
                        0,
                        5
                );

        assertNotNull(result);

        assertTrue(
                result.isEmpty()
        );

        assertEquals(
                0,
                result.getTotalElements()
        );

        verify(userRepository)
                .findAll(
                        any(Specification.class),
                        any(Pageable.class)
                );

        verifyNoInteractions(
                userMapper
        );
    }

    // ========================================================================
    // findUsers with sorting
    // ========================================================================

    @Test
    void findUsers_shouldSortByUsernameAscending() {

        UserSearchRequestDTO request =
                new UserSearchRequestDTO(
                        null,
                        null,
                        null,
                        null
                );

        Page<User> userPage =
                new PageImpl<>(
                        List.of(user)
                );

        when(userRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(userPage);

        when(userMapper.toUserResponseDTO(user))
                .thenReturn(userResponse);

        Page<UserResponseDTO> result =
                userService.findUsers(
                        request,
                        0,
                        5,
                        "username",
                        "asc"
                );

        assertNotNull(result);

        assertEquals(
                1,
                result.getTotalElements()
        );

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(
                        Pageable.class
                );

        verify(userRepository)
                .findAll(
                        any(Specification.class),
                        captor.capture()
                );

        Pageable pageable =
                captor.getValue();

        assertEquals(
                0,
                pageable.getPageNumber()
        );

        assertEquals(
                5,
                pageable.getPageSize()
        );

        Sort.Order order =
                pageable.getSort()
                        .getOrderFor("username");

        assertNotNull(order);

        assertEquals(
                Sort.Direction.ASC,
                order.getDirection()
        );

        verify(userMapper)
                .toUserResponseDTO(user);
    }

    @Test
    void findUsers_shouldSortByUsernameDescending() {

        UserSearchRequestDTO request =
                new UserSearchRequestDTO(
                        null,
                        null,
                        null,
                        null
                );

        Page<User> userPage =
                new PageImpl<>(
                        List.of(user)
                );

        when(userRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(userPage);

        when(userMapper.toUserResponseDTO(user))
                .thenReturn(userResponse);

        Page<UserResponseDTO> result =
                userService.findUsers(
                        request,
                        1,
                        10,
                        "username",
                        "desc"
                );

        assertNotNull(result);

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(
                        Pageable.class
                );

        verify(userRepository)
                .findAll(
                        any(Specification.class),
                        captor.capture()
                );

        Pageable pageable =
                captor.getValue();

        assertEquals(
                1,
                pageable.getPageNumber()
        );

        assertEquals(
                10,
                pageable.getPageSize()
        );

        Sort.Order order =
                pageable.getSort()
                        .getOrderFor("username");

        assertNotNull(order);

        assertEquals(
                Sort.Direction.DESC,
                order.getDirection()
        );

        verify(userMapper)
                .toUserResponseDTO(user);
    }

    @Test
    void findUsers_shouldUseDescendingForInvalidDirection() {

        UserSearchRequestDTO request =
                new UserSearchRequestDTO(
                        null,
                        null,
                        null,
                        null
                );

        Page<User> userPage =
                new PageImpl<>(
                        List.of(user)
                );

        when(userRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(userPage);

        when(userMapper.toUserResponseDTO(user))
                .thenReturn(userResponse);

        userService.findUsers(
                request,
                0,
                5,
                "username",
                "invalid"
        );

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(
                        Pageable.class
                );

        verify(userRepository)
                .findAll(
                        any(Specification.class),
                        captor.capture()
                );

        Sort.Order order =
                captor.getValue()
                        .getSort()
                        .getOrderFor("username");

        assertNotNull(order);

        assertEquals(
                Sort.Direction.DESC,
                order.getDirection()
        );

        verify(userMapper)
                .toUserResponseDTO(user);
    }

    @Test
    void findUsers_shouldUseDefaultPageableWhenSortByIsNull() {

        UserSearchRequestDTO request =
                new UserSearchRequestDTO(
                        null,
                        null,
                        null,
                        null
                );

        Page<User> userPage =
                new PageImpl<>(
                        List.of(user)
                );

        when(userRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(userPage);

        when(userMapper.toUserResponseDTO(user))
                .thenReturn(userResponse);

        userService.findUsers(
                request,
                2,
                7,
                null,
                null
        );

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(
                        Pageable.class
                );

        verify(userRepository)
                .findAll(
                        any(Specification.class),
                        captor.capture()
                );

        Pageable pageable =
                captor.getValue();

        assertEquals(
                2,
                pageable.getPageNumber()
        );

        assertEquals(
                7,
                pageable.getPageSize()
        );

        assertTrue(
                pageable.getSort().isUnsorted()
        );

        verify(userMapper)
                .toUserResponseDTO(user);
    }

    @Test
    void findUsers_shouldUseDefaultPageableWhenSortByIsBlank() {

        UserSearchRequestDTO request =
                new UserSearchRequestDTO(
                        null,
                        null,
                        null,
                        null
                );

        Page<User> userPage =
                new PageImpl<>(
                        List.of(user)
                );

        when(userRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(userPage);

        when(userMapper.toUserResponseDTO(user))
                .thenReturn(userResponse);

        userService.findUsers(
                request,
                0,
                5,
                "   ",
                "asc"
        );

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(
                        Pageable.class
                );

        verify(userRepository)
                .findAll(
                        any(Specification.class),
                        captor.capture()
                );

        assertTrue(
                captor.getValue()
                        .getSort()
                        .isUnsorted()
        );

        verify(userMapper)
                .toUserResponseDTO(user);
    }

    @Test
    void findUsers_shouldAlwaysUseUsernameAsSortField() {

        UserSearchRequestDTO request =
                new UserSearchRequestDTO(
                        null,
                        null,
                        null,
                        null
                );

        Page<User> userPage =
                new PageImpl<>(
                        List.of(user)
                );

        when(userRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(userPage);

        when(userMapper.toUserResponseDTO(user))
                .thenReturn(userResponse);

        userService.findUsers(
                request,
                0,
                5,
                "email",
                "asc"
        );

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(
                        Pageable.class
                );

        verify(userRepository)
                .findAll(
                        any(Specification.class),
                        captor.capture()
                );

        Pageable pageable =
                captor.getValue();

        assertNotNull(
                pageable.getSort()
                        .getOrderFor("username")
        );

        assertNull(
                pageable.getSort()
                        .getOrderFor("email")
        );

        verify(userMapper)
                .toUserResponseDTO(user);
    }

    // ========================================================================
    // toggleUserStatus
    // ========================================================================

    @Test
    void toggleUserStatus_shouldSetInactiveWhenCurrentlyActive() {

        user.setActive(true);

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        userService.toggleUserStatus("john");

        assertFalse(
                user.isActive()
        );

        verify(userRepository)
                .save(user);
    }

    @Test
    void toggleUserStatus_shouldSetActiveWhenCurrentlyInactive() {

        user.setActive(false);

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        userService.toggleUserStatus("john");

        assertTrue(
                user.isActive()
        );

        verify(userRepository)
                .save(user);
    }

    @Test
    void toggleUserStatus_shouldThrowWhenUserNotFound() {

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> userService.toggleUserStatus("john")
                );

        assertEquals(
                "err.user.notFound",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    // ========================================================================
    // updateUserProfile
    // ========================================================================

    @Test
    void updateUserProfile_shouldUpdateUsernameAndReturnTrue() {

        UpdateProfileRequestDTO request =
                UpdateProfileRequestDTO.builder()
                        .username("john_new")
                        .email("john@gmail.com")
                        .lastName("Smith")
                        .phoneNumber("+48123456789")
                        .build();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userRepository.findUserByUsername("john_new"))
                .thenReturn(Optional.empty());

        when(userRepository.save(user))
                .thenReturn(user);

        boolean result =
                userService.updateUserProfile(
                        "john",
                        request
                );

        assertTrue(result);

        assertEquals(
                "john_new",
                user.getUsername()
        );

        assertEquals(
                "john@gmail.com",
                user.getEmail()
        );

        assertEquals(
                "Smith",
                user.getLastName()
        );

        assertEquals(
                "+48123456789",
                user.getPhoneNumber()
        );

        verify(userRepository)
                .findUserByUsername("john");

        verify(userRepository)
                .findUserByUsername("john_new");

        verify(userRepository)
                .save(user);
    }

    @Test
    void updateUserProfile_shouldNotChangeUsernameWhenSameUsername() {

        UpdateProfileRequestDTO request =
                UpdateProfileRequestDTO.builder()
                        .username("john")
                        .email("john@gmail.com")
                        .lastName("Smith")
                        .phoneNumber("+48123456789")
                        .build();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        boolean result =
                userService.updateUserProfile(
                        "john",
                        request
                );

        assertFalse(result);

        assertEquals(
                "john",
                user.getUsername()
        );

        verify(userRepository, never())
                .findUserByUsername("john_new");

        verify(userRepository)
                .save(user);
    }

    @Test
    void updateUserProfile_shouldRejectBlankUsername() {

        UpdateProfileRequestDTO request =
                UpdateProfileRequestDTO.builder()
                        .username("   ")
                        .email("john@gmail.com")
                        .lastName("Smith")
                        .phoneNumber("+48123456789")
                        .build();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        boolean result =
                userService.updateUserProfile(
                        "john",
                        request
                );

        assertFalse(result);

        assertEquals(
                "john",
                user.getUsername()
        );

        verify(userRepository, never())
                .findUserByUsername(
                        argThat(name ->
                                name != null
                                        && !"john".equals(name)
                        )
                );

        verify(userRepository)
                .save(user);
    }

    @Test
    void updateUserProfile_shouldRejectAlreadyTakenUsername() {

        User anotherUser =
                User.builder()
                        .id(UUID.randomUUID())
                        .username("john_new")
                        .build();

        UpdateProfileRequestDTO request =
                UpdateProfileRequestDTO.builder()
                        .username("john_new")
                        .email("john@gmail.com")
                        .lastName("Smith")
                        .phoneNumber("+48123456789")
                        .build();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userRepository.findUserByUsername("john_new"))
                .thenReturn(Optional.of(anotherUser));

        IllegalUserArgumentException exception =
                assertThrows(
                        IllegalUserArgumentException.class,
                        () -> userService.updateUserProfile(
                                "john",
                                request
                        )
                );

        assertEquals(
                "err.username.taken",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void updateUserProfile_shouldUpdateEmail() {

        UpdateProfileRequestDTO request =
                UpdateProfileRequestDTO.builder()
                        .username("john")
                        .email("new@gmail.com")
                        .lastName("Smith")
                        .phoneNumber("+48123456789")
                        .build();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userRepository.findByEmail("new@gmail.com"))
                .thenReturn(Optional.empty());

        when(userRepository.save(user))
                .thenReturn(user);

        boolean result =
                userService.updateUserProfile(
                        "john",
                        request
                );

        assertFalse(result);

        assertEquals(
                "new@gmail.com",
                user.getEmail()
        );

        verify(userRepository)
                .findByEmail("new@gmail.com");

        verify(userRepository)
                .save(user);
    }

    @Test
    void updateUserProfile_shouldRejectAlreadyTakenEmail() {

        User anotherUser =
                User.builder()
                        .id(UUID.randomUUID())
                        .email("new@gmail.com")
                        .build();

        UpdateProfileRequestDTO request =
                UpdateProfileRequestDTO.builder()
                        .username("john")
                        .email("new@gmail.com")
                        .lastName("Smith")
                        .phoneNumber("+48123456789")
                        .build();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userRepository.findByEmail("new@gmail.com"))
                .thenReturn(Optional.of(anotherUser));

        IllegalUserArgumentException exception =
                assertThrows(
                        IllegalUserArgumentException.class,
                        () -> userService.updateUserProfile(
                                "john",
                                request
                        )
                );

        assertEquals(
                "err.email.taken",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void updateUserProfile_shouldNotCheckEmailWhenEmailIsSame() {

        UpdateProfileRequestDTO request =
                UpdateProfileRequestDTO.builder()
                        .username("john")
                        .email("john@gmail.com")
                        .lastName("Smith")
                        .phoneNumber("+48123456789")
                        .build();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        userService.updateUserProfile(
                "john",
                request
        );

        verify(userRepository, never())
                .findByEmail(anyString());

        verify(userRepository)
                .save(user);
    }

    @Test
    void updateUserProfile_shouldUpdateLastName() {

        UpdateProfileRequestDTO request =
                UpdateProfileRequestDTO.builder()
                        .username("john")
                        .email("john@gmail.com")
                        .lastName("Johnson")
                        .phoneNumber("+48123456789")
                        .build();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        userService.updateUserProfile(
                "john",
                request
        );

        assertEquals(
                "Johnson",
                user.getLastName()
        );

        verify(userRepository)
                .save(user);
    }

    @Test
    void updateUserProfile_shouldIgnoreBlankLastName() {

        UpdateProfileRequestDTO request =
                UpdateProfileRequestDTO.builder()
                        .username("john")
                        .email("john@gmail.com")
                        .lastName("   ")
                        .phoneNumber("+48123456789")
                        .build();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        userService.updateUserProfile(
                "john",
                request
        );

        assertEquals(
                "Smith",
                user.getLastName()
        );

        verify(userRepository)
                .save(user);
    }

    @Test
    void updateUserProfile_shouldUpdatePhoneNumber() {

        UpdateProfileRequestDTO request =
                UpdateProfileRequestDTO.builder()
                        .username("john")
                        .email("john@gmail.com")
                        .lastName("Smith")
                        .phoneNumber("+380991112233")
                        .build();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        userService.updateUserProfile(
                "john",
                request
        );

        assertEquals(
                "+380991112233",
                user.getPhoneNumber()
        );

        verify(userRepository)
                .save(user);
    }

    @Test
    void updateUserProfile_shouldIgnoreBlankPhoneNumber() {

        UpdateProfileRequestDTO request =
                UpdateProfileRequestDTO.builder()
                        .username("john")
                        .email("john@gmail.com")
                        .lastName("Smith")
                        .phoneNumber("   ")
                        .build();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        userService.updateUserProfile(
                "john",
                request
        );

        assertEquals(
                "+48123456789",
                user.getPhoneNumber()
        );

        verify(userRepository)
                .save(user);
    }

    @Test
    void updateUserProfile_shouldHandleNullOptionalFields() {

        UpdateProfileRequestDTO request =
                UpdateProfileRequestDTO.builder()
                        .username(null)
                        .email(null)
                        .lastName(null)
                        .phoneNumber(null)
                        .build();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        boolean result =
                userService.updateUserProfile(
                        "john",
                        request
                );

        assertFalse(result);

        assertEquals(
                "john",
                user.getUsername()
        );

        assertEquals(
                "john@gmail.com",
                user.getEmail()
        );

        assertEquals(
                "Smith",
                user.getLastName()
        );

        assertEquals(
                "+48123456789",
                user.getPhoneNumber()
        );

        verify(userRepository)
                .save(user);

        verify(userRepository, never())
                .findByEmail(anyString());
    }

    @Test
    void updateUserProfile_shouldThrowWhenCurrentUserNotFound() {

        UpdateProfileRequestDTO request =
                UpdateProfileRequestDTO.builder()
                        .username("john_new")
                        .email("new@gmail.com")
                        .lastName("Johnson")
                        .phoneNumber("+380991112233")
                        .build();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> userService.updateUserProfile(
                                "john",
                                request
                        )
                );

        assertEquals(
                "err.user.notFound",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void updateUserProfile_shouldIgnoreBlankEmail() {

        UpdateProfileRequestDTO request =
                UpdateProfileRequestDTO.builder()
                        .username("john")
                        .email("   ")
                        .lastName("Smith")
                        .phoneNumber("+48123456789")
                        .build();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        boolean result =
                userService.updateUserProfile(
                        "john",
                        request
                );

        assertFalse(result);

        assertEquals(
                "john@gmail.com",
                user.getEmail()
        );

        verify(userRepository, never())
                .findByEmail(anyString());

        verify(userRepository)
                .save(user);
    }

    @Test
    void updateUserProfile_shouldFormatNullOldValue() {

        user.setLastName(null);

        UpdateProfileRequestDTO request =
                UpdateProfileRequestDTO.builder()
                        .username("john")
                        .email("john@gmail.com")
                        .lastName("Johnson")
                        .phoneNumber("+48123456789")
                        .build();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        boolean result =
                userService.updateUserProfile(
                        "john",
                        request
                );

        assertFalse(result);

        assertEquals(
                "Johnson",
                user.getLastName()
        );

        verify(userRepository)
                .save(user);
    }

    @Test
    void updateUserProfile_shouldNotAddChangeWhenLastNameIsSame() {

        UpdateProfileRequestDTO request =
                UpdateProfileRequestDTO.builder()
                        .username("john")
                        .email("john@gmail.com")
                        .lastName("Smith")
                        .phoneNumber("+48123456789")
                        .build();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        boolean result =
                userService.updateUserProfile(
                        "john",
                        request
                );

        assertFalse(result);

        assertEquals(
                "Smith",
                user.getLastName()
        );

        verify(userRepository)
                .save(user);
    }

    @Test
    void updateUserProfile_shouldNotAddChangeWhenPhoneIsSame() {

        UpdateProfileRequestDTO request =
                UpdateProfileRequestDTO.builder()
                        .username("john")
                        .email("john@gmail.com")
                        .lastName("Smith")
                        .phoneNumber("+48123456789")
                        .build();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        boolean result =
                userService.updateUserProfile(
                        "john",
                        request
                );

        assertFalse(result);

        assertEquals(
                "+48123456789",
                user.getPhoneNumber()
        );

        verify(userRepository)
                .save(user);
    }

    // ========================================================================
    // changePassword
    // ========================================================================

    @Test
    void changePassword_shouldChangePassword() {

        ChangePasswordRequestDTO request =
                new ChangePasswordRequestDTO(
                        "old",
                        "newPassword"
                );

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "old",
                "encoded-password"
        )).thenReturn(true);

        when(passwordEncoder.encode(
                "newPassword"
        )).thenReturn(
                "new-encoded"
        );

        userService.changePassword(
                "john",
                request
        );

        assertEquals(
                "new-encoded",
                user.getPassword()
        );

        verify(passwordEncoder)
                .matches(
                        "old",
                        "encoded-password"
                );

        verify(passwordEncoder)
                .encode("newPassword");

        verify(userRepository)
                .save(user);
    }

    @Test
    void changePassword_shouldThrowWhenCurrentPasswordIsIncorrect() {

        ChangePasswordRequestDTO request =
                new ChangePasswordRequestDTO(
                        "wrong",
                        "newPassword"
                );

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrong",
                "encoded-password"
        )).thenReturn(false);

        IllegalUserArgumentException exception =
                assertThrows(
                        IllegalUserArgumentException.class,
                        () -> userService.changePassword(
                                "john",
                                request
                        )
                );

        assertEquals(
                "err.password.current.invalid",
                exception.getMessage()
        );

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void changePassword_shouldThrowWhenUserNotFound() {

        ChangePasswordRequestDTO request =
                new ChangePasswordRequestDTO(
                        "old",
                        "newPassword"
                );

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> userService.changePassword(
                                "john",
                                request
                        )
                );

        assertEquals(
                "err.user.notFound",
                exception.getMessage()
        );

        verifyNoInteractions(
                passwordEncoder
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    // ========================================================================
    // depositBalance
    // ========================================================================

    @Test
    void depositBalance_shouldAddToExistingBalance() {

        DepositBalanceRequestDTO request =
                new DepositBalanceRequestDTO(
                        50.0
                );

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toUserResponseDTO(user))
                .thenReturn(userResponse);

        UserResponseDTO result =
                userService.depositBalance(
                        "john",
                        request
                );

        assertEquals(
                150.0,
                user.getBalance()
        );

        assertEquals(
                userResponse,
                result
        );

        verify(userRepository)
                .save(user);

        verify(userMapper)
                .toUserResponseDTO(user);
    }

    @Test
    void depositBalance_shouldHandleNullBalance() {

        user.setBalance(null);

        DepositBalanceRequestDTO request =
                new DepositBalanceRequestDTO(
                        50.0
                );

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toUserResponseDTO(user))
                .thenReturn(userResponse);

        UserResponseDTO result =
                userService.depositBalance(
                        "john",
                        request
                );

        assertEquals(
                50.0,
                user.getBalance()
        );

        assertEquals(
                userResponse,
                result
        );

        verify(userRepository)
                .save(user);

        verify(userMapper)
                .toUserResponseDTO(user);
    }

    @Test
    void depositBalance_shouldThrowWhenUserNotFound() {

        DepositBalanceRequestDTO request =
                new DepositBalanceRequestDTO(
                        50.0
                );

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> userService.depositBalance(
                                "john",
                                request
                        )
                );

        assertEquals(
                "err.user.notFound",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));

        verifyNoInteractions(
                userMapper
        );
    }

    // ========================================================================
    // depositBalanceByAdmin
    // ========================================================================

    @Test
    void depositBalanceByAdmin_shouldAddToBalance() {

        AdminDepositBalanceRequestDTO request =
                new AdminDepositBalanceRequestDTO(
                        userId,
                        25.0
                );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toUserResponseDTO(user))
                .thenReturn(userResponse);

        UserResponseDTO result =
                userService.depositBalanceByAdmin(
                        request
                );

        assertEquals(
                125.0,
                user.getBalance()
        );

        assertEquals(
                userResponse,
                result
        );

        verify(userRepository)
                .findById(userId);

        verify(userRepository)
                .save(user);

        verify(userMapper)
                .toUserResponseDTO(user);
    }

    @Test
    void depositBalanceByAdmin_shouldHandleNullBalance() {

        user.setBalance(null);

        AdminDepositBalanceRequestDTO request =
                new AdminDepositBalanceRequestDTO(
                        userId,
                        25.0
                );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toUserResponseDTO(user))
                .thenReturn(userResponse);

        UserResponseDTO result =
                userService.depositBalanceByAdmin(
                        request
                );

        assertEquals(
                25.0,
                user.getBalance()
        );

        assertEquals(
                userResponse,
                result
        );

        verify(userRepository)
                .save(user);

        verify(userMapper)
                .toUserResponseDTO(user);
    }

    @Test
    void depositBalanceByAdmin_shouldThrowWhenUserNotFound() {

        AdminDepositBalanceRequestDTO request =
                new AdminDepositBalanceRequestDTO(
                        userId,
                        25.0
                );

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> userService.depositBalanceByAdmin(
                                request
                        )
                );

        assertEquals(
                "err.user.notFound",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));

        verifyNoInteractions(
                userMapper
        );
    }

    // ========================================================================
    // deleteAccount
    // ========================================================================

    @Test
    void deleteAccount_shouldDetachVouchersAndDeleteUser() {

        Voucher voucher1 =
                Voucher.builder()
                        .id(UUID.randomUUID())
                        .user(user)
                        .build();

        Voucher voucher2 =
                Voucher.builder()
                        .id(UUID.randomUUID())
                        .user(user)
                        .build();

        List<Voucher> vouchers =
                List.of(
                        voucher1,
                        voucher2
                );

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(voucherRepository.findAllByUserId(userId))
                .thenReturn(vouchers);

        userService.deleteAccount(
                "john"
        );

        assertNull(
                voucher1.getUser()
        );

        assertNull(
                voucher2.getUser()
        );

        verify(voucherRepository)
                .findAllByUserId(userId);

        verify(voucherRepository)
                .saveAllAndFlush(vouchers);

        verify(userRepository)
                .delete(user);
    }

    @Test
    void deleteAccount_shouldWorkWhenUserHasNoVouchers() {

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));

        when(voucherRepository.findAllByUserId(userId))
                .thenReturn(
                        Collections.emptyList()
                );

        userService.deleteAccount(
                "john"
        );

        verify(voucherRepository)
                .findAllByUserId(userId);

        verify(voucherRepository)
                .saveAllAndFlush(
                        Collections.emptyList()
                );

        verify(userRepository)
                .delete(user);
    }

    @Test
    void deleteAccount_shouldThrowWhenUserNotFound() {

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> userService.deleteAccount(
                                "john"
                        )
                );

        assertEquals(
                "err.user.notFound",
                exception.getMessage()
        );

        verifyNoInteractions(
                voucherRepository
        );

        verify(userRepository, never())
                .delete(any(User.class));
    }
}
