package com.epam.finaltask.user;

import com.epam.finaltask.core.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;
    private final MessageSource messageSource;

    private String getMsg(
            String key
    ) {

        return messageSource.getMessage(
                key,
                null,
                LocaleContextHolder.getLocale()
        );
    }

    // ========================================================================
    // SEARCH USERS
    // ========================================================================


    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<
            ApiResponse<Page<UserResponseDTO>>
            > searchUsers(

            @Valid
            @ParameterObject
            UserSearchRequestDTO requestDTO,

            @ParameterObject
            Pageable pageable
    ) {

        Page<UserResponseDTO> userPage =
                userService.findUsers(
                        requestDTO,
                        pageable.getPageNumber(),
                        pageable.getPageSize()
                );

        return ResponseEntity.ok(
                ApiResponse.ok(
                        getMsg("msg.user.fetchedUsers"),
                        userPage
                )
        );
    }



    // ========================================================================
    // GET USER BY ID
    // ========================================================================

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<
            ApiResponse<UserResponseDTO>
            > getUserById(
            @PathVariable UUID id
    ) {

        UserResponseDTO user =
                userService.getUserById(
                        id
                );

        return ResponseEntity.ok(
                ApiResponse.ok(
                        getMsg("msg.user.fetched"),
                        user
                )
        );
    }

    // ========================================================================
    // GET USER BY USERNAME
    // ========================================================================

    @GetMapping("/username/{username}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<
            ApiResponse<UserResponseDTO>
            > getUserByUsername(
            @PathVariable String username
    ) {

        UserResponseDTO user =
                userService.getUserByUsername(
                        username
                );

        return ResponseEntity.ok(
                ApiResponse.ok(
                        getMsg("msg.user.fetched"),
                        user
                )
        );
    }

    // ========================================================================
    // CHANGE ACCOUNT STATUS
    // ========================================================================

    @PatchMapping("/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<
            ApiResponse<UserResponseDTO>
            > changeAccountStatus(

            @Valid
            @RequestBody
            ChangeAccountStatusRequestDTO request
    ) {

        UserResponseDTO updatedUser =
                userService.changeAccountStatus(
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.ok(
                        getMsg("msg.user.status.changed"),
                        updatedUser
                )
        );
    }

    // ========================================================================
    // UPDATE PROFILE
    // ========================================================================

    @PatchMapping("/profile")
    public ResponseEntity<
            ApiResponse<UserResponseDTO>
            > updateProfile(

            @Valid
            @RequestBody
            UpdateProfileRequestDTO request,

            Principal principal
    ) {

        userService.updateUserProfile(
                principal.getName(),
                request
        );

        UserResponseDTO updatedUser =
                userService.getUserByUsername(
                        principal.getName()
                );

        return ResponseEntity.ok(
                ApiResponse.ok(
                        getMsg("msg.user.profile.updated"),
                        updatedUser
                )
        );
    }

    // ========================================================================
    // CHANGE PASSWORD
    // ========================================================================

    @PatchMapping("/password")
    public ResponseEntity<
            ApiResponse<Void>
            > changePassword(

            @Valid
            @RequestBody
            ChangePasswordRequestDTO request,

            Principal principal
    ) {

        userService.changePassword(
                principal.getName(),
                request
        );

        return ResponseEntity.ok(
                ApiResponse.ok(
                        getMsg("msg.user.password.changed"),
                        null
                )
        );
    }

    // ========================================================================
    // USER BALANCE
    // ========================================================================

    @PostMapping("/balance/deposit")
    public ResponseEntity<
            ApiResponse<UserResponseDTO>
            > depositBalance(

            @Valid
            @RequestBody
            DepositBalanceRequestDTO request,

            Principal principal
    ) {

        UserResponseDTO updatedUser =
                userService.depositBalance(
                        principal.getName(),
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.ok(
                        getMsg("msg.user.balance.toppedUp"),
                        updatedUser
                )
        );
    }

    // ========================================================================
    // ADMIN BALANCE
    // ========================================================================

    @PostMapping("/balance/deposit/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<
            ApiResponse<UserResponseDTO>
            > depositBalanceByAdmin(

            @Valid
            @RequestBody
            AdminDepositBalanceRequestDTO request
    ) {

        UserResponseDTO updatedUser =
                userService.depositBalanceByAdmin(
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.ok(
                        getMsg(
                                "msg.user.balance.adminToppedUp"
                        ),
                        updatedUser
                )
        );
    }

    // ========================================================================
    // DELETE ACCOUNT
    // ========================================================================

    @DeleteMapping("/me")
    public ResponseEntity<
            ApiResponse<Void>
            > deleteAccount(
            Principal principal
    ) {

        userService.deleteAccount(
                principal.getName()
        );

        return ResponseEntity.ok(
                ApiResponse.ok(
                        getMsg(
                                "msg.user.account.deleted"
                        ),
                        null
                )
        );
    }
}

