package com.auefly.spring.boot.security.controller;

import com.auefly.spring.boot.security.entity.Post;
import com.auefly.spring.boot.security.entity.User;
import com.auefly.spring.boot.security.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public class ResourceControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    PostRepository postRepository;

    @Test
    @DisplayName("错误的url访问post类型资源")
    void showWithIncorrectType() throws Exception {
        Post post = new Post();
        post.setType("post");
        post.setTitle(UUID.randomUUID().toString());
        post.setContent(UUID.randomUUID().toString());
        post.setCreated_at(LocalDateTime.now());
        post.setUser(new User(1L));
        postRepository.save(post);

        mockMvc.perform(MockMvcRequestBuilders.get("/resource/" + post.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
        ;

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/posts/" + post.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
        ;

        postRepository.deleteById(post.getId());
    }
}
