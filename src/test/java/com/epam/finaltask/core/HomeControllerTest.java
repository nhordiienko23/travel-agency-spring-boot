package com.epam.finaltask.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HomeControllerTest {

    private final HomeController controller =
            new HomeController();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getIndexPage_shouldReturnIndexWhenAuthenticationIsNull() {

        SecurityContextHolder.clearContext();

        String result =
                controller.getIndexPage();

        assertEquals(
                "index",
                result
        );
    }

    @Test
    void getIndexPage_shouldReturnIndexWhenUserIsNotAuthenticated() {

        Authentication authentication =
                mock(Authentication.class);

        when(authentication.isAuthenticated())
                .thenReturn(false);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        String result =
                controller.getIndexPage();

        assertEquals(
                "index",
                result
        );
    }

    @Test
    void getIndexPage_shouldReturnIndexForAnonymousUser() {

        Authentication authentication =
                mock(Authentication.class);

        when(authentication.isAuthenticated())
                .thenReturn(true);

        when(authentication.getPrincipal())
                .thenReturn("anonymousUser");

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        String result =
                controller.getIndexPage();

        assertEquals(
                "index",
                result
        );
    }

    @Test
    void getIndexPage_shouldRedirectToDashboardForAuthenticatedUser() {

        Authentication authentication =
                mock(Authentication.class);

        when(authentication.isAuthenticated())
                .thenReturn(true);

        when(authentication.getPrincipal())
                .thenReturn("admin");

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        String result =
                controller.getIndexPage();

        assertEquals(
                "redirect:/dashboard",
                result
        );
    }

    @Test
    void getIndexPage_shouldRedirectForAuthenticatedUserWithCustomPrincipal() {

        Authentication authentication =
                mock(Authentication.class);

        when(authentication.isAuthenticated())
                .thenReturn(true);

        Object principal = new Object();

        when(authentication.getPrincipal())
                .thenReturn(principal);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        String result =
                controller.getIndexPage();

        assertEquals(
                "redirect:/dashboard",
                result
        );
    }
}