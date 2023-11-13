package com.auefly.spring.boot.security.controller.backend;

import com.auefly.spring.boot.security.dto.PostDto;
import com.auefly.spring.boot.security.entity.Post;
import com.auefly.spring.boot.security.exception.PostNotFoundException;
import com.auefly.spring.boot.security.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class PostController {
    @Autowired
    PostService postService;

    @Value("${custom.upload.base-path}")
    String uploadBasePath;

    @Value("${custom.upload.post-cover-dir-under-base-path}")
    String postCoverDirPostUnderBasePath;

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
    String posts(@RequestParam(name = "coverFile", required = false) MultipartFile coverFile,
                 @Valid @ModelAttribute("post") PostDto postDto,
                 BindingResult bindingResult) throws IOException {
        if (bindingResult.hasErrors()) {
            return "backend/post/create";
        }
        if (coverFile != null && !coverFile.isEmpty()) {
            File dir = new File(uploadBasePath + File.separator + postCoverDirPostUnderBasePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String originalFile = coverFile.getOriginalFilename();
            assert originalFile != null;
            String suffix = originalFile.substring(originalFile.lastIndexOf("."));
            String newFileName = UUID.randomUUID() + suffix;
            coverFile.transferTo(new File(dir.getAbsolutePath() + File.separator + newFileName));
            postDto.setCover(File.separator + postCoverDirPostUnderBasePath + File.separator + newFileName);
        }
        postService.savePost(postDto);
        return "redirect:/admin/posts";
    }

    @DeleteMapping("/admin/post/destroy")
    @ResponseBody
    String delete(@RequestParam(name = "ids[]") List<Long> ids) {
        postService.deleteByIds(ids);
        return "done";
    }

    @DeleteMapping("/admin/post/destroy/{id}")
    String delete(@PathVariable Long id) {
        postService.deleteByid(id);
        return "redirect:/admin/posts";
    }

    @GetMapping("/admin/post/edit/{id}")
    String edit(@PathVariable Long id, Model model) {
        Optional<Post> optionalPost = postService.findById(id);
        if (optionalPost.isEmpty()) {
            throw new PostNotFoundException();
        } else {
            Post post = optionalPost.get();
            model.addAttribute("post", post);
            return "backend/post/edit";
        }
    }

    @PutMapping("/admin/post/update")
    @PreAuthorize("#postDto.userId == authentication.principal.user.id")
    String update(@Valid @ModelAttribute("post") PostDto postDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("post", postDto);
            return "backend/post/edit";
        }

        postService.savePost(postDto);

        return "redirect:/admin/posts";
    }
}
