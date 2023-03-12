package com.study.content.clients;

import com.study.content.config.MultipartSupportConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MediaServiceClientTest {

    @Autowired
    private MediaServiceClient mediaServiceClient;

    @Test
    void uploadImage() {
        File file = new File("D:\\header.jpg");
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);

        mediaServiceClient.uploadImage(multipartFile, "image/header.jpg");
    }
}