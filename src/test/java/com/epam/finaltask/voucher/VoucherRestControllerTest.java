package com.epam.finaltask.voucher;

import com.epam.finaltask.core.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoucherRestControllerTest {

    @Mock
    private VoucherService voucherService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private VoucherRestController controller;

    @BeforeEach
    void setUp() {
        when(messageSource.getMessage(
                anyString(),
                isNull(),
                anyString(),
                any(Locale.class)
        )).thenAnswer(invocation ->
                invocation.getArgument(0, String.class)
        );
    }

    @Test
    void findAll_shouldReturnList() {
        List<VoucherDTO> list = List.of(
                VoucherDTO.builder()
                        .title("Trip")
                        .build()
        );

        when(voucherService.findAll())
                .thenReturn(list);

        var response = controller.findAll();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        ApiResponse<List<VoucherDTO>> body = response.getBody();

        assertNotNull(body);
        assertEquals("msg.vouchers.fetched", body.getStatusMessage());
        assertEquals(list, body.getResults());

        verify(voucherService).findAll();
        verify(messageSource).getMessage(
                eq("msg.vouchers.fetched"),
                isNull(),
                eq("msg.vouchers.fetched"),
                any(Locale.class)
        );
    }

    @Test
    void findAllByUserId_shouldReturnList() {
        List<VoucherDTO> list = List.of(
                VoucherDTO.builder()
                        .title("Trip")
                        .build()
        );

        when(voucherService.findAllByUserId("user-id"))
                .thenReturn(list);

        var response = controller.findAllByUserId("user-id");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        ApiResponse<List<VoucherDTO>> body = response.getBody();

        assertNotNull(body);
        assertEquals("msg.vouchers.userFetched", body.getStatusMessage());
        assertEquals(list, body.getResults());

        verify(voucherService).findAllByUserId("user-id");
        verify(messageSource).getMessage(
                eq("msg.vouchers.userFetched"),
                isNull(),
                eq("msg.vouchers.userFetched"),
                any(Locale.class)
        );
    }

    @Test
    void createVoucher_shouldReturnCreated() {
        CreateVoucherRequestDTO request =
                CreateVoucherRequestDTO.builder()
                        .title("Trip")
                        .build();

        VoucherDTO dto = VoucherDTO.builder()
                .title("Trip")
                .build();

        when(voucherService.create(request))
                .thenReturn(dto);

        var response = controller.createVoucher(request);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());

        ApiResponse<VoucherDTO> body = response.getBody();

        assertNotNull(body);
        assertEquals("msg.tour.created", body.getStatusMessage());
        assertEquals(dto, body.getResults());

        verify(voucherService).create(request);
        verify(messageSource).getMessage(
                eq("msg.tour.created"),
                isNull(),
                eq("msg.tour.created"),
                any(Locale.class)
        );
    }

    @Test
    void updateVoucher_shouldReturnOk() {
        UpdateVoucherRequestDTO request =
                UpdateVoucherRequestDTO.builder()
                        .title("Updated Trip")
                        .build();

        VoucherDTO dto = VoucherDTO.builder()
                .title("Updated Trip")
                .build();

        when(voucherService.update("voucher-id", request))
                .thenReturn(dto);

        var response = controller.updateVoucher(
                "voucher-id",
                request
        );

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        ApiResponse<VoucherDTO> body = response.getBody();

        assertNotNull(body);
        assertEquals("msg.tour.updated", body.getStatusMessage());
        assertEquals(dto, body.getResults());

        verify(voucherService)
                .update("voucher-id", request);

        verify(messageSource).getMessage(
                eq("msg.tour.updated"),
                isNull(),
                eq("msg.tour.updated"),
                any(Locale.class)
        );
    }

    @Test
    void deleteVoucherById_shouldReturnOk() {
        var response =
                controller.deleteVoucherById("voucher-id");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        ApiResponse<Void> body = response.getBody();

        assertNotNull(body);
        assertEquals("msg.tour.deleted", body.getStatusMessage());
        assertEquals(null, body.getResults());

        verify(voucherService)
                .delete("voucher-id");

        verify(messageSource).getMessage(
                eq("msg.tour.deleted"),
                isNull(),
                eq("msg.tour.deleted"),
                any(Locale.class)
        );
    }

    @Test
    void changeHotStatus_shouldDelegate() {
        VoucherDTO request =
                VoucherDTO.builder()
                        .isHot(true)
                        .build();

        when(voucherService.changeHotStatus(
                "voucher-id",
                request
        )).thenReturn(request);

        var response = controller.changeHotStatus(
                "voucher-id",
                request
        );

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        ApiResponse<VoucherDTO> body = response.getBody();

        assertNotNull(body);
        assertEquals("msg.status.hot", body.getStatusMessage());
        assertEquals(request, body.getResults());

        verify(voucherService)
                .changeHotStatus("voucher-id", request);

        verify(messageSource).getMessage(
                eq("msg.status.hot"),
                isNull(),
                eq("msg.status.hot"),
                any(Locale.class)
        );
    }
}

