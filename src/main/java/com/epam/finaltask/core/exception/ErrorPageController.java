package com.epam.finaltask.core.exception;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorPageController {

    @GetMapping("/test-error")
    public String testError() {
        throw new RuntimeException("Test error");
    }
}