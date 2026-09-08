package com.epam.finaltask.auth;


import com.epam.finaltask.user.UserResponseDTO;

public interface AuthService {
    UserResponseDTO register(RegisterRequestDTO request);
    String login(LoginRequestDTO request);
    String refreshToken(String username);
}