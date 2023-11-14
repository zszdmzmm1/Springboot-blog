package com.auefly.spring.boot.security.controller.backend;

import com.auefly.spring.boot.security.util.R;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Controller
public class ImagesController {
    @PostMapping("/backend/images/upload-form-vidtor")
    @ResponseBody
    R imagesUpload(@RequestParam("file[]") MultipartFile image) throws IOException {
        if (!image.isEmpty()) {
            File dir = new File("upload" + File.separator + "image");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String originalFile = image.getOriginalFilename();
            assert originalFile != null;
            String suffix = originalFile.substring(originalFile.lastIndexOf("."));
            String newFileName = UUID.randomUUID() + suffix;
            image.transferTo(new File(dir.getAbsolutePath() + File.separator + newFileName));
            R r = new R();
            r.setCode(0);
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("succMap", Map.of("avatar.png", File.separator + "image" + File.separator + newFileName));
            r.setData(dataMap);
            return r;
        }
        return null;
    }
}
