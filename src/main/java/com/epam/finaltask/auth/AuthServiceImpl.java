package com.epam.finaltask.auth;

import com.epam.finaltask.core.exception.invalidData.IllegalUserArgumentException;
import com.epam.finaltask.log.Loggable;
import com.epam.finaltask.security.JwtUtils;
import com.epam.finaltask.user.*;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Override
    @Transactional
    @Loggable("REGISTER_USER")
    public UserResponseDTO register(RegisterRequestDTO request) {

        if (userRepository.findUserByUsername(request.username()).isPresent()) {
            throw new IllegalUserArgumentException("err.username.taken");
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalUserArgumentException("err.email.taken");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .lastName(request.lastName())
                .phoneNumber(request.phoneNumber())
                .role(Role.USER)
                .balance(null)
                .active(true)
                .build();

        User savedUser = userRepository.save(user);

        return userMapper.toUserResponseDTO(savedUser);
    }

    @Override
    @Transactional
    @Loggable("LOGIN")
    public String login(LoginRequestDTO request) {

        UserDetails userDetails;

        try {
            userDetails = userDetailsService.loadUserByUsername(
                    request.username()
            );
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException("err.login.invalid");
        }

        if (!passwordEncoder.matches(
                request.password(),
                userDetails.getPassword()
        )) {
            throw new BadCredentialsException("err.login.invalid");
        }

        if (!userDetails.isEnabled()) {
            throw new DisabledException("err.account.blocked");
        }

        return jwtUtils.generateToken(request.username());
    }

    @Override
    public String refreshToken(String username) {
        return jwtUtils.generateToken(username);
    }
}

