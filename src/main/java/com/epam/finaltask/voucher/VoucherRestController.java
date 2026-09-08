package com.epam.finaltask.voucher;

import com.epam.finaltask.core.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherRestController {

    private final VoucherService voucherService;
    private final MessageSource messageSource;

    private String getMsg(String key) {
        return messageSource.getMessage(
                key,
                null,
                key,
                LocaleContextHolder.getLocale()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VoucherDTO>>> findAll() {

        List<VoucherDTO> vouchers =
                voucherService.findAll();

        return ResponseEntity.ok(
                ApiResponse.ok(
                        getMsg("msg.vouchers.fetched"),
                        vouchers
                )
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<VoucherDTO>>>
    findAllByUserId(
            @PathVariable String userId
    ) {

        List<VoucherDTO> vouchers =
                voucherService.findAllByUserId(
                        userId
                );

        return ResponseEntity.ok(
                ApiResponse.ok(
                        getMsg("msg.vouchers.userFetched"),
                        vouchers
                )
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<VoucherDTO>>
    createVoucher(
            @Valid
            @RequestBody
            CreateVoucherRequestDTO request
    ) {

        VoucherDTO createdVoucher =
                voucherService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.ok(
                                getMsg("msg.tour.created"),
                                createdVoucher
                        )
                );
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<VoucherDTO>>
    updateVoucher(
            @PathVariable String id,
            @Valid
            @RequestBody
            UpdateVoucherRequestDTO request
    ) {

        VoucherDTO updatedVoucher =
                voucherService.update(
                        id,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.ok(
                        getMsg("msg.tour.updated"),
                        updatedVoucher
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>>
    deleteVoucherById(
            @PathVariable String id
    ) {

        voucherService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.ok(
                        getMsg("msg.tour.deleted"),
                        null
                )
        );
    }

    @PatchMapping("/{id}/hot")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<VoucherDTO>>
    changeHotStatus(
            @PathVariable String id,
            @RequestBody VoucherDTO voucherDTO
    ) {

        VoucherDTO updatedVoucher =
                voucherService.changeHotStatus(
                        id,
                        voucherDTO
                );

        return ResponseEntity.ok(
                ApiResponse.ok(
                        getMsg("msg.status.hot"),
                        updatedVoucher
                )
        );
    }
}

