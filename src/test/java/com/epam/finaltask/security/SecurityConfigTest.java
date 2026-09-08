package com.epam.finaltask.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import({
        SecurityConfig.class,
        SecurityConfigTest.TestControllerConfig.class
})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @MockBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @BeforeEach
    void setUp() throws Exception {

        /*
         * We test SecurityConfig, not JwtFilter.
         *
         * Therefore JwtFilter is mocked,
         * but it must continue the filter chain.
         */
        doAnswer(invocation -> {

            FilterChain chain =
                    invocation.getArgument(2);

            chain.doFilter(
                    invocation.getArgument(0),
                    invocation.getArgument(1)
            );

            return null;

        }).when(jwtFilter).doFilter(
                any(),
                any(),
                any()
        );

        /*
         * Simulate the universal AuthenticationEntryPoint.
         *
         * We do not test its internal JSON/redirect logic here.
         * That logic has its own unit test.
         *
         * For SecurityConfig we only need to verify that
         * the correct entry point is called.
         */
        doAnswer(invocation -> {

            HttpServletResponse response =
                    invocation.getArgument(1);

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            return null;

        }).when(customAuthenticationEntryPoint).commence(
                any(),
                any(),
                any()
        );

        /*
         * Simulate the AccessDeniedHandler.
         *
         * We test here that SecurityConfig delegates
         * forbidden access to this handler.
         */
        doAnswer(invocation -> {

            HttpServletResponse response =
                    invocation.getArgument(1);

            response.setStatus(
                    HttpServletResponse.SC_FORBIDDEN
            );

            return null;

        }).when(customAccessDeniedHandler).handle(
                any(),
                any(),
                any()
        );
    }

    // ========================================================================
    // SWAGGER
    // ========================================================================

    @Test
    void swaggerApiDocs_ShouldBePublic()
            throws Exception {

        mockMvc.perform(
                get("/v3/api-docs/test")
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void swaggerUi_ShouldBePublic()
            throws Exception {

        mockMvc.perform(
                get("/swagger-ui/test")
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void swaggerUiHtml_ShouldBePublic()
            throws Exception {

        mockMvc.perform(
                get("/swagger-ui.html")
        ).andExpect(
                status().isOk()
        );
    }

    // ========================================================================
    // PUBLIC PAGES
    // ========================================================================

    @Test
    void root_ShouldBePublic()
            throws Exception {

        mockMvc.perform(
                get("/")
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void signIn_ShouldBePublic()
            throws Exception {

        mockMvc.perform(
                get("/auth/sign-in")
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void signUp_ShouldBePublic()
            throws Exception {

        mockMvc.perform(
                get("/auth/sign-up")
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void logout_ShouldBePublic()
            throws Exception {

        mockMvc.perform(
                get("/auth/logout")
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void error_ShouldBePublic()
            throws Exception {

        mockMvc.perform(
                get("/error")
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void testError_ShouldBePublic()
            throws Exception {

        mockMvc.perform(
                get("/test-error")
        ).andExpect(
                status().isOk()
        );
    }

    // ========================================================================
    // STATIC RESOURCES
    // ========================================================================

    @Test
    void css_ShouldBePublic()
            throws Exception {

        mockMvc.perform(
                get("/css/test")
        ).andExpect(
                status().isNotFound()
        );
    }

    @Test
    void js_ShouldBePublic()
            throws Exception {

        mockMvc.perform(
                get("/js/test")
        ).andExpect(
                status().isNotFound()
        );
    }

    @Test
    void webjars_ShouldBePublic()
            throws Exception {

        mockMvc.perform(
                get("/webjars/test")
        ).andExpect(
                status().isNotFound()
        );
    }

    // ========================================================================
    // REST AUTH
    // ========================================================================

    @Test
    void apiAuth_ShouldBePublic()
            throws Exception {

        mockMvc.perform(
                get("/api/auth/test")
        ).andExpect(
                status().isOk()
        );
    }

    // ========================================================================
    // CSRF
    // ========================================================================

    @Test
    void csrf_ShouldBeDisabled()
            throws Exception {

        mockMvc.perform(
                post("/api/auth/test")
        ).andExpect(
                status().isOk()
        );
    }

    // ========================================================================
    // API AUTHENTICATION
    // ========================================================================

    @Test
    void apiRequest_ShouldRejectAnonymousUser()
            throws Exception {

        mockMvc.perform(
                get("/api/protected")
        ).andExpect(
                status().isUnauthorized()
        );

        verify(customAuthenticationEntryPoint)
                .commence(
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    void apiRequest_ShouldAllowAuthenticatedUser()
            throws Exception {

        mockMvc.perform(
                get("/api/protected")
                        .with(
                                user("john")
                                        .roles("USER")
                        )
        ).andExpect(
                status().isOk()
        );
    }

    // ========================================================================
    // PROTECTED WEB REQUESTS
    // ========================================================================

    @Test
    void protectedRequest_ShouldRejectAnonymousUser()
            throws Exception {

        /*
         * The universal AuthenticationEntryPoint handles
         * non-API requests by redirecting to /error?status=401.
         *
         * In this SecurityConfigTest the entry point is mocked,
         * therefore only 401 delegation is verified.
         */
        mockMvc.perform(
                get("/protected/test")
        ).andExpect(
                status().isUnauthorized()
        );

        verify(customAuthenticationEntryPoint)
                .commence(
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    void protectedRequest_ShouldAllowAuthenticatedUser()
            throws Exception {

        mockMvc.perform(
                get("/protected/test")
                        .with(
                                user("john")
                                        .roles("USER")
                        )
        ).andExpect(
                status().isOk()
        );
    }

    // ========================================================================
    // METHOD SECURITY
    // ========================================================================

    @Test
    void adminEndpoint_ShouldAllowAdmin()
            throws Exception {

        mockMvc.perform(
                get("/admin/test")
                        .with(
                                user("admin")
                                        .roles("ADMIN")
                        )
        ).andExpect(
                status().isOk()
        );
    }

    @Test
    void adminEndpoint_ShouldRejectUserWith403()
            throws Exception {

        mockMvc.perform(
                get("/admin/test")
                        .with(
                                user("john")
                                        .roles("USER")
                        )
        ).andExpect(
                status().isForbidden()
        );

        verify(customAccessDeniedHandler)
                .handle(
                        any(),
                        any(),
                        any()
                );
    }

    // ========================================================================
    // ANY OTHER REQUEST
    // ========================================================================

    @Test
    void anyOtherRequest_ShouldRejectAnonymousUser()
            throws Exception {

        mockMvc.perform(
                get("/some-random-endpoint")
        ).andExpect(
                status().isUnauthorized()
        );

        verify(customAuthenticationEntryPoint)
                .commence(
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    void anyOtherRequest_ShouldAllowAuthenticatedUser()
            throws Exception {

        mockMvc.perform(
                get("/some-random-endpoint")
                        .with(
                                user("john")
                                        .roles("USER")
                        )
        ).andExpect(
                status().isNotFound()
        );
    }

    // ========================================================================
    // PASSWORD ENCODER
    // ========================================================================

    @Test
    void passwordEncoder_ShouldBeConfigured() {

        assertNotNull(passwordEncoder);

        String rawPassword =
                "Password123!";

        String encodedPassword =
                passwordEncoder.encode(
                        rawPassword
                );

        assertNotNull(encodedPassword);

        assertTrue(
                passwordEncoder.matches(
                        rawPassword,
                        encodedPassword
                )
        );
    }

    // ========================================================================
    // TEST CONTROLLER CONFIGURATION
    // ========================================================================

    @Configuration
    static class TestControllerConfig {

        @Bean
        TestController testController() {
            return new TestController();
        }
    }

    // ========================================================================
    // TEST CONTROLLER
    // ========================================================================

    @RestController
    static class TestController {

        @GetMapping("/")
        String root() {
            return "root";
        }

        @GetMapping("/auth/sign-in")
        String signIn() {
            return "sign-in";
        }

        @GetMapping("/auth/sign-up")
        String signUp() {
            return "sign-up";
        }

        @GetMapping("/auth/logout")
        String logout() {
            return "logout";
        }

        @GetMapping("/error")
        String error() {
            return "error";
        }

        @GetMapping("/test-error")
        String testError() {
            return "test-error";
        }

        @GetMapping("/api/auth/test")
        String apiAuth() {
            return "api-auth";
        }

        /*
         * This POST endpoint exists specifically
         * to verify that CSRF is disabled.
         */
        @PostMapping("/api/auth/test")
        String apiAuthPost() {
            return "api-auth-post";
        }

        @GetMapping("/api/protected")
        String apiProtected() {
            return "api-protected";
        }

        @GetMapping("/v3/api-docs/test")
        String swaggerApiDocs() {
            return "swagger";
        }

        @GetMapping("/swagger-ui/test")
        String swaggerUi() {
            return "swagger-ui";
        }

        @GetMapping("/swagger-ui.html")
        String swaggerUiHtml() {
            return "swagger-ui-html";
        }

        @GetMapping("/protected/test")
        String protectedEndpoint() {
            return "protected";
        }

        @GetMapping("/admin/test")
        @PreAuthorize("hasRole('ADMIN')")
        String adminEndpoint() {
            return "admin";
        }
    }
}

