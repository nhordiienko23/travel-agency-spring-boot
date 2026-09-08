package com.epam.finaltask.auth;

import com.epam.finaltask.user.Role;
import com.epam.finaltask.user.User;
import com.epam.finaltask.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    // ========================================================================
    // HAPPY PATH
    // ========================================================================

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {

        User user = User.builder()
                .username("john")
                .password("encodedPassword")
                .role(Role.USER)
                .active(true)
                .build();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));


        UserDetails result =
                userDetailsService.loadUserByUsername("john");


        assertNotNull(result);
        assertEquals("john", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.isEnabled());

        assertTrue(
                result.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_USER")
                        )
        );

        verify(userRepository)
                .findUserByUsername("john");
    }

    // ========================================================================
    // NOT FOUND
    // ========================================================================

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {

        when(userRepository.findUserByUsername("unknown"))
                .thenReturn(Optional.empty());


        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown")
        );


        assertEquals(
                "User not found with username: unknown",
                exception.getMessage()
        );

        verify(userRepository)
                .findUserByUsername("unknown");
    }

    // ========================================================================
    // ACTIVE / DISABLED
    // ========================================================================

    @Test
    void loadUserByUsername_ShouldReturnEnabledUser_WhenUserIsActive() {

        User user = User.builder()
                .username("activeUser")
                .password("encoded")
                .role(Role.USER)
                .active(true)
                .build();

        when(userRepository.findUserByUsername("activeUser"))
                .thenReturn(Optional.of(user));


        UserDetails result =
                userDetailsService.loadUserByUsername("activeUser");


        assertTrue(result.isEnabled());
    }

    @Test
    void loadUserByUsername_ShouldReturnDisabledUser_WhenUserIsInactive() {

        User user = User.builder()
                .username("blockedUser")
                .password("encoded")
                .role(Role.USER)
                .active(false)
                .build();

        when(userRepository.findUserByUsername("blockedUser"))
                .thenReturn(Optional.of(user));


        UserDetails result =
                userDetailsService.loadUserByUsername("blockedUser");


        assertFalse(result.isEnabled());
    }

    // ========================================================================
    // ROLES
    // ========================================================================

    @Test
    void loadUserByUsername_ShouldAssignUserRole() {

        User user = User.builder()
                .username("john")
                .password("encoded")
                .role(Role.USER)
                .active(true)
                .build();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));


        UserDetails result =
                userDetailsService.loadUserByUsername("john");


        assertEquals(
                1,
                result.getAuthorities().size()
        );

        assertEquals(
                "ROLE_USER",
                result.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
        );
    }

    @Test
    void loadUserByUsername_ShouldAssignAdminRole() {

        User user = User.builder()
                .username("admin")
                .password("encoded")
                .role(Role.ADMIN)
                .active(true)
                .build();

        when(userRepository.findUserByUsername("admin"))
                .thenReturn(Optional.of(user));


        UserDetails result =
                userDetailsService.loadUserByUsername("admin");


        assertTrue(
                result.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN")
                        )
        );
    }

    // ========================================================================
    // ACCOUNT FLAGS
    // ========================================================================

    @Test
    void loadUserByUsername_ShouldEnableAccount_WhenActive() {

        User user = User.builder()
                .username("john")
                .password("encoded")
                .role(Role.USER)
                .active(true)
                .build();

        when(userRepository.findUserByUsername("john"))
                .thenReturn(Optional.of(user));


        UserDetails result =
                userDetailsService.loadUserByUsername("john");


        assertTrue(result.isEnabled());
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isCredentialsNonExpired());
    }

    @Test
    void loadUserByUsername_ShouldKeepOtherAccountFlagsTrue_WhenInactive() {

        User user = User.builder()
                .username("blocked")
                .password("encoded")
                .role(Role.USER)
                .active(false)
                .build();

        when(userRepository.findUserByUsername("blocked"))
                .thenReturn(Optional.of(user));


        UserDetails result =
                userDetailsService.loadUserByUsername("blocked");


        assertFalse(result.isEnabled());
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isCredentialsNonExpired());
    }
}