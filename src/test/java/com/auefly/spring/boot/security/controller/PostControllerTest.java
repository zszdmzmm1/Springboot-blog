package com.auefly.spring.boot.security.controller;

import com.auefly.spring.boot.security.entity.Post;
import com.auefly.spring.boot.security.entity.User;
import com.auefly.spring.boot.security.repository.PostRepository;
import org.hamcrest.Matchers;
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
public class PostControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    PostRepository postRepository;

    @Test
    @DisplayName("测试post展示页面")
    void show() throws Exception {
        Post post = new Post();
        post.setCreated_at(LocalDateTime.now());
        String title = UUID.randomUUID().toString();
        post.setTitle(title);
        post.setContent(UUID.randomUUID().toString());
        post.setUser(new User(1L));
        postRepository.save(post);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/posts/" + post.getId()))
                .andExpect(MockMvcResultMatchers.view().name("post/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("post"))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(title)))
        ;

        postRepository.deleteById(post.getId());
    }
}
