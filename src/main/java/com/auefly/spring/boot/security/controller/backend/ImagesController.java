package com.auefly.spring.boot.security.controller.backend;

import com.auefly.spring.boot.security.util.R;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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

        for (MultipartFile image : images) {
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

    @PostMapping("/backend/images/re-upload")
    R imagesReUploads(@RequestBody Map<String, String> body,
                      HttpServletRequest request) {
        R r = new R();
        if (!body.containsKey("url")) {
            r.setMsg("无效的 url");
            r.setCode(0);
            r.setData(null);

            return r;
        }

        String url = body.get("url");
        Map<String, String> data = new HashMap<>();
        if (url.startsWith(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort())) {
            r.setCode(0);
            r.setMsg("地址已在服务器中，无需额外处理");
            data.put("originalURL", url);
            data.put("ur", url);
            r.setData(data);

            return r;
        }

        File dir = new File(uploadBasePath + File.separator + fromVditorDirUnderBasePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String extension = FilenameUtils.getExtension(url);
        String newFilename = UUID.randomUUID() + "." + extension;
        try {
            FileUtils.copyURLToFile(new URL(url), new File(dir.getAbsolutePath() + File.separator + newFilename));
            r.setMsg("外部图片链接已成功上传到服务器");
            r.setCode(0);
            data.put("originalURL", url);
            data.put("url", "/" + fromVditorDirUnderBasePath + File.separator + newFilename);
            r.setData(data);
        } catch (IOException e) {
            r.setCode(-1);
            r.setMsg("图片处理异常：" + e.getMessage());
            data.put("originalURL", url);
            data.put("url", url);
            r.setData(data);
            throw new RuntimeException(e);
        }

        return r;
    }

    @lombok.Data
    private static class Data {
        private List<String> errorFiles;
        private Map<String, String> succMap;
    }
}
