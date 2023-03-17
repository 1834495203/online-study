package com.study.content.clients;

import com.study.content.config.MultipartSupportConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@SpringBootTest
class MediaServiceClientTest {

    @Autowired
    private MediaServiceClient mediaServiceClient;

    @Test
    void uploadImage() {
        File file = new File("D:\\header.jpg");
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);

        String s = mediaServiceClient.uploadImage(multipartFile, "image/header.jpg");
        if (s == null) System.out.println("走了降级逻辑");
    }
}