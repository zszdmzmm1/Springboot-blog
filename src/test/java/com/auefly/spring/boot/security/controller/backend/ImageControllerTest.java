package com.auefly.spring.boot.security.controller.backend;

import com.auefly.spring.boot.security.controller.WithMockUserForAdminBaseTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.nio.file.Files;

import static org.hamcrest.Matchers.is;

@WithUserDetails(userDetailsServiceBeanName = "jpaUserDetailsService", value = "admin@example.com")
class ImageControllerTest extends WithMockUserForAdminBaseTest {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Environment env;

    @Test
    @DisplayName("测试image upload")
    void uploadImageTest() throws Exception {

        String originalFileName = "example.png";
        MockMultipartFile multipartFile = new MockMultipartFile("file[]", originalFileName, MediaType.IMAGE_PNG_VALUE, new byte[]{1, 2, 3});
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/backend/images/upload-form-vidtor")
                        .file(multipartFile)
                )
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("data.succMap['"
                        + originalFileName
                        + "']").exists())
                .andReturn();
        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        String file = jsonNode.get("data").get("succMap").get(originalFileName).textValue();
        File coverOnDisk = new File(env.getProperty("custom.upload.base-path") + file);
        Assertions.assertTrue(Files.exists(coverOnDisk.toPath()));
        Assertions.assertTrue(coverOnDisk.delete());
    }

    @Test
    @DisplayName("测试下载外部图片， 转换url")
    void uploadLinkFromPasteToVditor() throws Exception {
        String link = "http://xpicx.oss-cn-shenzhen.aliyuncs.com/uPic/send-email-2.png";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .post("/backend/images/re-upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"" + link + "\"}")
                )
                .andExpect(MockMvcResultMatchers.jsonPath("code", is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("msg", is("外部图片链接已成功上传到服务器")))
                .andExpect(MockMvcResultMatchers.jsonPath("data.originalURL", is(link)))
                .andExpect(MockMvcResultMatchers.jsonPath("data.url").exists())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        String file = jsonNode.get("data").get("url").textValue();
        File coverOnDisk = new File(env.getProperty("custom.upload.base-path") + file);
        Assertions.assertTrue(Files.exists(coverOnDisk.toPath()));
        Assertions.assertTrue(coverOnDisk.delete());
    }
}
