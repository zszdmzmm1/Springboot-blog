package com.auefly.spring.boot.security.controller.backend;

import com.auefly.spring.boot.security.dto.PostDto;
import com.auefly.spring.boot.security.entity.Post;
import com.auefly.spring.boot.security.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class PostController {
    @Autowired
    PostService postService;

    @GetMapping("/admin/posts")
    String post(Model model,
              @RequestParam Optional<Integer> page,
              @RequestParam Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        Page<Post> pageContent = postService.findAll(currentPage, pageSize);
        model.addAttribute("page", pageContent);
        return "backend/post/index";
    }

    @GetMapping("/admin/posts/create")
    String creat(Model model) {
        model.addAttribute("post", new Post());
        return "backend/post/create";
    }

    @PostMapping("/admin/posts")
    String posts(@Valid @ModelAttribute("post") PostDto postDto,
                 BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "backend/post/create";
        }
        postService.savePost(postDto);
        return "redirect:/admin/posts";
    }
}
