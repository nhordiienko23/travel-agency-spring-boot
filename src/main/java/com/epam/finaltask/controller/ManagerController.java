package com.epam.finaltask.controller;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/manager")
@PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
@RequiredArgsConstructor
public class ManagerController {

    private final VoucherService voucherService;

    @GetMapping("/vouchers")
    public String manageVouchers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "isHot") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Page<VoucherDTO> vouchersPage = voucherService.findAllVouchersForManager(keyword, status, page, 5, sortField, sortDir);

        model.addAttribute("vouchersPage", vouchersPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("currentPage", page);

        return "manager/vouchers";
    }

    @PostMapping("/vouchers/toggle-hot")
    public String toggleHotStatus(@RequestParam String id, @RequestParam boolean isHot) {
        com.epam.finaltask.dto.VoucherDTO dto = new com.epam.finaltask.dto.VoucherDTO();
        dto.setIsHot(!isHot);
        voucherService.changeHotStatus(id, dto);
        return "redirect:/manager/vouchers";
    }

    @PostMapping("/vouchers/update-status")
    public String updateStatus(@RequestParam String id, @RequestParam String status) {
        com.epam.finaltask.dto.VoucherDTO dto = new com.epam.finaltask.dto.VoucherDTO();
        dto.setStatus(status);
        voucherService.update(id, dto);
        return "redirect:/manager/vouchers";
    }
}