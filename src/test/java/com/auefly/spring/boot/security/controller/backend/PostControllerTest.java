package com.auefly.spring.boot.security.controller.backend;

import com.auefly.spring.boot.security.controller.WithMockUserForAdminBaseTest;
import com.auefly.spring.boot.security.entity.Post;
import com.auefly.spring.boot.security.repository.PostRepository;
import com.auefly.spring.boot.security.service.PostService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;
import java.util.UUID;

@WithUserDetails(userDetailsServiceBeanName = "jpaUserDetailsService", value = "admin@example.com")
public class PostControllerTest extends WithMockUserForAdminBaseTest {
    @Test
    @DisplayName("管理posts页面test")
    void backendModelTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/posts"))
                .andExpect(MockMvcResultMatchers.view().name("backend/post/index"))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("posts")))
        ;
    }

    @Test
    @DisplayName("访问增加post页面正确")
    void creatPostPageTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/posts/create"))
                .andExpect(MockMvcResultMatchers.view().name("backend/post/create"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("post"))
        ;
    }

    @Test
    @DisplayName("新建文章，非空字段不可为空")
    void creatPostFailedTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/posts")
                        .param("id", "")
                        .param("title", "test")
                        .param("content", "")
                        .param("userId", "1")
                )
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("post", "content", "NotEmpty"))
        ;
    }

    @Test
    @DisplayName("新建post成功")
    void creatPostTest(@Autowired PostRepository postRepository) throws Exception {
        String title = UUID.randomUUID().toString().substring(0, 6);
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/posts")
                        .param("id", "")
                        .param("title", "test" + title)
                        .param("content", "君不见黄河之水天上来，奔流到海不复回。")
                        .param("userId", "1")
                )
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/posts"))
        ;
        Optional<Post> post = postRepository.findFirstByTitle("test" + title);
        Assertions.assertTrue(post.isPresent());
        postRepository.delete(post.get());
    }
}
