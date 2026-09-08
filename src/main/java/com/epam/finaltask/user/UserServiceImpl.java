package com.epam.finaltask.user;

import com.epam.finaltask.core.exception.invalidData.IllegalUserArgumentException;
import com.epam.finaltask.core.exception.notFound.ResourceNotFoundException;
import com.epam.finaltask.log.AuditContext;
import com.epam.finaltask.log.Loggable;
import com.epam.finaltask.voucher.Voucher;
import com.epam.finaltask.voucher.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final VoucherRepository voucherRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    // ========================================================================
    // HELPERS
    // ========================================================================

    private User findUserByIdOrThrow(
            UUID id
    ) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "err.user.notFound"
                        )
                );
    }

    private User findUserByUsernameOrThrow(
            String username
    ) {

        return userRepository.findUserByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "err.user.notFound"
                        )
                );
    }

    private void addBalance(
            User user,
            Double amount
    ) {

        double currentBalance =
                user.getBalance() == null
                        ? 0.0
                        : user.getBalance();

        user.setBalance(
                currentBalance + amount
        );
    }

    private void addChange(
            List<String> changes,
            String field,
            String oldValue,
            String newValue
    ) {

        if (!Objects.equals(
                oldValue,
                newValue
        )) {

            changes.add(
                    field
                            + ": "
                            + formatValue(oldValue)
                            + " -> "
                            + formatValue(newValue)
            );
        }
    }

    private String formatValue(
            String value
    ) {

        return value == null
                ? "null"
                : value;
    }

    private Sort buildUserSort(
            String sortBy,
            String sortDir
    ) {

        /*
         * At the moment administrator sorting is
         * intentionally limited to username.
         *
         * This prevents clients from passing arbitrary
         * entity fields into Sort.
         */

        String field =
                "username";

        if ("username".equalsIgnoreCase(sortBy)) {
            field = "username";
        }

        Sort.Direction direction =
                "asc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        return Sort.by(
                direction,
                field
        );
    }

    // ========================================================================
    // GET USER BY ID
    // ========================================================================

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(
            UUID id
    ) {

        User user =
                findUserByIdOrThrow(id);

        return userMapper.toUserResponseDTO(
                user
        );
    }

    // ========================================================================
    // GET USER BY USERNAME
    // ========================================================================

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByUsername(
            String username
    ) {

        User user =
                findUserByUsernameOrThrow(
                        username
                );

        return userMapper.toUserResponseDTO(
                user
        );
    }

    // ========================================================================
    // CHANGE ACCOUNT STATUS
    // ========================================================================

    @Override
    @Transactional
    @Loggable("CHANGE_ACCOUNT_STATUS")
    public UserResponseDTO changeAccountStatus(
            ChangeAccountStatusRequestDTO request
    ) {

        User user =
                findUserByIdOrThrow(
                        request.id()
                );

        boolean oldActive =
                user.isActive();

        boolean newActive =
                request.active();

        AuditContext.setDetails(
                String.format(
                        "userId=%s, active: %s -> %s",
                        request.id(),
                        oldActive,
                        newActive
                )
        );

        user.setActive(
                newActive
        );

        User savedUser =
                userRepository.save(
                        user
                );

        return userMapper.toUserResponseDTO(
                savedUser
        );
    }

    // ========================================================================
    // SEARCH USERS
    // ========================================================================

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> findUsers(
            UserSearchRequestDTO searchRequest,
            int page,
            int size
    ) {

        return findUsers(
                searchRequest,
                page,
                size,
                null,
                null
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> findUsers(
            UserSearchRequestDTO searchRequest,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {

        Pageable pageable;

        if (sortBy == null || sortBy.isBlank()) {

            pageable =
                    PageRequest.of(
                            page,
                            size
                    );

        } else {

            pageable =
                    PageRequest.of(
                            page,
                            size,
                            buildUserSort(
                                    sortBy,
                                    sortDir
                            )
                    );
        }

        Specification<User> spec =
                UserSpecification.searchByCriteria(
                        searchRequest
                );

        return userRepository
                .findAll(
                        spec,
                        pageable
                )
                .map(
                        userMapper::toUserResponseDTO
                );
    }

    // ========================================================================
    // TOGGLE USER STATUS
    // ========================================================================

    @Override
    @Transactional
    @Loggable("TOGGLE_USER_BLOCK_STATUS")
    public void toggleUserStatus(
            String username
    ) {

        User user =
                findUserByUsernameOrThrow(
                        username
                );

        boolean oldActive =
                user.isActive();

        boolean newActive =
                !oldActive;

        AuditContext.setDetails(
                String.format(
                        "username=%s, active: %s -> %s",
                        username,
                        oldActive,
                        newActive
                )
        );

        user.setActive(
                newActive
        );

        userRepository.save(
                user
        );
    }

    // ========================================================================
    // UPDATE PROFILE
    // ========================================================================

    @Override
    @Transactional
    @Loggable("UPDATE_PROFILE_INFO")
    public boolean updateUserProfile(
            String currentUsername,
            UpdateProfileRequestDTO request
    ) {

        User user =
                findUserByUsernameOrThrow(
                        currentUsername
                );

        List<String> changes =
                new ArrayList<>();

        boolean isUsernameChanged =
                false;

        if (request.username() != null
                && !request.username().isBlank()
                && !request.username().equals(
                currentUsername
        )) {

            if (userRepository
                    .findUserByUsername(
                            request.username()
                    )
                    .isPresent()) {

                throw new IllegalUserArgumentException(
                        "err.username.taken"
                );
            }

            addChange(
                    changes,
                    "username",
                    user.getUsername(),
                    request.username()
            );

            user.setUsername(
                    request.username()
            );

            isUsernameChanged =
                    true;
        }

        if (request.email() != null
                && !request.email().isBlank()
                && !request.email().equals(
                user.getEmail()
        )) {

            if (userRepository
                    .findByEmail(
                            request.email()
                    )
                    .isPresent()) {

                throw new IllegalUserArgumentException(
                        "err.email.taken"
                );
            }

            addChange(
                    changes,
                    "email",
                    user.getEmail(),
                    request.email()
            );

            user.setEmail(
                    request.email()
            );
        }

        if (request.lastName() != null
                && !request.lastName().isBlank()) {

            addChange(
                    changes,
                    "lastName",
                    user.getLastName(),
                    request.lastName()
            );

            user.setLastName(
                    request.lastName()
            );
        }

        if (request.phoneNumber() != null
                && !request.phoneNumber().isBlank()) {

            addChange(
                    changes,
                    "phoneNumber",
                    user.getPhoneNumber(),
                    request.phoneNumber()
            );

            user.setPhoneNumber(
                    request.phoneNumber()
            );
        }

        if (changes.isEmpty()) {

            AuditContext.setDetails(
                    "username="
                            + currentUsername
                            + ", no changes"
            );

        } else {

            AuditContext.setDetails(
                    "username="
                            + currentUsername
                            + ", "
                            + String.join(
                            ", ",
                            changes
                    )
            );
        }

        userRepository.save(
                user
        );

        return isUsernameChanged;
    }

    // ========================================================================
    // CHANGE PASSWORD
    // ========================================================================

    @Override
    @Transactional
    @Loggable("CHANGE_PASSWORD")
    public void changePassword(
            String username,
            ChangePasswordRequestDTO request
    ) {

        User user =
                findUserByUsernameOrThrow(
                        username
                );

        if (!passwordEncoder.matches(
                request.currentPassword(),
                user.getPassword()
        )) {

            throw new IllegalUserArgumentException(
                    "err.password.current.invalid"
            );
        }

        AuditContext.setDetails(
                "username=" + username
        );

        user.setPassword(
                passwordEncoder.encode(
                        request.newPassword()
                )
        );

        userRepository.save(
                user
        );
    }

    // ========================================================================
    // USER DEPOSIT
    // ========================================================================

    @Override
    @Transactional
    @Loggable("DEPOSIT_BALANCE_BY_USER")
    public UserResponseDTO depositBalance(
            String username,
            DepositBalanceRequestDTO request
    ) {

        User user =
                findUserByUsernameOrThrow(
                        username
                );

        addBalance(
                user,
                request.amount()
        );

        User savedUser =
                userRepository.save(
                        user
                );

        return userMapper.toUserResponseDTO(
                savedUser
        );
    }

    // ========================================================================
    // ADMIN DEPOSIT
    // ========================================================================

    @Override
    @Transactional
    @Loggable("DEPOSIT_BALANCE_BY_ADMIN")
    public UserResponseDTO depositBalanceByAdmin(
            AdminDepositBalanceRequestDTO request
    ) {

        User user =
                findUserByIdOrThrow(
                        request.userId()
                );

        addBalance(
                user,
                request.amount()
        );

        User savedUser =
                userRepository.save(
                        user
                );

        return userMapper.toUserResponseDTO(
                savedUser
        );
    }

    // ========================================================================
    // DELETE ACCOUNT
    // ========================================================================

    @Override
    @Transactional
    @Loggable("DELETE_ACCOUNT")
    public void deleteAccount(
            String username
    ) {

        User user =
                findUserByUsernameOrThrow(
                        username
                );

        List<Voucher> userVouchers =
                voucherRepository.findAllByUserId(
                        user.getId()
                );

        userVouchers.forEach(
                voucher ->
                        voucher.setUser(null)
        );

        voucherRepository.saveAllAndFlush(
                userVouchers
        );

        AuditContext.setDetails(
                String.format(
                        "username=%s, userId=%s",
                        username,
                        user.getId()
                )
        );

        userRepository.delete(
                user
        );
    }
}

