package com.epam.finaltask.restcontroller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epam.finaltask.dto.ApiResponse;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.service.VoucherService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherRestController {

    private final VoucherService voucherService;



    // 1. Получение всех ваучеров
    @GetMapping
    public ResponseEntity<ApiResponse<List<VoucherDTO>>> findAll() {
        List<VoucherDTO> vouchers = voucherService.findAll();
        return ResponseEntity.ok(ApiResponse.ok("OK", vouchers));
    }

    // 2. Получение ваучеров конкретного пользователя
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<VoucherDTO>>> findAllByUserId(@PathVariable String userId) {
        List<VoucherDTO> vouchers = voucherService.findAllByUserId(userId);
        return ResponseEntity.ok(ApiResponse.ok("OK", vouchers));
    }

    // 3. Создание ваучера (Доступно только ADMIN)
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<VoucherDTO>> createVoucher(@Valid @RequestBody VoucherDTO voucherDTO) {
        VoucherDTO createdVoucher = voucherService.create(voucherDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Voucher is successfully created", createdVoucher));
    }

    // 4. Обновление ваучера (Доступно только ADMIN)
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<VoucherDTO>> updateVoucher(
            @PathVariable String id,
            @Valid @RequestBody VoucherDTO voucherDTO) {
        VoucherDTO updatedVoucher = voucherService.update(id, voucherDTO);
        return ResponseEntity.ok(ApiResponse.ok("Voucher is successfully updated", updatedVoucher));
    }

    // 5. Удаление ваучера (Доступно только ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteVoucherById(@PathVariable String id) {
        voucherService.delete(id);
        String message = String.format("Voucher with Id %s has been deleted", id);
        return ResponseEntity.ok(ApiResponse.ok(message));
    }

    // 6. Изменение статуса "Hot" (Доступно ADMIN и MANAGER)
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<VoucherDTO>> changeVoucherStatus(
            @PathVariable String id,
            @RequestBody VoucherDTO voucherDTO) {
        VoucherDTO updatedVoucher = voucherService.changeHotStatus(id, voucherDTO);
        return ResponseEntity.ok(ApiResponse.ok("Voucher status is successfully changed", updatedVoucher));
    }
}