package com.epam.finaltask.user;

import com.epam.finaltask.voucher.ChangeVoucherStatusRequestDTO;
import com.epam.finaltask.voucher.VoucherDTO;
import com.epam.finaltask.voucher.VoucherSearchRequestDTO;
import com.epam.finaltask.voucher.VoucherService;
import com.epam.finaltask.voucher.VoucherStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/manager")
@PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN')")
@RequiredArgsConstructor
public class ManagerController {

    private final VoucherService voucherService;
    private final MessageSource messageSource;

    private String getMsg(String key) {
        return messageSource.getMessage(
                key,
                null,
                LocaleContextHolder.getLocale()
        );
    }

    private String resolveExceptionMessage(String message) {

        if (message == null || message.isBlank()) {
            return getMsg("error.unknown");
        }

        return messageSource.getMessage(
                message,
                null,
                message,
                LocaleContextHolder.getLocale()
        );
    }

    @GetMapping("/vouchers")
    public String manageVouchers(
            @ModelAttribute("searchRequest")
            VoucherSearchRequestDTO searchRequest,

            @RequestParam(defaultValue = "isHot")
            String sortField,

            @RequestParam(defaultValue = "desc")
            String sortDir,

            @RequestParam(defaultValue = "0")
            int page,

            Model model
    ) {

        Page<VoucherDTO> vouchersPage =
                voucherService.findAllVouchersPaged(
                        searchRequest,
                        page,
                        5,
                        sortField,
                        sortDir
                );

        if (page > 0
                && page >= vouchersPage.getTotalPages()) {

            page = Math.max(
                    0,
                    vouchersPage.getTotalPages() - 1
            );

            vouchersPage =
                    voucherService.findAllVouchersPaged(
                            searchRequest,
                            page,
                            5,
                            sortField,
                            sortDir
                    );
        }

        model.addAttribute(
                "vouchersPage",
                vouchersPage
        );

        model.addAttribute(
                "searchRequest",
                searchRequest
        );

        model.addAttribute(
                "sortField",
                sortField
        );

        model.addAttribute(
                "sortDir",
                sortDir
        );

        model.addAttribute(
                "currentPage",
                page
        );

        return "manager/vouchers";
    }

    @PostMapping("/vouchers/toggle-hot")
    public String toggleHotStatus(
            @RequestParam String id,
            @RequestParam boolean isHot,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request
    ) {

        try {

            VoucherDTO dto =
                    VoucherDTO.builder()
                            .isHot(!isHot)
                            .build();

            voucherService.changeHotStatus(
                    id,
                    dto
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    getMsg("msg.status.hot")
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    resolveExceptionMessage(
                            e.getMessage()
                    )
            );
        }

        String referer =
                request.getHeader("Referer");

        return "redirect:"
                + (
                referer != null
                        ? referer
                        : "/manager/vouchers"
        );
    }

    @PostMapping("/vouchers/update-status")
    public String updateStatus(
            @RequestParam String id,
            @RequestParam VoucherStatus status,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request
    ) {

        try {

            ChangeVoucherStatusRequestDTO req =
                    new ChangeVoucherStatusRequestDTO(
                            status
                    );

            voucherService.changeStatus(
                    id,
                    req
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    getMsg("msg.status.tour")
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    resolveExceptionMessage(
                            e.getMessage()
                    )
            );
        }

        String referer =
                request.getHeader("Referer");

        return "redirect:"
                + (
                referer != null
                        ? referer
                        : "/manager/vouchers"
        );
    }
}

