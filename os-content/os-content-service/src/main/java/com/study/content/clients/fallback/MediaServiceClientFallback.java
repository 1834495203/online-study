package com.study.content.clients.fallback;

import com.study.content.clients.MediaServiceClient;
import org.springframework.web.multipart.MultipartFile;

public abstract class MediaServiceClientFallback implements MediaServiceClient {

    @Override
    public String uploadImage(MultipartFile file, String objectName) {

        return null;
    }
}
