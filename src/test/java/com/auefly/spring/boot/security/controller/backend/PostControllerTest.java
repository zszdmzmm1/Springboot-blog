package com.auefly.spring.boot.security.controller.backend;

import com.auefly.spring.boot.security.controller.WithMockUserForAdminBaseTest;
import com.auefly.spring.boot.security.entity.Post;
import com.auefly.spring.boot.security.repository.PostRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.nio.file.Files;
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

    @Test
    @DisplayName("测试图片封面上传")
    void storeWithCoverImage(@Autowired PostRepository postRepository, @Autowired Environment env) throws Exception {
        String title = "title_" + UUID.randomUUID();
        MockMultipartFile coverFile = new MockMultipartFile("coverFile", "cover.png", MediaType.IMAGE_PNG_VALUE, new byte[]{1, 2, 3});
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/admin/posts")
                        //.contentType(MediaType.MULTIPART_FORM_DATA)
                        .file(coverFile)
                        .param("id", "")
                        .param("userId", "1")
                        .param("title", title)
                        .param("content", "content-" + UUID.randomUUID())
                )
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/posts"))
        ;

        Optional<Post> po = postRepository.findFirstByTitle(title);
        Assertions.assertTrue(po.isPresent());

        String cover = po.get().getCover();
        File coverOnDisk = new File(env.getProperty("custom.upload.base-path") + cover);
        Assertions.assertTrue(Files.exists(coverOnDisk.toPath()));
        Assertions.assertTrue(coverOnDisk.delete());

        postRepository.delete(po.get());
    }


    @Test
    @DisplayName("update测试")
    void update(@Autowired PostRepository postRepository) throws Exception {
        String title = "title-" + UUID.randomUUID();
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/posts")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "")
                        .param("userId", "1")
                        .param("title", title)
                        .param("content", "content-" + UUID.randomUUID())
                )
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/posts"))
        ;
        Optional<Post> po = postRepository.findFirstByTitle(title);
        Assertions.assertTrue(po.isPresent());
        Post post = po.get();

        String descriptionUpdated = "description--updated";
        String contendUpdated = post.getContent() + "--updated";
        mockMvc.perform(MockMvcRequestBuilders.put("/admin/post/update")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", post.getId().toString())
                        .param("title", post.getTitle())
                        .param("userId", "1")
                        .param("description", descriptionUpdated)
                        .param("content", contendUpdated)
                )
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/posts"))
        ;

        Post postUpdated = postRepository.findFirstByTitle(post.getTitle()).get();
        Assertions.assertEquals(descriptionUpdated, postUpdated.getDescription());
        Assertions.assertEquals(contendUpdated, postUpdated.getContent());

        postRepository.delete(po.get());
    }

    @Test
    @DisplayName("未使用正确用户无法更新对应文章")
    void updatePostThatNotMyOwn(@Autowired PostRepository postRepository) throws Exception {
        String title = "title-" + UUID.randomUUID();
        String authorId = "2";
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/posts")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "")
                        .param("userId", authorId)
                        .param("title", title)
                        .param("content", "content-" + UUID.randomUUID())
                )
                .andExpect(MockMvcResultMatchers.redirectedUrl("/admin/posts"))
        ;
        Optional<Post> po = postRepository.findFirstByTitle(title);
        Assertions.assertTrue(po.isPresent());
        Post post = po.get();

        String descriptionUpdated = "description--updated";
        String contendUpdated = post.getContent() + "--updated";
        mockMvc.perform(MockMvcRequestBuilders.put("/admin/post/update")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", post.getId().toString())
                        .param("title", post.getTitle())
                        .param("userId", authorId)
                        .param("description", descriptionUpdated)
                        .param("content", contendUpdated)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden())
        ;

        postRepository.delete(po.get());
    }
}
