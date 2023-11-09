package com.auefly.spring.boot.security.controller.backend;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BackendController {

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('admin')")
    String adminDashboard() {
        return "backend/dashboard";
    }

}
