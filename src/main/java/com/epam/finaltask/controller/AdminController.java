package com.epam.finaltask.controller;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.User;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final VoucherService voucherService;

    // Управление пользователями
    @GetMapping("/users")
    public String manageUsers(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "") String keyword,
                              Model model) {
        int pageSize = 5;
        Page<User> userPage = userService.findUsers(keyword, page, pageSize);

        model.addAttribute("userPage", userPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());

        return "admin/users";
    }

    @PostMapping("/users/toggle-status")
    public String toggleUserStatus(@RequestParam String username,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "") String keyword) {
        userService.toggleUserStatus(username);
        return "redirect:/admin/users?page=" + page + "&keyword=" + keyword;
    }

    // Управление турами для Администратора
    @GetMapping("/vouchers")
    public String manageVouchers(Model model) {
        model.addAttribute("vouchers", voucherService.findAll());
        model.addAttribute("voucherDTO", new VoucherDTO());
        return "admin/vouchers";
    }

    @PostMapping("/vouchers/create")
    public String createVoucher(@ModelAttribute VoucherDTO voucherDTO) {
        voucherService.create(voucherDTO);
        return "redirect:/admin/vouchers";
    }

    @PostMapping("/vouchers/delete")
    public String deleteVoucher(@RequestParam String id) {
        voucherService.delete(id);
        return "redirect:/admin/vouchers";
    }

    @PostMapping("/vouchers/update")
    public String updateVoucher(@RequestParam String id, @ModelAttribute VoucherDTO voucherDTO) {
        voucherService.update(id, voucherDTO);
        return "redirect:/admin/vouchers";
    }
}