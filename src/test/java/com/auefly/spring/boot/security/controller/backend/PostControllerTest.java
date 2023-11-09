package com.auefly.spring.boot.security.controller.backend;

import com.auefly.spring.boot.security.controller.WithMockUserForAdminBaseTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class PostControllerTest extends WithMockUserForAdminBaseTest {
    @Test
    @DisplayName("管理posts页面test")
    void backendModelTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/posts"))
                .andExpect(MockMvcResultMatchers.view().name("backend/post/index"))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("posts")))
        ;
    }
}
