package com.study.media.service.impl;

import com.study.media.service.TempUrlService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

@Service
public class TempUrlServiceImpl implements TempUrlService {

    @Autowired
    private MinioClient minioClient;

    @Override
    public String getTempUrl() {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket("testbuckets")
                            .method(Method.GET)
                            .expiry(10, TimeUnit.SECONDS)
                            .object("test.mp4")
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
