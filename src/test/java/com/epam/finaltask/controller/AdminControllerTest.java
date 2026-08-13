package com.epam.finaltask.controller;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.User;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.VoucherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private VoucherService voucherService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    void manageUsers_ReturnsUsersPage() throws Exception {
        Page<User> userPage = new PageImpl<>(List.of(new User()));
        when(userService.findUsers(anyString(), anyInt(), anyInt())).thenReturn(userPage);

        mockMvc.perform(get("/admin/users").param("page", "0").param("keyword", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attributeExists("userPage"));
    }

    @Test
    void toggleUserStatus_RedirectsToUsersPage() throws Exception {
        mockMvc.perform(post("/admin/users/toggle-status")
                        .param("username", "testuser")
                        .param("page", "0")
                        .param("keyword", "test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users?page=0&keyword=test"));

        verify(userService).toggleUserStatus("testuser");
    }

    @Test
    void createVoucher_RedirectsToVouchersPage() throws Exception {
        mockMvc.perform(post("/admin/vouchers/create")
                        .param("title", "New Tour"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/vouchers"));

        verify(voucherService).create(any(VoucherDTO.class));
    }
}