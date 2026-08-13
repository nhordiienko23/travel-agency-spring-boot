package com.epam.finaltask.token;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_WithValidHeaderToken_SetsAuthentication() throws Exception {
        String token = "valid_token";
        String username = "testuser";
        UserDetails userDetails = new User(username, "password", Collections.emptyList());

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.validateToken(token)).thenReturn(true);
        when(jwtUtils.getUsernameFromToken(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithValidCookieToken_SetsAuthentication() throws Exception {
        String token = "valid_cookie_token";
        String username = "testuser";
        UserDetails userDetails = new User(username, "password", Collections.emptyList());
        Cookie[] cookies = {new Cookie("JWT", token)};

        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(cookies);
        when(jwtUtils.validateToken(token)).thenReturn(true);
        when(jwtUtils.getUsernameFromToken(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_NoToken_DoesNotSetAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_InvalidToken_DoesNotSetAuthentication() throws Exception {
        String token = "invalid_token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.validateToken(token)).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_UserNotFound_DoesNotSetAuthentication() throws Exception {
        String token = "valid_token";
        String username = "testuser";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.validateToken(token)).thenReturn(true);
        when(jwtUtils.getUsernameFromToken(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenThrow(new UsernameNotFoundException("Not found"));

        jwtFilter.doFilterInternal(request, response, filterChain);

        // Security Context должен остаться пустым, так как пользователь был удален из БД
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}