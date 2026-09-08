package com.epam.finaltask.user;

import com.epam.finaltask.auth.AuthService;
import com.epam.finaltask.core.exception.invalidData.IllegalUserArgumentException;
import com.epam.finaltask.core.exception.notFound.ResourceNotFoundException;
import com.epam.finaltask.voucher.VoucherDTO;
import com.epam.finaltask.voucher.VoucherService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final VoucherService voucherService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthService authService;

    // ========================================================================
    // PROFILE
    // ========================================================================

    @GetMapping("/profile")
    public String getProfilePage(
            @RequestParam(defaultValue = "0")
            int page,

            Principal principal,

            Model model
    ) {

        populateModelForProfile(
                principal,
                model,
                page
        );

        return "user/profile";
    }

    // ========================================================================
    // UPDATE PROFILE
    // ========================================================================

    @PostMapping("/profile/update")
    public String updateProfile(
            @Valid
            @ModelAttribute("updateProfileRequest")
            UpdateProfileRequestDTO request,

            BindingResult bindingResult,

            Principal principal,

            Model model,

            HttpServletResponse response
    ) {

        if (bindingResult.hasErrors()) {

            return repopulateProfileModel(
                    principal,
                    model
            );
        }

        try {

            boolean usernameChanged =
                    userService.updateUserProfile(
                            principal.getName(),
                            request
                    );

            if (usernameChanged) {

                String newToken =
                        authService.refreshToken(
                                request.username()
                        );

                Cookie cookie =
                        new Cookie(
                                "JWT",
                                newToken
                        );

                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(
                        24 * 60 * 60
                );

                response.addCookie(
                        cookie
                );
            }

            return "redirect:/profile?success";

        } catch (IllegalUserArgumentException e) {

            String errorCode =
                    resolveErrorCode(
                            e.getMessage()
                    );

            if ("err.email.taken".equals(
                    errorCode
            )) {

                bindingResult.rejectValue(
                        "email",
                        errorCode
                );

            } else {

                bindingResult.rejectValue(
                        "username",
                        errorCode
                );
            }

            return repopulateProfileModel(
                    principal,
                    model
            );
        }
    }

    // ========================================================================
    // CHANGE PASSWORD
    // ========================================================================

    @PostMapping("/profile/password")
    public String changePassword(
            @Valid
            @ModelAttribute("changePasswordRequest")
            ChangePasswordRequestDTO request,

            BindingResult bindingResult,

            Principal principal,

            Model model
    ) {

        if (bindingResult.hasErrors()) {

            return repopulateProfileModel(
                    principal,
                    model
            );
        }

        try {

            userService.changePassword(
                    principal.getName(),
                    request
            );

            return "redirect:/profile?passwordChanged";

        } catch (IllegalUserArgumentException e) {

            String errorCode =
                    resolveErrorCode(
                            e.getMessage()
                    );

            bindingResult.rejectValue(
                    "currentPassword",
                    errorCode
            );

            return repopulateProfileModel(
                    principal,
                    model
            );
        }
    }

    // ========================================================================
    // DEPOSIT BALANCE
    // ========================================================================

    @PostMapping("/profile/balance")
    public String depositBalance(
            @Valid
            @ModelAttribute("depositBalanceRequest")
            DepositBalanceRequestDTO request,

            BindingResult bindingResult,

            Principal principal,

            Model model
    ) {

        if (bindingResult.hasErrors()) {

            return repopulateProfileModel(
                    principal,
                    model
            );
        }

        try {

            userService.depositBalance(
                    principal.getName(),
                    request
            );

            return "redirect:/profile?balanceSuccess";

        } catch (IllegalArgumentException e) {

            String errorCode =
                    resolveErrorCode(
                            e.getMessage()
                    );

            bindingResult.rejectValue(
                    "amount",
                    errorCode
            );

            return repopulateProfileModel(
                    principal,
                    model
            );
        }
    }

    // ========================================================================
    // DELETE ACCOUNT
    // ========================================================================

    @PostMapping("/profile/delete")
    public String deleteAccount(
            Principal principal,
            HttpServletResponse response
    ) {

        userService.deleteAccount(
                principal.getName()
        );

        Cookie cookie =
                new Cookie(
                        "JWT",
                        null
                );

        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(
                cookie
        );

        return "redirect:/";
    }

    // ========================================================================
    // PROFILE MODEL
    // ========================================================================

    private void populateModelForProfile(
            Principal principal,
            Model model,
            int page
    ) {

        User user =
                userRepository
                        .findUserByUsername(
                                principal.getName()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "err.user.notFound"
                                )
                        );

        Page<VoucherDTO> myVouchersPage =
                voucherService.findAllByUserIdPaged(
                        user.getId().toString(),
                        page,
                        10
                );

        if (page > 0
                && page >= myVouchersPage.getTotalPages()) {

            page =
                    Math.max(
                            0,
                            myVouchersPage.getTotalPages() - 1
                    );

            myVouchersPage =
                    voucherService.findAllByUserIdPaged(
                            user.getId().toString(),
                            page,
                            10
                    );
        }

        /*
         * Pass DTO to the view instead of entity.
         */
        model.addAttribute(
                "user",
                userMapper.toUserResponseDTO(
                        user
                )
        );

        model.addAttribute(
                "myVouchersPage",
                myVouchersPage
        );

        if (!model.containsAttribute(
                "updateProfileRequest"
        )) {

            model.addAttribute(
                    "updateProfileRequest",
                    UpdateProfileRequestDTO.builder()
                            .username(
                                    user.getUsername()
                            )
                            .email(
                                    user.getEmail()
                            )
                            .lastName(
                                    user.getLastName()
                            )
                            .phoneNumber(
                                    user.getPhoneNumber()
                            )
                            .build()
            );
        }

        if (!model.containsAttribute(
                "changePasswordRequest"
        )) {

            model.addAttribute(
                    "changePasswordRequest",
                    ChangePasswordRequestDTO
                            .builder()
                            .build()
            );
        }

        if (!model.containsAttribute(
                "depositBalanceRequest"
        )) {

            model.addAttribute(
                    "depositBalanceRequest",
                    DepositBalanceRequestDTO
                            .builder()
                            .build()
            );
        }
    }

    // ========================================================================
    // REPOPULATE PROFILE MODEL
    // ========================================================================

    private String repopulateProfileModel(
            Principal principal,
            Model model
    ) {

        populateModelForProfile(
                principal,
                model,
                0
        );

        return "user/profile";
    }

    // ========================================================================
    // ERROR CODE
    // ========================================================================

    private String resolveErrorCode(
            String message
    ) {

        if (message == null
                || message.isBlank()) {

            return "error.unknown";
        }

        return message;
    }
}

