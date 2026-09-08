package com.epam.finaltask.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MessageSource messageSource;

    @GetMapping("/auth/sign-in")
    public String getSignInPage() {
        return "auth/sign-in";
    }

    @PostMapping("/auth/sign-in")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletResponse response) {

        try {

            LoginRequestDTO request = LoginRequestDTO.builder()
                    .username(username)
                    .password(password)
                    .build();

            String token = authService.login(request);

            Cookie cookie = new Cookie("JWT", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60);

            response.addCookie(cookie);

            return "redirect:/dashboard";

        } catch (DisabledException e) {

            return "redirect:/auth/sign-in?blocked";

        } catch (BadCredentialsException e) {

            return "redirect:/auth/sign-in?error";
        }
    }

    @GetMapping("/auth/logout")
    public String logout(HttpServletResponse response) {

        Cookie cookie = new Cookie("JWT", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return "redirect:/";
    }

    @GetMapping("/auth/sign-up")
    public String getSignUpPage(Model model) {

        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .build();

        model.addAttribute("registerRequest", request);

        return "auth/sign-up";
    }

    @PostMapping("/auth/sign-up")
    public String registerUser(
            @Valid @ModelAttribute("registerRequest")
            RegisterRequestDTO request,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "auth/sign-up";
        }

        try {
            authService.register(request);

            return "redirect:/auth/sign-in?registered";

        } catch (IllegalArgumentException e) {

            String message = messageSource.getMessage(
                    e.getMessage(),
                    null,
                    e.getMessage(),
                    LocaleContextHolder.getLocale()
            );

            model.addAttribute("registrationError", message);

            return "auth/sign-up";
        }
    }
}

