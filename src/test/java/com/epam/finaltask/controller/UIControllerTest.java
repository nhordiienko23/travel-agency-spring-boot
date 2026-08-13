package com.epam.finaltask.controller;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.VoucherService;
import com.epam.finaltask.token.JwtUtils;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UIControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VoucherService voucherService;

    @Mock
    private UserService userService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UIController uiController;

    private User testUser;
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(uiController).build();

        testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("testuser");
        testUser.setBalance(5000.0);
    }

    @Test
    void getIndexPage_ReturnsIndexView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void getSignInPage_ReturnsSignInView() throws Exception {
        mockMvc.perform(get("/auth/sign-in"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/sign-in"));
    }

    @Test
    void login_Success_RedirectsToDashboard() throws Exception {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "testuser", "encodedPass", true, true, true, true, Collections.emptyList());

        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(passwordEncoder.matches("password", "encodedPass")).thenReturn(true);
        when(jwtUtils.generateToken("testuser")).thenReturn("fake_jwt_token");

        mockMvc.perform(post("/auth/sign-in")
                        .param("username", "testuser")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(cookie().exists("JWT"))
                .andExpect(cookie().value("JWT", "fake_jwt_token"));
    }

    @Test
    void login_Failure_RedirectsToSignInWithError() throws Exception {
        when(userDetailsService.loadUserByUsername("unknown")).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(post("/auth/sign-in")
                        .param("username", "unknown")
                        .param("password", "wrong"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/sign-in?error"));
    }

    @Test
    void logout_ClearsCookieAndRedirects() throws Exception {
        mockMvc.perform(get("/auth/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(cookie().exists("JWT"))
                .andExpect(cookie().maxAge("JWT", 0)); // Проверка, что куки удаляется
    }

    @Test
    void getDashboardPage_ReturnsDashboardView() throws Exception {
        Page<VoucherDTO> page = new PageImpl<>(List.of(new VoucherDTO()));

        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(voucherService.findAvailableVouchers(any(), any(), any(), any(), any(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(page);
        when(voucherService.findAllByUserId(userId.toString())).thenReturn(List.of(new VoucherDTO()));

        mockMvc.perform(get("/dashboard")
                        .principal(() -> "testuser"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/dashboard"))
                .andExpect(model().attributeExists("vouchersPage"))
                .andExpect(model().attributeExists("myVouchers"))
                .andExpect(model().attributeExists("currentUser"));
    }

    @Test
    void orderVoucher_RedirectsToDashboard() throws Exception {
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/vouchers/order")
                        .param("voucherId", "some_voucher_id")
                        .principal(() -> "testuser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard?ordered"));

        verify(voucherService).order("some_voucher_id", userId.toString());
    }

    @Test
    void getSignUpPage_ReturnsSignUpView() throws Exception {
        mockMvc.perform(get("/auth/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/sign-up"))
                .andExpect(model().attributeExists("userDTO"));
    }

    @Test
    void registerUser_Success_RedirectsToSignIn() throws Exception {
        mockMvc.perform(post("/auth/sign-up")
                        .param("username", "newuser")
                        .param("password", "password123")
                        .param("email", "test@test.com")
                        .param("phoneNumber", "123456789")
                        .param("role", "USER")
                        .param("active", "true")
                        .param("balance", "5000.0")) // <-- Добавили баланс, чтобы пройти валидацию @NotNull
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/sign-in?registered"));

        verify(userService).register(any(UserDTO.class));
    }

    @Test
    void getProfilePage_ReturnsProfileView() throws Exception {
        when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(voucherService.findAllByUserId(userId.toString())).thenReturn(List.of(new VoucherDTO()));

        mockMvc.perform(get("/profile")
                        .principal(() -> "testuser"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("myVouchers"));
    }

    @Test
    void updateProfile_Success_RedirectsToProfile() throws Exception {
        doNothing().when(userService).updateUserProfile(eq("testuser"), any(UserDTO.class));

        mockMvc.perform(post("/profile/update")
                        .principal(() -> "testuser")
                        .param("email", "new@test.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?success"));

        verify(userService).updateUserProfile(eq("testuser"), any(UserDTO.class));
    }

    @Test
    void updateProfile_Error_RedirectsToProfileWithError() throws Exception {
        doThrow(new IllegalArgumentException("Error")).when(userService).updateUserProfile(eq("testuser"), any(UserDTO.class));

        mockMvc.perform(post("/profile/update")
                        .principal(() -> "testuser")
                        .param("password", "12")) // Слишком короткий пароль вызовет ошибку
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?error"));
    }
}