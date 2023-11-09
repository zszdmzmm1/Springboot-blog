package com.auefly.spring.boot.security.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


class UserControllerTest extends WithMockUserForAdminBaseTest{
    @Test
    @DisplayName("用户管理页面存在page attribute")
    void usersListTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/users"))
                .andExpect(MockMvcResultMatchers.view().name("backend/user/index"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("page"))
        ;
    }
}
