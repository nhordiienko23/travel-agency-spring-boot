package com.epam.finaltask.voucher;

import com.epam.finaltask.core.exception.notFound.ResourceNotFoundException;
import com.epam.finaltask.user.User;
import com.epam.finaltask.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class VoucherController {

    private final UserRepository userRepository;
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

    @GetMapping("/dashboard")
    public String getDashboardPage(
            @ModelAttribute("searchRequest")
            VoucherSearchRequestDTO searchRequest,

            @RequestParam(defaultValue = "isHot")
            String sortField,

            @RequestParam(defaultValue = "desc")
            String sortDir,

            @RequestParam(defaultValue = "0")
            int page,

            Principal principal,
            Model model
    ) {

        User user = userRepository
                .findUserByUsername(principal.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "err.user.notFound"
                        )
                );

        Page<VoucherDTO> vouchersPage =
                voucherService.findAvailableVouchers(
                        searchRequest,
                        page,
                        8,
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
                    voucherService.findAvailableVouchers(
                            searchRequest,
                            page,
                            8,
                            sortField,
                            sortDir
                    );
        }

        model.addAttribute(
                "currentUser",
                user
        );

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

        return "user/dashboard";
    }

    @PostMapping("/vouchers/order")
    public String orderVoucher(
            @RequestParam String voucherId,
            Principal principal,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request
    ) {

        try {
            User user = userRepository
                    .findUserByUsername(
                            principal.getName()
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "err.user.notFound"
                            )
                    );

            voucherService.order(
                    voucherId,
                    user.getId().toString()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    getMsg("msg.tour.ordered")
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    resolveExceptionMessage(
                            e.getMessage()
                    )
            );
        }

        String referer =
                request.getHeader("Referer");

        return "redirect:" +
                (referer != null
                        ? referer
                        : "/dashboard");
    }

    @PostMapping("/vouchers/cancel")
    public String cancelVoucher(
            @RequestParam String voucherId,
            Principal principal,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request
    ) {

        try {

            voucherService.cancelOrder(
                    voucherId,
                    principal.getName()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    getMsg("msg.tour.canceled")
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    resolveExceptionMessage(
                            e.getMessage()
                    )
            );
        }

        String referer =
                request.getHeader("Referer");

        return "redirect:" +
                (referer != null
                        ? referer
                        : "/dashboard");
    }
}

