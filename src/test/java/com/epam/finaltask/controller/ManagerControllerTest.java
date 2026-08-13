package com.epam.finaltask.controller;

import com.epam.finaltask.dto.VoucherDTO;
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
class ManagerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VoucherService voucherService;

    @InjectMocks
    private ManagerController managerController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(managerController).build();
    }

    @Test
    void manageVouchers_ReturnsManagerPage() throws Exception {
        Page<VoucherDTO> page = new PageImpl<>(List.of(new VoucherDTO()));
        when(voucherService.findAllVouchersForManager(any(), any(), anyInt(), anyInt(), anyString(), anyString())).thenReturn(page);

        mockMvc.perform(get("/manager/vouchers"))
                .andExpect(status().isOk())
                .andExpect(view().name("manager/vouchers"))
                .andExpect(model().attributeExists("vouchersPage"));
    }

    @Test
    void toggleHotStatus_RedirectsToVouchers() throws Exception {
        mockMvc.perform(post("/manager/vouchers/toggle-hot")
                        .param("id", "123")
                        .param("isHot", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager/vouchers"));

        verify(voucherService).changeHotStatus(eq("123"), any(VoucherDTO.class));
    }
}