package com.epam.finaltask.auth;

import com.epam.finaltask.user.Role;
import com.epam.finaltask.user.UserResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationRestControllerTest {

    private static final String STRONG_PASSWORD = "Password123!";

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        AuthenticationRestController controller =
                new AuthenticationRestController(authService);

        LocalValidatorFactoryBean validator =
                new LocalValidatorFactoryBean();

        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setValidator(validator)
                .build();
    }

    // ========================================================================
    // REGISTER - HAPPY PATH
    // ========================================================================

    @Test
    void register_ShouldReturnCreated_WhenRequestIsValid()
            throws Exception {

        UserResponseDTO response = UserResponseDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .lastName("Smith")
                .phoneNumber("+48123456789")
                .balance(null)
                .role(Role.USER.name())
                .active(true)
                .build();

        when(authService.register(any(RegisterRequestDTO.class)))
                .thenReturn(response);

        String json = """
                {
                    "username": "john",
                    "email": "john@gmail.com",
                    "password": "Password123!",
                    "lastName": "Smith",
                    "phoneNumber": "+48123456789"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(jsonPath("$.statusCode")
                        .value("OK"))
                .andExpect(jsonPath("$.statusMessage")
                        .value("User registered successfully"))
                .andExpect(jsonPath("$.results.username")
                        .value("john"))
                .andExpect(jsonPath("$.results.email")
                        .value("john@gmail.com"))
                .andExpect(jsonPath("$.results.lastName")
                        .value("Smith"))
                .andExpect(jsonPath("$.results.phoneNumber")
                        .value("+48123456789"))
                .andExpect(jsonPath("$.results.role")
                        .value("USER"))
                .andExpect(jsonPath("$.results.active")
                        .value(true));

        verify(authService).register(any(RegisterRequestDTO.class));
    }

    // ========================================================================
    // REGISTER - REQUEST
    // ========================================================================

    @Test
    void register_ShouldPassCorrectRequestToService()
            throws Exception {

        UserResponseDTO response = UserResponseDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .role(Role.USER.name())
                .active(true)
                .build();

        when(authService.register(any(RegisterRequestDTO.class)))
                .thenReturn(response);

        String json = """
                {
                    "username": "john",
                    "email": "john@gmail.com",
                    "password": "Password123!",
                    "lastName": "Smith",
                    "phoneNumber": "1234567890"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        verify(authService).register(argThat(request ->
                "john".equals(request.username())
                        && "john@gmail.com".equals(request.email())
                        && STRONG_PASSWORD.equals(request.password())
                        && "Smith".equals(request.lastName())
                        && "1234567890".equals(request.phoneNumber())
        ));
    }

    // ========================================================================
    // REGISTER - VALIDATION
    // ========================================================================

    @Test
    void register_ShouldReturnBadRequest_WhenUsernameIsBlank()
            throws Exception {

        String json = """
                {
                    "username": "",
                    "email": "john@gmail.com",
                    "password": "Password123!",
                    "phoneNumber": "1234567890"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void register_ShouldAcceptUsernameWithOneCharacter()
            throws Exception {

        UserResponseDTO response = UserResponseDTO.builder()
                .username("a")
                .email("a@gmail.com")
                .role(Role.USER.name())
                .active(true)
                .build();

        when(authService.register(any(RegisterRequestDTO.class)))
                .thenReturn(response);

        String json = """
                {
                    "username": "a",
                    "email": "a@gmail.com",
                    "password": "Password123!",
                    "phoneNumber": "1234567890"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isCreated());

        verify(authService).register(any(RegisterRequestDTO.class));
    }

    @Test
    void register_ShouldAcceptUsernameWithTwoCharacters()
            throws Exception {

        UserResponseDTO response = UserResponseDTO.builder()
                .username("ab")
                .email("ab@gmail.com")
                .role(Role.USER.name())
                .active(true)
                .build();

        when(authService.register(any(RegisterRequestDTO.class)))
                .thenReturn(response);

        String json = """
                {
                    "username": "ab",
                    "email": "ab@gmail.com",
                    "password": "Password123!",
                    "phoneNumber": "1234567890"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isCreated());

        verify(authService).register(any(RegisterRequestDTO.class));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenUsernameIsTooLong()
            throws Exception {

        String username = "a".repeat(51);

        String json = """
                {
                    "username": "%s",
                    "email": "john@gmail.com",
                    "password": "Password123!",
                    "phoneNumber": "1234567890"
                }
                """.formatted(username);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void register_ShouldAcceptUsernameWithExactlyThreeCharacters()
            throws Exception {

        UserResponseDTO response = UserResponseDTO.builder()
                .username("abc")
                .email("abc@gmail.com")
                .role(Role.USER.name())
                .active(true)
                .build();

        when(authService.register(any(RegisterRequestDTO.class)))
                .thenReturn(response);

        String json = """
                {
                    "username": "abc",
                    "email": "abc@gmail.com",
                    "password": "Password123!",
                    "phoneNumber": "1234567890"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isCreated());

        verify(authService).register(any(RegisterRequestDTO.class));
    }

    @Test
    void register_ShouldAcceptUsernameWithExactlyFiftyCharacters()
            throws Exception {

        String username = "a".repeat(50);

        UserResponseDTO response = UserResponseDTO.builder()
                .username(username)
                .email("john@gmail.com")
                .role(Role.USER.name())
                .active(true)
                .build();

        when(authService.register(any(RegisterRequestDTO.class)))
                .thenReturn(response);

        String json = """
                {
                    "username": "%s",
                    "email": "john@gmail.com",
                    "password": "Password123!",
                    "phoneNumber": "1234567890"
                }
                """.formatted(username);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isCreated());

        verify(authService).register(any(RegisterRequestDTO.class));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenEmailIsInvalid()
            throws Exception {

        String json = """
                {
                    "username": "john",
                    "email": "invalid-email",
                    "password": "Password123!",
                    "phoneNumber": "1234567890"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void register_ShouldReturnBadRequest_WhenEmailIsBlank()
            throws Exception {

        String json = """
                {
                    "username": "john",
                    "email": "",
                    "password": "Password123!",
                    "phoneNumber": "1234567890"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void register_ShouldReturnBadRequest_WhenPasswordIsBlank()
            throws Exception {

        String json = """
                {
                    "username": "john",
                    "email": "john@gmail.com",
                    "password": "",
                    "phoneNumber": "1234567890"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void register_ShouldReturnBadRequest_WhenPasswordIsTooShort()
            throws Exception {

        String json = """
                {
                    "username": "john",
                    "email": "john@gmail.com",
                    "password": "Ab1!",
                    "phoneNumber": "1234567890"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void register_ShouldReturnBadRequest_WhenPasswordHasNoUppercase()
            throws Exception {

        String json = """
                {
                    "username": "john",
                    "email": "john@gmail.com",
                    "password": "password123!",
                    "phoneNumber": "1234567890"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void register_ShouldReturnBadRequest_WhenPasswordHasNoLowercase()
            throws Exception {

        String json = """
                {
                    "username": "john",
                    "email": "john@gmail.com",
                    "password": "PASSWORD123!",
                    "phoneNumber": "1234567890"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void register_ShouldReturnBadRequest_WhenPasswordHasNoDigit()
            throws Exception {

        String json = """
                {
                    "username": "john",
                    "email": "john@gmail.com",
                    "password": "Password!",
                    "phoneNumber": "1234567890"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void register_ShouldReturnBadRequest_WhenPasswordHasNoSpecialCharacter()
            throws Exception {

        String json = """
                {
                    "username": "john",
                    "email": "john@gmail.com",
                    "password": "Password1234",
                    "phoneNumber": "1234567890"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void register_ShouldAcceptStrongPasswordWithExactlyEightCharacters()
            throws Exception {

        UserResponseDTO response = UserResponseDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .role(Role.USER.name())
                .active(true)
                .build();

        when(authService.register(any(RegisterRequestDTO.class)))
                .thenReturn(response);

        String json = """
                {
                    "username": "john",
                    "email": "john@gmail.com",
                    "password": "Abcd123!",
                    "phoneNumber": "1234567890"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isCreated());

        verify(authService).register(any(RegisterRequestDTO.class));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenPhoneIsInvalid()
            throws Exception {

        String json = """
                {
                    "username": "john",
                    "email": "john@gmail.com",
                    "password": "Password123!",
                    "phoneNumber": "123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void register_ShouldAcceptPhoneWithTenDigits()
            throws Exception {

        UserResponseDTO response = UserResponseDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .role(Role.USER.name())
                .active(true)
                .build();

        when(authService.register(any(RegisterRequestDTO.class)))
                .thenReturn(response);

        String json = """
                {
                    "username": "john",
                    "email": "john@gmail.com",
                    "password": "Password123!",
                    "phoneNumber": "1234567890"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isCreated());

        verify(authService).register(any(RegisterRequestDTO.class));
    }

    @Test
    void register_ShouldAcceptPhoneWithFifteenDigits()
            throws Exception {

        UserResponseDTO response = UserResponseDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .role(Role.USER.name())
                .active(true)
                .build();

        when(authService.register(any(RegisterRequestDTO.class)))
                .thenReturn(response);

        String json = """
                {
                    "username": "john",
                    "email": "john@gmail.com",
                    "password": "Password123!",
                    "phoneNumber": "123456789012345"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isCreated());

        verify(authService).register(any(RegisterRequestDTO.class));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenPhoneHasSixteenDigits()
            throws Exception {

        String json = """
                {
                    "username": "john",
                    "email": "john@gmail.com",
                    "password": "Password123!",
                    "phoneNumber": "1234567890123456"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void register_ShouldReturnBadRequest_WhenRequiredFieldsAreMissing()
            throws Exception {

        String json = """
                {
                    "lastName": "Smith"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void register_ShouldAllowNullLastName()
            throws Exception {

        UserResponseDTO response = UserResponseDTO.builder()
                .username("john")
                .email("john@gmail.com")
                .role(Role.USER.name())
                .active(true)
                .build();

        when(authService.register(any(RegisterRequestDTO.class)))
                .thenReturn(response);

        String json = """
                {
                    "username": "john",
                    "email": "john@gmail.com",
                    "password": "Password123!",
                    "phoneNumber": "1234567890"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isCreated());

        verify(authService).register(argThat(request ->
                request.lastName() == null
        ));
    }

    // ========================================================================
    // LOGIN - HAPPY PATH
    // ========================================================================

    @Test
    void login_ShouldReturnOk_WhenCredentialsAreValid()
            throws Exception {

        when(authService.login(any(LoginRequestDTO.class)))
                .thenReturn("jwt-token");

        String json = """
                {
                    "username": "john",
                    "password": "Password123!"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(jsonPath("$.statusCode")
                        .value("OK"))
                .andExpect(jsonPath("$.statusMessage")
                        .value("Login successful"))
                .andExpect(jsonPath("$.results")
                        .value("jwt-token"));

        verify(authService).login(any(LoginRequestDTO.class));
    }

    // ========================================================================
    // LOGIN - REQUEST
    // ========================================================================

    @Test
    void login_ShouldPassCorrectRequestToService()
            throws Exception {

        when(authService.login(any(LoginRequestDTO.class)))
                .thenReturn("jwt-token");

        String json = """
                {
                    "username": "admin",
                    "password": "Password123!"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        verify(authService).login(argThat(request ->
                "admin".equals(request.username())
                        && "Password123!".equals(request.password())
        ));
    }

    // ========================================================================
    // LOGIN - VALIDATION
    // ========================================================================

    @Test
    void login_ShouldReturnBadRequest_WhenUsernameIsBlank()
            throws Exception {

        String json = """
                {
                    "username": "",
                    "password": "Password123!"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void login_ShouldReturnBadRequest_WhenUsernameIsNull()
            throws Exception {

        String json = """
                {
                    "username": null,
                    "password": "Password123!"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void login_ShouldReturnBadRequest_WhenPasswordIsBlank()
            throws Exception {

        String json = """
                {
                    "username": "john",
                    "password": ""
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void login_ShouldReturnBadRequest_WhenPasswordIsNull()
            throws Exception {

        String json = """
                {
                    "username": "john",
                    "password": null
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void login_ShouldReturnBadRequest_WhenRequiredFieldsAreMissing()
            throws Exception {

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void login_ShouldReturnBadRequest_WhenRequestBodyIsInvalid()
            throws Exception {

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("not-json"))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }
}

