package com.epam.finaltask.auth;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private MessageSource messageSource;

    private MockMvc mockMvc;

    private AuthController authController;

    @BeforeEach
    void setUp() {

        authController = new AuthController(
                authService,
                messageSource
        );

        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .build();
    }

    // ========================================================================
    // GET SIGN-IN
    // ========================================================================

    @Test
    void getSignInPage_ShouldReturnSignInView() throws Exception {

        mockMvc.perform(get("/auth/sign-in"))

                .andExpect(status().isOk())
                .andExpect(view().name("auth/sign-in"));
    }

    // ========================================================================
    // LOGIN
    // ========================================================================

    @Test
    void login_ShouldRedirectToDashboard_WhenCredentialsAreValid()
            throws Exception {

        when(authService.login(any(LoginRequestDTO.class)))
                .thenReturn("jwt-token");

        mockMvc.perform(post("/auth/sign-in")
                        .param("username", "john")
                        .param("password", "Password123!"))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(cookie().value("JWT", "jwt-token"))
                .andExpect(cookie().httpOnly("JWT", true))
                .andExpect(cookie().path("JWT", "/"))
                .andExpect(cookie().maxAge("JWT", 86400));

        verify(authService).login(argThat(request ->
                "john".equals(request.username())
                        && "Password123!".equals(request.password())
        ));
    }

    @Test
    void login_ShouldRedirectToBlockedPage_WhenAccountIsDisabled()
            throws Exception {

        when(authService.login(any(LoginRequestDTO.class)))
                .thenThrow(
                        new org.springframework.security.authentication.DisabledException(
                                "err.account.blocked"
                        )
                );

        mockMvc.perform(post("/auth/sign-in")
                        .param("username", "blocked")
                        .param("password", "Password123!"))

                .andExpect(status().is3xxRedirection())
                .andExpect(
                        redirectedUrl("/auth/sign-in?blocked")
                );

        verify(authService).login(any(LoginRequestDTO.class));
    }

    @Test
    void login_ShouldRedirectToErrorPage_WhenCredentialsAreInvalid()
            throws Exception {

        when(authService.login(any(LoginRequestDTO.class)))
                .thenThrow(
                        new org.springframework.security.authentication.BadCredentialsException(
                                "err.login.invalid"
                        )
                );

        mockMvc.perform(post("/auth/sign-in")
                        .param("username", "john")
                        .param("password", "Wrong123!"))

                .andExpect(status().is3xxRedirection())
                .andExpect(
                        redirectedUrl("/auth/sign-in?error")
                );

        verify(authService).login(any(LoginRequestDTO.class));
    }

    @Test
    void login_ShouldPassCorrectCredentialsToService()
            throws Exception {

        when(authService.login(any(LoginRequestDTO.class)))
                .thenReturn("token");

        mockMvc.perform(post("/auth/sign-in")
                .param("username", "testUser")
                .param("password", "Password123!"));

        verify(authService).login(argThat(request ->
                "testUser".equals(request.username())
                        && "Password123!".equals(request.password())
        ));
    }

    @Test
    void login_ShouldCreateCorrectJwtCookie()
            throws Exception {

        when(authService.login(any(LoginRequestDTO.class)))
                .thenReturn("secure-token");

        MvcResult result =
                mockMvc.perform(
                                post("/auth/sign-in")
                                        .param("username", "john")
                                        .param("password", "Password123!")
                        )
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/dashboard"))
                        .andReturn();

        Cookie cookie =
                result.getResponse().getCookie("JWT");

        assertNotNull(cookie);
        assertEquals("JWT", cookie.getName());
        assertEquals("secure-token", cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertEquals("/", cookie.getPath());
        assertEquals(86400, cookie.getMaxAge());
    }

    // ========================================================================
    // LOGOUT
    // ========================================================================

    @Test
    void logout_ShouldDeleteJwtCookie()
            throws Exception {

        MvcResult result =
                mockMvc.perform(get("/auth/logout"))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/"))
                        .andReturn();

        Cookie cookie =
                result.getResponse().getCookie("JWT");

        assertNotNull(cookie);
        assertEquals("JWT", cookie.getName());
        assertEquals(0, cookie.getMaxAge());
        assertEquals("/", cookie.getPath());
        assertTrue(cookie.isHttpOnly());

        verifyNoInteractions(authService);
        verifyNoInteractions(messageSource);
    }

    // ========================================================================
    // GET SIGN-UP
    // ========================================================================

    @Test
    void getSignUpPage_ShouldReturnSignUpViewAndAddRequest()
            throws Exception {

        mockMvc.perform(get("/auth/sign-up"))

                .andExpect(status().isOk())
                .andExpect(view().name("auth/sign-up"))
                .andExpect(model().attributeExists("registerRequest"))
                .andExpect(model().attribute(
                        "registerRequest",
                        org.hamcrest.Matchers.instanceOf(
                                RegisterRequestDTO.class
                        )
                ));

        verifyNoInteractions(authService);
        verifyNoInteractions(messageSource);
    }

    // ========================================================================
    // REGISTER
    // ========================================================================

    @Test
    void registerUser_ShouldRedirectToLogin_WhenRegistrationSucceeds() {

        RegisterRequestDTO request =
                RegisterRequestDTO.builder()
                        .username("john")
                        .email("john@gmail.com")
                        .password("Password123!")
                        .lastName("Smith")
                        .phoneNumber("+48123456789")
                        .build();

        BindingResult bindingResult =
                mock(BindingResult.class);

        Model model =
                mock(Model.class);

        when(bindingResult.hasErrors())
                .thenReturn(false);

        when(authService.register(request))
                .thenReturn(null);

        String result =
                authController.registerUser(
                        request,
                        bindingResult,
                        model
                );

        assertEquals(
                "redirect:/auth/sign-in?registered",
                result
        );

        verify(authService).register(request);
        verifyNoInteractions(model);
        verifyNoInteractions(messageSource);
    }

    @Test
    void registerUser_ShouldReturnSignUpPage_WhenValidationFails() {

        RegisterRequestDTO request =
                RegisterRequestDTO.builder()
                        .build();

        BindingResult bindingResult =
                mock(BindingResult.class);

        Model model =
                mock(Model.class);

        when(bindingResult.hasErrors())
                .thenReturn(true);

        String result =
                authController.registerUser(
                        request,
                        bindingResult,
                        model
                );

        assertEquals(
                "auth/sign-up",
                result
        );

        verifyNoInteractions(authService);
        verifyNoInteractions(model);
        verifyNoInteractions(messageSource);
    }

    @Test
    void registerUser_ShouldReturnSignUpPageAndAddTranslatedUsernameError() {

        RegisterRequestDTO request =
                RegisterRequestDTO.builder()
                        .username("existing")
                        .email("existing@gmail.com")
                        .password("Password123!")
                        .phoneNumber("1234567890")
                        .build();

        BindingResult bindingResult =
                mock(BindingResult.class);

        Model model =
                mock(Model.class);

        when(bindingResult.hasErrors())
                .thenReturn(false);

        when(authService.register(request))
                .thenThrow(
                        new IllegalArgumentException(
                                "err.username.taken"
                        )
                );

        when(messageSource.getMessage(
                eq("err.username.taken"),
                isNull(),
                eq("err.username.taken"),
                any(Locale.class)
        )).thenReturn("Username is already taken");

        String result =
                authController.registerUser(
                        request,
                        bindingResult,
                        model
                );

        assertEquals(
                "auth/sign-up",
                result
        );

        verify(messageSource).getMessage(
                eq("err.username.taken"),
                isNull(),
                eq("err.username.taken"),
                any(Locale.class)
        );

        verify(model).addAttribute(
                "registrationError",
                "Username is already taken"
        );

        verify(authService).register(request);
    }

    @Test
    void registerUser_ShouldHandleEmailDuplicateError() {

        RegisterRequestDTO request =
                RegisterRequestDTO.builder()
                        .username("john")
                        .email("duplicate@gmail.com")
                        .password("Password123!")
                        .phoneNumber("1234567890")
                        .build();

        BindingResult bindingResult =
                mock(BindingResult.class);

        Model model =
                mock(Model.class);

        when(bindingResult.hasErrors())
                .thenReturn(false);

        when(authService.register(request))
                .thenThrow(
                        new IllegalArgumentException(
                                "err.email.taken"
                        )
                );

        when(messageSource.getMessage(
                eq("err.email.taken"),
                isNull(),
                eq("err.email.taken"),
                any(Locale.class)
        )).thenReturn("Email is already taken");

        String result =
                authController.registerUser(
                        request,
                        bindingResult,
                        model
                );

        assertEquals(
                "auth/sign-up",
                result
        );

        verify(messageSource).getMessage(
                eq("err.email.taken"),
                isNull(),
                eq("err.email.taken"),
                any(Locale.class)
        );

        verify(model).addAttribute(
                "registrationError",
                "Email is already taken"
        );

        verify(authService).register(request);
    }

    @Test
    void registerUser_ShouldUseFallbackMessage_WhenMessageKeyIsNotFound() {

        RegisterRequestDTO request =
                RegisterRequestDTO.builder()
                        .username("existing")
                        .email("existing@gmail.com")
                        .password("Password123!")
                        .phoneNumber("1234567890")
                        .build();

        BindingResult bindingResult =
                mock(BindingResult.class);

        Model model =
                mock(Model.class);

        when(bindingResult.hasErrors())
                .thenReturn(false);

        when(authService.register(request))
                .thenThrow(
                        new IllegalArgumentException(
                                "unknown.error.key"
                        )
                );

        when(messageSource.getMessage(
                eq("unknown.error.key"),
                isNull(),
                eq("unknown.error.key"),
                any(Locale.class)
        )).thenReturn("unknown.error.key");

        String result =
                authController.registerUser(
                        request,
                        bindingResult,
                        model
                );

        assertEquals(
                "auth/sign-up",
                result
        );

        verify(model).addAttribute(
                "registrationError",
                "unknown.error.key"
        );
    }

    @Test
    void registerUser_ShouldNotCallService_WhenBindingHasErrors() {

        RegisterRequestDTO request =
                RegisterRequestDTO.builder()
                        .username("")
                        .email("invalid")
                        .password("")
                        .phoneNumber("")
                        .build();

        BindingResult bindingResult =
                mock(BindingResult.class);

        Model model =
                mock(Model.class);

        when(bindingResult.hasErrors())
                .thenReturn(true);

        String result =
                authController.registerUser(
                        request,
                        bindingResult,
                        model
                );

        assertEquals(
                "auth/sign-up",
                result
        );

        verifyNoInteractions(authService);
        verifyNoInteractions(messageSource);
    }
}

