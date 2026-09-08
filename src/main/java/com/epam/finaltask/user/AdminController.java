package com.epam.finaltask.user;

import com.epam.finaltask.voucher.CreateVoucherRequestDTO;
import com.epam.finaltask.voucher.UpdateVoucherRequestDTO;
import com.epam.finaltask.voucher.VoucherDTO;
import com.epam.finaltask.voucher.VoucherSearchRequestDTO;
import com.epam.finaltask.voucher.VoucherService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final VoucherService voucherService;
    private final MessageSource messageSource;

    // ========================================================================
    // HELPERS
    // ========================================================================

    private String getMsg(
            String key
    ) {

        return messageSource.getMessage(
                key,
                null,
                LocaleContextHolder.getLocale()
        );
    }

    private void validateVoucherDates(
            LocalDate arrival,
            LocalDate eviction,
            BindingResult bindingResult
    ) {

        if (arrival != null
                && arrival.getYear() > 2100) {

            bindingResult.rejectValue(
                    "arrivalDate",
                    "error.arrivalDate",
                    getMsg("err.date.year")
            );
        }

        if (eviction != null
                && eviction.getYear() > 2100) {

            bindingResult.rejectValue(
                    "evictionDate",
                    "error.evictionDate",
                    getMsg("err.date.year")
            );
        }

        if (arrival != null
                && eviction != null
                && eviction.isBefore(arrival)) {

            bindingResult.rejectValue(
                    "evictionDate",
                    "error.evictionDate",
                    getMsg("err.date.order")
            );
        }
    }

    // ========================================================================
    // ADMIN USERS
    // ========================================================================

    @GetMapping("/users")
    public String manageUsers(
            @ModelAttribute("searchRequest")
            UserSearchRequestDTO searchRequest,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "username")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String sortDir,

            Model model
    ) {

        /*
         * Only username sorting is currently supported.
         */
        sortBy = "username";

        if (!"desc".equalsIgnoreCase(sortDir)) {
            sortDir = "asc";
        } else {
            sortDir = "desc";
        }

        Page<UserResponseDTO> userPage =
                userService.findUsers(
                        searchRequest,
                        page,
                        5,
                        sortBy,
                        sortDir
                );

        if (page > 0
                && page >= userPage.getTotalPages()) {

            page =
                    Math.max(
                            0,
                            userPage.getTotalPages() - 1
                    );

            userPage =
                    userService.findUsers(
                            searchRequest,
                            page,
                            5,
                            sortBy,
                            sortDir
                    );
        }

        model.addAttribute(
                "userPage",
                userPage
        );

        model.addAttribute(
                "currentPage",
                page
        );

        model.addAttribute(
                "totalPages",
                userPage.getTotalPages()
        );

        model.addAttribute(
                "sortBy",
                sortBy
        );

        model.addAttribute(
                "sortDir",
                sortDir
        );

        return "admin/users";
    }

    // ========================================================================
    // TOGGLE USER STATUS
    // ========================================================================

    @PostMapping("/users/toggle-status")
    public String toggleUserStatus(
            @RequestParam String username,
            HttpServletRequest request
    ) {

        userService.toggleUserStatus(
                username
        );

        String referer =
                request.getHeader("Referer");

        return "redirect:"
                + (
                referer != null
                        ? referer
                        : "/admin/users"
        );
    }

    // ========================================================================
    // ADMIN USER BALANCE
    // ========================================================================

    @GetMapping("/users/deposit")
    public String showDepositForm(
            @RequestParam String userId,
            Model model
    ) {

        UUID id =
                UUID.fromString(
                        userId
                );

        UserResponseDTO user =
                userService.getUserById(
                        id
                );

        if (!model.containsAttribute(
                "depositRequest"
        )) {

            model.addAttribute(
                    "depositRequest",
                    new AdminDepositBalanceRequestDTO(
                            id,
                            null
                    )
            );
        }

        model.addAttribute(
                "user",
                user
        );

        return "admin/deposit";
    }

    @PostMapping("/users/deposit")
    public String depositBalance(
            @Valid
            @ModelAttribute("depositRequest")
            AdminDepositBalanceRequestDTO depositRequest,

            BindingResult bindingResult,

            HttpServletRequest request,

            Model model,

            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {

            UserResponseDTO user =
                    userService.getUserById(
                            depositRequest.userId()
                    );

            model.addAttribute(
                    "user",
                    user
            );

            return "admin/deposit";
        }

        userService.depositBalanceByAdmin(
                depositRequest
        );

        redirectAttributes.addFlashAttribute(
                "successMessage",
                getMsg(
                        "msg.user.balance.adminToppedUp"
                )
        );

        String referer =
                request.getHeader("Referer");

        return "redirect:"
                + (
                referer != null
                        ? referer
                        : "/admin/users"
        );
    }

    // ========================================================================
    // ADMIN VOUCHERS
    // ========================================================================

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

            page =
                    Math.max(
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

        return "admin/vouchers";
    }

    // ========================================================================
    // CREATE VOUCHER
    // ========================================================================

    @GetMapping("/vouchers/new")
    public String showCreateForm(
            Model model
    ) {

        if (!model.containsAttribute(
                "createRequest"
        )) {

            model.addAttribute(
                    "createRequest",
                    CreateVoucherRequestDTO
                            .builder()
                            .build()
            );
        }

        return "admin/voucher-form";
    }

    @PostMapping("/vouchers/new")
    public String processCreateForm(
            @Valid
            @ModelAttribute("createRequest")
            CreateVoucherRequestDTO createRequest,

            BindingResult bindingResult,

            RedirectAttributes redirectAttributes
    ) {

        validateVoucherDates(
                createRequest.arrivalDate(),
                createRequest.evictionDate(),
                bindingResult
        );

        if (bindingResult.hasErrors()) {

            return "admin/voucher-form";
        }

        voucherService.create(
                createRequest
        );

        redirectAttributes.addFlashAttribute(
                "successMessage",
                getMsg(
                        "msg.tour.created"
                )
        );

        return "redirect:/admin/vouchers";
    }

    // ========================================================================
    // EDIT VOUCHER
    // ========================================================================

    @GetMapping("/vouchers/edit/{id}")
    public String showEditForm(
            @PathVariable String id,
            Model model
    ) {

        if (!model.containsAttribute(
                "updateRequest"
        )) {

            VoucherDTO v =
                    voucherService.findById(
                            id
                    );

            UpdateVoucherRequestDTO updateRequest =
                    UpdateVoucherRequestDTO
                            .builder()
                            .title(v.title())
                            .description(v.description())
                            .price(v.price())
                            .tourType(v.tourType())
                            .transferType(v.transferType())
                            .hotelType(v.hotelType())
                            .arrivalDate(v.arrivalDate())
                            .evictionDate(v.evictionDate())
                            .isHot(v.isHot())
                            .status(v.status())
                            .build();

            model.addAttribute(
                    "updateRequest",
                    updateRequest
            );
        }

        model.addAttribute(
                "voucherId",
                id
        );

        return "admin/voucher-edit";
    }

    @PostMapping("/vouchers/edit/{id}")
    public String processEditForm(
            @PathVariable String id,

            @Valid
            @ModelAttribute("updateRequest")
            UpdateVoucherRequestDTO updateRequest,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes
    ) {

        validateVoucherDates(
                updateRequest.arrivalDate(),
                updateRequest.evictionDate(),
                bindingResult
        );

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "voucherId",
                    id
            );

            return "admin/voucher-edit";
        }

        voucherService.update(
                id,
                updateRequest
        );

        redirectAttributes.addFlashAttribute(
                "successMessage",
                getMsg(
                        "msg.tour.updated"
                )
        );

        return "redirect:/admin/vouchers";
    }

    // ========================================================================
    // DELETE VOUCHER
    // ========================================================================

    @PostMapping("/vouchers/delete")
    public String deleteVoucher(
            @RequestParam String id,

            HttpServletRequest request,

            RedirectAttributes redirectAttributes
    ) {

        voucherService.delete(
                id
        );

        redirectAttributes.addFlashAttribute(
                "successMessage",
                getMsg(
                        "msg.tour.deleted"
                )
        );

        String referer =
                request.getHeader("Referer");

        return "redirect:"
                + (
                referer != null
                        ? referer
                        : "/admin/vouchers"
        );
    }
}

