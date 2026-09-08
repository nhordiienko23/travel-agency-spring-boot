package com.epam.finaltask.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    private JwtFilter jwtFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        jwtFilter = new JwtFilter(
                jwtUtils,
                userDetailsService
        );

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ========================================================================
    // HAPPY PATH
    // ========================================================================

    @Test
    void doFilterInternal_ShouldAuthenticateUser_WhenValidBearerToken()
            throws Exception {

        String token = "valid-token";
        String username = "john";

        UserDetails userDetails =
                User.withUsername(username)
                        .password("encodedPassword")
                        .authorities("ROLE_USER")
                        .build();

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        when(jwtUtils.validateToken(token))
                .thenReturn(true);

        when(jwtUtils.getUsernameFromToken(token))
                .thenReturn(username);

        when(userDetailsService.loadUserByUsername(username))
                .thenReturn(userDetails);

        jwtFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        assertNotNull(authentication);

        assertInstanceOf(
                UsernamePasswordAuthenticationToken.class,
                authentication
        );

        assertSame(
                userDetails,
                authentication.getPrincipal()
        );

        assertEquals(
                username,
                authentication.getName()
        );

        assertTrue(
                authentication.getAuthorities()
                        .contains(
                                new SimpleGrantedAuthority("ROLE_USER")
                        )
        );

        verify(jwtUtils).validateToken(token);
        verify(jwtUtils).getUsernameFromToken(token);
        verify(userDetailsService)
                .loadUserByUsername(username);

        verify(filterChain)
                .doFilter(request, response);
    }

    // ========================================================================
    // COOKIE
    // ========================================================================

    @Test
    void doFilterInternal_ShouldAuthenticateUser_WhenTokenIsInCookie()
            throws Exception {

        String token = "cookie-token";
        String username = "john";

        request.setCookies(
                new Cookie("JWT", token)
        );

        UserDetails userDetails =
                User.withUsername(username)
                        .password("encodedPassword")
                        .authorities("ROLE_USER")
                        .build();

        when(jwtUtils.validateToken(token))
                .thenReturn(true);

        when(jwtUtils.getUsernameFromToken(token))
                .thenReturn(username);

        when(userDetailsService.loadUserByUsername(username))
                .thenReturn(userDetails);

        jwtFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        assertNotNull(authentication);
        assertEquals(
                username,
                authentication.getName()
        );

        verify(jwtUtils).validateToken(token);
        verify(jwtUtils).getUsernameFromToken(token);
        verify(userDetailsService)
                .loadUserByUsername(username);

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldPreferBearerHeaderOverCookie()
            throws Exception {

        String headerToken = "header-token";
        String cookieToken = "cookie-token";

        request.addHeader(
                "Authorization",
                "Bearer " + headerToken
        );

        request.setCookies(
                new Cookie("JWT", cookieToken)
        );

        when(jwtUtils.validateToken(headerToken))
                .thenReturn(false);

        jwtFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(jwtUtils)
                .validateToken(headerToken);

        verify(jwtUtils, never())
                .validateToken(cookieToken);

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldFindJwtCookieAmongOtherCookies()
            throws Exception {

        String token = "jwt-token";

        request.setCookies(
                new Cookie("SESSION", "session"),
                new Cookie("OTHER", "other"),
                new Cookie("JWT", token)
        );

        when(jwtUtils.validateToken(token))
                .thenReturn(false);

        jwtFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(jwtUtils)
                .validateToken(token);

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldIgnoreCookiesWithoutJwt()
            throws Exception {

        request.setCookies(
                new Cookie("SESSION", "session"),
                new Cookie("OTHER", "other")
        );

        jwtFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder.getContext()
                        .getAuthentication()
        );

        verifyNoInteractions(jwtUtils);
        verifyNoInteractions(userDetailsService);

        verify(filterChain)
                .doFilter(request, response);
    }

    // ========================================================================
    // NO TOKEN
    // ========================================================================

    @Test
    void doFilterInternal_ShouldContinue_WhenTokenIsMissing()
            throws Exception {

        jwtFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder.getContext()
                        .getAuthentication()
        );

        verifyNoInteractions(jwtUtils);
        verifyNoInteractions(userDetailsService);

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldIgnoreNonBearerAuthorizationHeader()
            throws Exception {

        request.addHeader(
                "Authorization",
                "Basic credentials"
        );

        jwtFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder.getContext()
                        .getAuthentication()
        );

        verifyNoInteractions(jwtUtils);
        verifyNoInteractions(userDetailsService);

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldValidateEmptyToken_WhenBearerPrefixHasNoToken()
            throws Exception {

        request.addHeader(
                "Authorization",
                "Bearer "
        );

        when(jwtUtils.validateToken(""))
                .thenReturn(false);

        jwtFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder.getContext()
                        .getAuthentication()
        );

        verify(jwtUtils)
                .validateToken("");

        verify(jwtUtils, never())
                .getUsernameFromToken(anyString());

        verifyNoInteractions(userDetailsService);

        verify(filterChain)
                .doFilter(request, response);
    }

    // ========================================================================
    // INVALID TOKEN
    // ========================================================================

    @Test
    void doFilterInternal_ShouldNotAuthenticate_WhenTokenIsInvalid()
            throws Exception {

        String token = "invalid-token";

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        when(jwtUtils.validateToken(token))
                .thenReturn(false);

        jwtFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder.getContext()
                        .getAuthentication()
        );

        verify(jwtUtils)
                .validateToken(token);

        verify(jwtUtils, never())
                .getUsernameFromToken(anyString());

        verifyNoInteractions(userDetailsService);

        verify(filterChain)
                .doFilter(request, response);
    }

    // ========================================================================
    // USERNAME
    // ========================================================================

    @Test
    void doFilterInternal_ShouldNotAuthenticate_WhenUsernameIsNull()
            throws Exception {

        String token = "valid-token";

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        when(jwtUtils.validateToken(token))
                .thenReturn(true);

        when(jwtUtils.getUsernameFromToken(token))
                .thenReturn(null);

        jwtFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder.getContext()
                        .getAuthentication()
        );

        verify(jwtUtils)
                .validateToken(token);

        verify(jwtUtils)
                .getUsernameFromToken(token);

        verifyNoInteractions(userDetailsService);

        verify(filterChain)
                .doFilter(request, response);
    }



    // ========================================================================
    // EXISTING AUTHENTICATION
    // ========================================================================

    @Test
    void doFilterInternal_ShouldNotReplaceExistingAuthentication()
            throws Exception {

        String token = "valid-token";

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        Authentication existingAuthentication =
                new UsernamePasswordAuthenticationToken(
                        "existingUser",
                        null,
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_USER"
                                )
                        )
                );

        SecurityContextHolder.getContext()
                .setAuthentication(existingAuthentication);

        when(jwtUtils.validateToken(token))
                .thenReturn(true);

        when(jwtUtils.getUsernameFromToken(token))
                .thenReturn("john");

        jwtFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        assertSame(
                existingAuthentication,
                authentication
        );

        verify(jwtUtils)
                .validateToken(token);

        verify(jwtUtils)
                .getUsernameFromToken(token);

        verifyNoInteractions(userDetailsService);

        verify(filterChain)
                .doFilter(request, response);
    }

    // ========================================================================
    // USER NOT FOUND
    // ========================================================================

    @Test
    void doFilterInternal_ShouldContinue_WhenUserDoesNotExist()
            throws Exception {

        String token = "valid-token";
        String username = "unknown";

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        when(jwtUtils.validateToken(token))
                .thenReturn(true);

        when(jwtUtils.getUsernameFromToken(token))
                .thenReturn(username);

        when(userDetailsService.loadUserByUsername(username))
                .thenThrow(
                        new UsernameNotFoundException(
                                "User not found"
                        )
                );

        jwtFilter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder.getContext()
                        .getAuthentication()
        );

        verify(jwtUtils)
                .validateToken(token);

        verify(jwtUtils)
                .getUsernameFromToken(token);

        verify(userDetailsService)
                .loadUserByUsername(username);

        verify(filterChain)
                .doFilter(request, response);
    }
}