package com.epam.finaltask.controller;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.VoucherService;
import com.epam.finaltask.token.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UIController {

    private final VoucherService voucherService;
    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String getIndexPage() {
        return "index";
    }

    @GetMapping("/auth/sign-in")
    public String getSignInPage() {
        return "auth/sign-in";
    }

    @PostMapping("/auth/sign-in")
    public String login(@RequestParam String username, @RequestParam String password, HttpServletResponse response) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (passwordEncoder.matches(password, userDetails.getPassword())) {
                if (!userDetails.isEnabled()) return "redirect:/auth/sign-in?blocked";
                String token = jwtUtils.generateToken(username);
                Cookie cookie = new Cookie("JWT", token);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(24 * 60 * 60);
                response.addCookie(cookie);
                return "redirect:/dashboard";
            }
        } catch (Exception ignored) {}
        return "redirect:/auth/sign-in?error";
    }

    @GetMapping("/auth/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("JWT", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }

    @GetMapping("/dashboard")
    public String getDashboardPage(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String tourType,
            @RequestParam(required = false) String hotelType,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean isHot,
            @RequestParam(defaultValue = "isHot") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            Principal principal, Model model) {

        User user = userRepository.findUserByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Page<VoucherDTO> vouchersPage = voucherService.findAvailableVouchers(keyword, tourType, hotelType, maxPrice, isHot, page, 5, sortField, sortDir);
        List<VoucherDTO> myVouchers = voucherService.findAllByUserId(user.getId().toString());

        model.addAttribute("currentUser", user);
        model.addAttribute("vouchersPage", vouchersPage);
        model.addAttribute("myVouchers", myVouchers);
        model.addAttribute("keyword", keyword);
        model.addAttribute("tourType", tourType);
        model.addAttribute("hotelType", hotelType);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("isHot", isHot);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("currentPage", page);

        return "user/dashboard";
    }

    @PostMapping("/vouchers/order")
    public String orderVoucher(@RequestParam String voucherId, Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName()).get();
        voucherService.order(voucherId, user.getId().toString());
        return "redirect:/dashboard?ordered";
    }

    @GetMapping("/auth/sign-up")
    public String getSignUpPage(Model model) {
        UserDTO userDTO = new UserDTO();
        userDTO.setRole("USER");
        userDTO.setBalance(5000.0);
        userDTO.setActive(true);

        model.addAttribute("userDTO", userDTO);
        return "auth/sign-up";
    }

    @PostMapping("/auth/sign-up")
    public String registerUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) return "auth/sign-up";
        try {
            userService.register(userDTO);
            return "redirect:/auth/sign-in?registered";
        } catch (Exception e) {
            model.addAttribute("registrationError", e.getMessage());
            return "auth/sign-up";
        }
    }

    @GetMapping("/profile")
    public String getProfilePage(Principal principal, Model model) {
        User user = userRepository.findUserByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Получаем туры конкретного пользователя
        List<VoucherDTO> myVouchers = voucherService.findAllByUserId(user.getId().toString());

        model.addAttribute("user", user);
        model.addAttribute("myVouchers", myVouchers); // Передаем их на страницу
        return "user/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute UserDTO userDTO, Principal principal) {
        try {
            userService.updateUserProfile(principal.getName(), userDTO);
        } catch (IllegalArgumentException e) {
            return "redirect:/profile?error";
        }
        return "redirect:/profile?success";
    }
}