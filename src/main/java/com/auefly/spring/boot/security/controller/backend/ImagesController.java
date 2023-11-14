package com.auefly.spring.boot.security.controller.backend;

import com.auefly.spring.boot.security.util.R;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;


@RestController
public class ImagesController {
    @Value("${custom.upload.base-path}")
    String uploadBasePath;
    @Value("${custom.upload.from-vditor-dir-under-base-path}")
    String fromVditorDirUnderBasePath;

    @PostMapping("/backend/images/upload-form-vidtor")
    R imagesUpload(@RequestParam("file[]") MultipartFile[] images) {
        List<String> errorFiles = new ArrayList<>();
        Map<String, String> imageMap = new HashMap<>();

        for(MultipartFile image: images){
            if (!image.isEmpty()) {
                File dir = new File(uploadBasePath + File.separator + fromVditorDirUnderBasePath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String originalFile = image.getOriginalFilename();
                assert originalFile != null;
                String suffix = originalFile.substring(originalFile.lastIndexOf("."));
                String newFileName = UUID.randomUUID() + suffix;
                try {
                    image.transferTo(new File(dir.getAbsolutePath() + File.separator + newFileName));
                } catch (IOException e) {
                    errorFiles.add(originalFile);
                }
                imageMap.put(originalFile, File.separator + fromVditorDirUnderBasePath + File.separator + newFileName);
            }
        }
        Data data = new Data();
        R r = new R();
        Map<String, String> succMap = new HashMap<>(imageMap);
        data.setErrorFiles(errorFiles);
        data.setSuccMap(succMap);
        r.setData(data);
        if (errorFiles.isEmpty()) {
            r.setMsg("success");
            r.setCode(0);
        } else {
            r.setMsg("error");
            r.setCode(-1);
        }
        return r;
    }

    @lombok.Data
    private static class Data {
        private List<String> errorFiles;
        private Map<String, String> succMap;
    }
}
