package com.auefly.spring.boot.security.controller;

import com.auefly.spring.boot.security.bean.backend.BackendProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


class BackendControllerTest extends WithMockUserForAdminBaseTest{
    @Autowired
    BackendProperties backendProperties;

    @Test
    @DisplayName("测试添加进page的model是否生效")
    void backendModelTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/dashboard"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("menus"))
                .andExpect(MockMvcResultMatchers.model().attribute("menus", backendProperties.getMenus()))
        ;
    }

    @Test
    @DisplayName("csrf.disable(),使thymeleaf模板引擎自动向你发送了一个验证的token，缺少这个token将访问失败。" +
            "在访问互联网时，模板引擎会自动添加，无需添加，但在测试中没有经过模板引擎，需要添加")
    void logoutTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/logout"))
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));
    }
}
