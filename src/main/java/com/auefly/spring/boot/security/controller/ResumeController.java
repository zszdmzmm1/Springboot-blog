package com.auefly.spring.boot.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class ResumeController {
    @GetMapping("/resume")
    String resume() {
        return "resume/resume";
    }
}
