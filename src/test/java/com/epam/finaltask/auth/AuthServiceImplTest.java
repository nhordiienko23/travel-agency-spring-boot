package com.epam.finaltask.auth;

import com.epam.finaltask.core.exception.invalidData.IllegalUserArgumentException;
import com.epam.finaltask.security.JwtUtils;
import com.epam.finaltask.user.Role;
import com.epam.finaltask.user.User;
import com.epam.finaltask.user.UserMapper;
import com.epam.finaltask.user.UserRepository;
import com.epam.finaltask.user.UserResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    private static final String STRONG_PASSWORD = "Password123!";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthServiceImpl authService;

    // ========================================================================
    // REGISTER
    // ========================================================================

    @Test
    void register_ShouldReturnUserResponse_WhenValidData() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("newuser")
                .email("newuser@gmail.com")
                .password(STRONG_PASSWORD)
                .lastName("Smith")
                .phoneNumber("+380123456789")
                .build();

        User savedUser = User.builder()
                .username("newuser")
                .email("newuser@gmail.com")
                .password("encodedPassword")
                .lastName("Smith")
                .phoneNumber("+380123456789")
                .role(Role.USER)
                .balance(null)
                .active(true)
                .build();

        UserResponseDTO expectedResponse = UserResponseDTO.builder()
                .username("newuser")
                .email("newuser@gmail.com")
                .lastName("Smith")
                .phoneNumber("+380123456789")
                .balance(null)
                .role(Role.USER.name())
                .active(true)
                .build();

        when(userRepository.findUserByUsername("newuser"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("newuser@gmail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(STRONG_PASSWORD))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        when(userMapper.toUserResponseDTO(savedUser))
                .thenReturn(expectedResponse);

        UserResponseDTO result = authService.register(request);

        assertNotNull(result);
        assertEquals(expectedResponse, result);

        verify(userRepository).findUserByUsername("newuser");
        verify(userRepository).findByEmail("newuser@gmail.com");
        verify(passwordEncoder).encode(STRONG_PASSWORD);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toUserResponseDTO(savedUser);

        verifyNoMoreInteractions(
                userRepository,
                passwordEncoder,
                userMapper
        );
    }

    @Test
    void register_ShouldCreateUserWithCorrectFields() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("newuser")
                .email("newuser@gmail.com")
                .password(STRONG_PASSWORD)
                .lastName("Smith")
                .phoneNumber("+380123456789")
                .build();

        when(userRepository.findUserByUsername("newuser"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("newuser@gmail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(STRONG_PASSWORD))
                .thenReturn("encodedPassword");

        User savedUser = User.builder()
                .username("newuser")
                .email("newuser@gmail.com")
                .password("encodedPassword")
                .lastName("Smith")
                .phoneNumber("+380123456789")
                .role(Role.USER)
                .balance(null)
                .active(true)
                .build();

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        when(userMapper.toUserResponseDTO(savedUser))
                .thenReturn(mock(UserResponseDTO.class));

        authService.register(request);

        ArgumentCaptor<User> captor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        User createdUser = captor.getValue();

        assertEquals("newuser", createdUser.getUsername());
        assertEquals("newuser@gmail.com", createdUser.getEmail());
        assertEquals("encodedPassword", createdUser.getPassword());
        assertEquals("Smith", createdUser.getLastName());
        assertEquals("+380123456789", createdUser.getPhoneNumber());
        assertEquals(Role.USER, createdUser.getRole());
        assertNull(createdUser.getBalance());
        assertTrue(createdUser.isActive());
    }

    @Test
    void register_ShouldUseEncodedPassword() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("user")
                .email("user@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("1234567890")
                .build();

        when(userRepository.findUserByUsername("user"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("user@gmail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(STRONG_PASSWORD))
                .thenReturn("$2a$encodedPassword");

        User savedUser = User.builder()
                .username("user")
                .email("user@gmail.com")
                .password("$2a$encodedPassword")
                .role(Role.USER)
                .active(true)
                .build();

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        when(userMapper.toUserResponseDTO(savedUser))
                .thenReturn(mock(UserResponseDTO.class));

        authService.register(request);

        verify(passwordEncoder).encode(STRONG_PASSWORD);

        verify(userRepository).save(argThat(user ->
                "$2a$encodedPassword".equals(user.getPassword())
        ));
    }

    @Test
    void register_ShouldThrowException_WhenUsernameAlreadyExists() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("existinguser")
                .email("new@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("+380123456789")
                .build();

        User existingUser = User.builder()
                .username("existinguser")
                .build();

        when(userRepository.findUserByUsername("existinguser"))
                .thenReturn(Optional.of(existingUser));

        IllegalUserArgumentException exception =
                assertThrows(
                        IllegalUserArgumentException.class,
                        () -> authService.register(request)
                );

        assertEquals(
                "err.username.taken",
                exception.getMessage()
        );

        verify(userRepository)
                .findUserByUsername("existinguser");

        verify(userRepository, never())
                .findByEmail(anyString());

        verify(userRepository, never())
                .save(any(User.class));

        verify(passwordEncoder, never())
                .encode(anyString());

        verifyNoInteractions(userMapper);
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("newuser")
                .email("existing@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("+380123456789")
                .build();

        when(userRepository.findUserByUsername("newuser"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("existing@gmail.com"))
                .thenReturn(Optional.of(new User()));

        IllegalUserArgumentException exception =
                assertThrows(
                        IllegalUserArgumentException.class,
                        () -> authService.register(request)
                );

        assertEquals(
                "err.email.taken",
                exception.getMessage()
        );

        verify(userRepository)
                .findUserByUsername("newuser");

        verify(userRepository)
                .findByEmail("existing@gmail.com");

        verify(userRepository, never())
                .save(any(User.class));

        verify(passwordEncoder, never())
                .encode(anyString());

        verifyNoInteractions(userMapper);
    }

    @Test
    void register_ShouldNotSaveUser_WhenUsernameIsDuplicate() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("duplicate")
                .email("new@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("1234567890")
                .build();

        when(userRepository.findUserByUsername("duplicate"))
                .thenReturn(Optional.of(new User()));

        assertThrows(
                IllegalUserArgumentException.class,
                () -> authService.register(request)
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void register_ShouldNotSaveUser_WhenEmailIsDuplicate() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("newuser")
                .email("duplicate@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("1234567890")
                .build();

        when(userRepository.findUserByUsername("newuser"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("duplicate@gmail.com"))
                .thenReturn(Optional.of(new User()));

        assertThrows(
                IllegalUserArgumentException.class,
                () -> authService.register(request)
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void register_ShouldPassSavedUserToMapper() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("user")
                .email("user@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("1234567890")
                .build();

        User savedUser = User.builder()
                .username("user")
                .email("user@gmail.com")
                .role(Role.USER)
                .active(true)
                .build();

        UserResponseDTO response =
                mock(UserResponseDTO.class);

        when(userRepository.findUserByUsername("user"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("user@gmail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(STRONG_PASSWORD))
                .thenReturn("encoded");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        when(userMapper.toUserResponseDTO(savedUser))
                .thenReturn(response);

        UserResponseDTO result =
                authService.register(request);

        assertSame(response, result);

        verify(userMapper)
                .toUserResponseDTO(savedUser);
    }

    // ========================================================================
    // LOGIN
    // ========================================================================

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {

        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("john")
                .password(STRONG_PASSWORD)
                .build();

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername("john")
                        .password("encodedPassword")
                        .authorities("ROLE_USER")
                        .build();

        when(userDetailsService.loadUserByUsername("john"))
                .thenReturn(userDetails);

        when(passwordEncoder.matches(
                STRONG_PASSWORD,
                "encodedPassword"
        )).thenReturn(true);

        when(jwtUtils.generateToken("john"))
                .thenReturn("jwt-token");

        String result = authService.login(request);

        assertEquals("jwt-token", result);

        verify(userDetailsService)
                .loadUserByUsername("john");

        verify(passwordEncoder)
                .matches(STRONG_PASSWORD, "encodedPassword");

        verify(jwtUtils)
                .generateToken("john");
    }

    @Test
    void login_ShouldThrowBadCredentials_WhenUserDoesNotExist() {

        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("unknown")
                .password(STRONG_PASSWORD)
                .build();

        when(userDetailsService.loadUserByUsername("unknown"))
                .thenThrow(
                        new UsernameNotFoundException("User not found")
                );

        BadCredentialsException exception =
                assertThrows(
                        BadCredentialsException.class,
                        () -> authService.login(request)
                );

        assertEquals(
                "err.login.invalid",
                exception.getMessage()
        );

        verify(userDetailsService)
                .loadUserByUsername("unknown");

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());

        verify(jwtUtils, never())
                .generateToken(anyString());
    }

    @Test
    void login_ShouldNotRevealUserExistence_WhenUserDoesNotExist() {

        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("unknown")
                .password(STRONG_PASSWORD)
                .build();

        when(userDetailsService.loadUserByUsername("unknown"))
                .thenThrow(
                        new UsernameNotFoundException("Internal message")
                );

        BadCredentialsException exception =
                assertThrows(
                        BadCredentialsException.class,
                        () -> authService.login(request)
                );

        assertEquals(
                "err.login.invalid",
                exception.getMessage()
        );

        assertFalse(
                exception.getMessage()
                        .contains("unknown")
        );
    }

    @Test
    void login_ShouldThrowBadCredentials_WhenPasswordIsWrong() {

        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("john")
                .password("Wrong123!")
                .build();

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername("john")
                        .password("encodedPassword")
                        .authorities("ROLE_USER")
                        .build();

        when(userDetailsService.loadUserByUsername("john"))
                .thenReturn(userDetails);

        when(passwordEncoder.matches(
                "Wrong123!",
                "encodedPassword"
        )).thenReturn(false);

        BadCredentialsException exception =
                assertThrows(
                        BadCredentialsException.class,
                        () -> authService.login(request)
                );

        assertEquals(
                "err.login.invalid",
                exception.getMessage()
        );

        verify(passwordEncoder)
                .matches("Wrong123!", "encodedPassword");

        verify(jwtUtils, never())
                .generateToken(anyString());
    }

    @Test
    void login_ShouldThrowDisabledException_WhenAccountIsDisabled() {

        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("blocked")
                .password(STRONG_PASSWORD)
                .build();

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername("blocked")
                        .password("encodedPassword")
                        .disabled(true)
                        .authorities("ROLE_USER")
                        .build();

        when(userDetailsService.loadUserByUsername("blocked"))
                .thenReturn(userDetails);

        when(passwordEncoder.matches(
                STRONG_PASSWORD,
                "encodedPassword"
        )).thenReturn(true);

        DisabledException exception =
                assertThrows(
                        DisabledException.class,
                        () -> authService.login(request)
                );

        assertEquals(
                "err.account.blocked",
                exception.getMessage()
        );

        verify(passwordEncoder)
                .matches(STRONG_PASSWORD, "encodedPassword");

        verify(jwtUtils, never())
                .generateToken(anyString());
    }

    @Test
    void login_ShouldGenerateTokenOnlyAfterSuccessfulAuthentication() {

        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("john")
                .password(STRONG_PASSWORD)
                .build();

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername("john")
                        .password("encodedPassword")
                        .authorities("ROLE_USER")
                        .build();

        when(userDetailsService.loadUserByUsername("john"))
                .thenReturn(userDetails);

        when(passwordEncoder.matches(
                STRONG_PASSWORD,
                "encodedPassword"
        )).thenReturn(true);

        when(jwtUtils.generateToken("john"))
                .thenReturn("jwt");

        authService.login(request);

        var inOrder = inOrder(
                userDetailsService,
                passwordEncoder,
                jwtUtils
        );

        inOrder.verify(userDetailsService)
                .loadUserByUsername("john");

        inOrder.verify(passwordEncoder)
                .matches(STRONG_PASSWORD, "encodedPassword");

        inOrder.verify(jwtUtils)
                .generateToken("john");
    }

    @Test
    void login_ShouldNotGenerateToken_WhenPasswordIsWrong() {

        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("john")
                .password("Wrong123!")
                .build();

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername("john")
                        .password("encoded")
                        .authorities("ROLE_USER")
                        .build();

        when(userDetailsService.loadUserByUsername("john"))
                .thenReturn(userDetails);

        when(passwordEncoder.matches(
                "Wrong123!",
                "encoded"
        )).thenReturn(false);

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(jwtUtils, never())
                .generateToken(anyString());
    }

    @Test
    void login_ShouldNotGenerateToken_WhenAccountIsDisabled() {

        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("john")
                .password(STRONG_PASSWORD)
                .build();

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername("john")
                        .password("encoded")
                        .disabled(true)
                        .authorities("ROLE_USER")
                        .build();

        when(userDetailsService.loadUserByUsername("john"))
                .thenReturn(userDetails);

        when(passwordEncoder.matches(
                STRONG_PASSWORD,
                "encoded"
        )).thenReturn(true);

        assertThrows(
                DisabledException.class,
                () -> authService.login(request)
        );

        verify(jwtUtils, never())
                .generateToken(anyString());
    }

    // ========================================================================
    // REFRESH TOKEN
    // ========================================================================

    @Test
    void refreshToken_ShouldReturnGeneratedToken() {

        String username = "john";
        String expectedToken = "new-jwt-token";

        when(jwtUtils.generateToken(username))
                .thenReturn(expectedToken);

        String result =
                authService.refreshToken(username);

        assertEquals(expectedToken, result);

        verify(jwtUtils)
                .generateToken(username);
    }

    @Test
    void refreshToken_ShouldGenerateTokenForRequestedUsername() {

        String username = "specificUser";

        when(jwtUtils.generateToken(username))
                .thenReturn("token");

        authService.refreshToken(username);

        verify(jwtUtils)
                .generateToken(eq(username));

        verifyNoMoreInteractions(jwtUtils);
    }

    @Test
    void refreshToken_ShouldPropagateException_WhenTokenGenerationFails() {

        String username = "john";

        when(jwtUtils.generateToken(username))
                .thenThrow(
                        new IllegalStateException(
                                "JWT generation failed"
                        )
                );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> authService.refreshToken(username)
                );

        assertEquals(
                "JWT generation failed",
                exception.getMessage()
        );

        verify(jwtUtils)
                .generateToken(username);
    }

    // ========================================================================
    // EDGE CASES
    // ========================================================================

    @Test
    void register_ShouldAllowNullLastName() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .password(STRONG_PASSWORD)
                .lastName(null)
                .phoneNumber("1234567890")
                .build();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(STRONG_PASSWORD))
                .thenReturn("encoded");

        User savedUser = User.builder()
                .username("john")
                .email("john@gmail.com")
                .password("encoded")
                .lastName(null)
                .role(Role.USER)
                .active(true)
                .build();

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        when(userMapper.toUserResponseDTO(savedUser))
                .thenReturn(mock(UserResponseDTO.class));

        assertDoesNotThrow(
                () -> authService.register(request)
        );

        verify(userRepository).save(argThat(user ->
                user.getLastName() == null
        ));
    }

    @Test
    void register_ShouldKeepPhoneNumberWithoutModification() {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .password(STRONG_PASSWORD)
                .phoneNumber("+48123456789")
                .build();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(STRONG_PASSWORD))
                .thenReturn("encoded");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(userMapper.toUserResponseDTO(any(User.class)))
                .thenReturn(mock(UserResponseDTO.class));

        authService.register(request);

        verify(userRepository).save(argThat(user ->
                "+48123456789".equals(user.getPhoneNumber())
        ));
    }

    @Test
    void login_ShouldWorkWithDifferentAuthority() {

        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("admin")
                .password(STRONG_PASSWORD)
                .build();

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername("admin")
                        .password("encoded")
                        .disabled(false)
                        .authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        )
                        .build();

        when(userDetailsService.loadUserByUsername("admin"))
                .thenReturn(userDetails);

        when(passwordEncoder.matches(
                STRONG_PASSWORD,
                "encoded"
        )).thenReturn(true);

        when(jwtUtils.generateToken("admin"))
                .thenReturn("admin-token");

        String result =
                authService.login(request);

        assertEquals(
                "admin-token",
                result
        );

        assertTrue(
                userDetails.getAuthorities()
                        .contains(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        )
        );
    }
}

