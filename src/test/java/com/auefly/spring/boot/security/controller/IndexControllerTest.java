package com.auefly.spring.boot.security.controller;

import com.auefly.spring.boot.security.entity.PasswordResetToken;
import com.auefly.spring.boot.security.entity.User;
import com.auefly.spring.boot.security.repository.PasswordResetTokenRepository;
import com.auefly.spring.boot.security.repository.UserRepository;
import org.hamcrest.Matchers;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
class IndexControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void indexTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.view().name("index")
                )
        ;
    }

    @Test
    void loginTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(MockMvcResultMatchers.view().name("login"))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("登录"))
                )
        ;
    }

    @Test
    @DisplayName("测试添加cookie")
    void loginCookieTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "user")
                        .param("password", "password")
                        .param("remember", "on")
                )
                .andExpect(MockMvcResultMatchers.cookie().exists("remember-me"))
        ;
    }

    @Test
    @DisplayName("用户无邮箱登录失败跳转")
    void loginByNameExpectFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "user")
                        .param("password", "password")
                )
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/login?error"))
        ;
    }

    @Test
    @DisplayName("用户用邮箱登录成功")
    void loginByEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "user@example.com")
                        .param("password", "password")
                )
                .andExpect(SecurityMockMvcResultMatchers.authenticated())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"))
        ;
    }

    @Test
    @DisplayName("已存在的邮箱不可注册")
    void userRegisterWithExistingEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "new-name")
                        .param("email", "admin@example.com")
                        .param("password", "secret")
                )
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("user", "email", "exist"))
        ;
    }

    @Test
    @DisplayName("自动登录")
    void autoLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "hjf")
                        .param("email", UUID.randomUUID().toString().substring(0, 7) + "@example.com")
                        .param("password", "123456")
                )
                .andExpect(SecurityMockMvcResultMatchers.authenticated())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/users/dashboard"))
        ;
    }

    @Test
    @DisplayName("修改密码请求发送成功(邮箱存在)")
    @Disabled
    void passwordChangingRequestSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users/password-reset")
                .param("email", "admin@example.com")
        )
                .andExpect(MockMvcResultMatchers.redirectedUrl("/users/password-reset"))
                .andExpect(MockMvcResultMatchers.flash().attribute("success", "密码重置邮件已发送，请注意查收"))
        ;
    }

    @Test
    @DisplayName("修改密码请求发送成功(邮箱不存在)")
    void passwordChangingRequestFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users/password-reset")
                        .param("email", "not-exist@example.com")
                )
                .andExpect(MockMvcResultMatchers.content().string(StringContains.containsString("找不到该邮箱！")))
        ;
    }

    @Test
    @DisplayName("token错误。")
    void doPasswordResetTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/do-password-reset")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("token", "jfasjfasihgieshgiahgig")
                )
                .andExpect(MockMvcResultMatchers.model().attribute("error", "密码token不存在。"))
        ;
    }


    @Test
    @DisplayName("更改新密码时在此输入密码输入不匹配密码")
    void postResetPasswordWithMismatchConfirmPasswordTest(@Autowired UserRepository userRepository, @Autowired PasswordResetTokenRepository passwordResetTokenRepository) throws Exception {
        //initialize
        User user = new User();
        user.setName(UUID.randomUUID().toString().substring(0, 6));
        user.setEmail(user.getName() + "@example.com");
        user.setEnabled(true);
        userRepository.save(user);
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUser(user);
        String token = UUID.randomUUID().toString();
        passwordResetToken.setToken(token);
        passwordResetToken.setExpirationDate(LocalDateTime.now().plusMinutes(30));
        passwordResetTokenRepository.save(passwordResetToken);

        //test
        mockMvc.perform(MockMvcRequestBuilders.post("/users/do-password-reset")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("token", token)
                .param("password", "new-password")
                .param("confirmPassword", "psw-mismatch")
        )
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("passwordResetDto", "confirmPassword", "PasswordConfirmation"))
        ;

        passwordResetTokenRepository.delete(passwordResetToken);
        userRepository.delete(user);
    }

    @Test
    @DisplayName("更改新密码成功")
    void postResetPasswordSuccessTest(@Autowired UserRepository userRepository, @Autowired PasswordResetTokenRepository passwordResetTokenRepository) throws Exception {
        //initialize
        User user = new User();
        user.setName(UUID.randomUUID().toString().substring(0, 6));
        user.setEmail(user.getName() + "@example.com");
        user.setEnabled(true);
        userRepository.save(user);
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUser(user);
        String token = UUID.randomUUID().toString();
        passwordResetToken.setToken(token);
        passwordResetToken.setExpirationDate(LocalDateTime.now().plusMinutes(30));
        passwordResetTokenRepository.save(passwordResetToken);

        //test
        mockMvc.perform(MockMvcRequestBuilders.post("/users/do-password-reset")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("token", token)
                        .param("password", "new-password")
                        .param("confirmPassword", "new-password")
                )
                .andExpect(MockMvcResultMatchers.redirectedUrl("/login"))
        ;

        passwordResetTokenRepository.delete(passwordResetToken);
        userRepository.delete(user);
    }


    @Test
    @DisplayName("更改新密后token失效")
    @Disabled
    void expiredTokenTest(@Autowired UserRepository userRepository, @Autowired PasswordResetTokenRepository passwordResetTokenRepository) throws Exception {
        //initialize
        User user = new User();
        user.setName(UUID.randomUUID().toString().substring(0, 6));
        user.setEmail(user.getName() + "@example.com");
        user.setEnabled(true);
        userRepository.save(user);
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUser(user);
        String token = UUID.randomUUID().toString();
        passwordResetToken.setToken(token);
        passwordResetToken.setExpirationDate(LocalDateTime.now().plusMinutes(30));
        passwordResetTokenRepository.save(passwordResetToken);

        //test
        mockMvc.perform(MockMvcRequestBuilders.post("/users/do-password-reset")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("token", token)
                        .param("password", "new-password")
                        .param("confirmPassword", "new-password")
                )
                .andExpect(MockMvcResultMatchers.redirectedUrl("/login"))
        ;

        mockMvc.perform(MockMvcRequestBuilders.get("/users/do-password-reset")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("token", token)
                )
                .andExpect(MockMvcResultMatchers.model().attribute("error", "密码token已过期。"))
        ;

        passwordResetTokenRepository.delete(passwordResetToken);
        userRepository.delete(user);
    }

}