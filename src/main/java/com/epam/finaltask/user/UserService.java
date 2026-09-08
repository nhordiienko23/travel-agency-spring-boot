package com.epam.finaltask.user;

import org.springframework.data.domain.Page;

import java.util.UUID;

public interface UserService {

    UserResponseDTO getUserById(
            UUID id
    );

    UserResponseDTO getUserByUsername(
            String username
    );

    UserResponseDTO changeAccountStatus(
            ChangeAccountStatusRequestDTO request
    );

    Page<UserResponseDTO> findUsers(
            UserSearchRequestDTO searchRequest,
            int page,
            int size
    );

    Page<UserResponseDTO> findUsers(
            UserSearchRequestDTO searchRequest,
            int page,
            int size,
            String sortBy,
            String sortDir
    );

    void toggleUserStatus(
            String username
    );

    boolean updateUserProfile(
            String username,
            UpdateProfileRequestDTO request
    );

    void changePassword(
            String username,
            ChangePasswordRequestDTO request
    );

    UserResponseDTO depositBalance(
            String username,
            DepositBalanceRequestDTO request
    );

    UserResponseDTO depositBalanceByAdmin(
            AdminDepositBalanceRequestDTO request
    );

    void deleteAccount(
            String username
    );
}

