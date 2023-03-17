package com.study.content.clients;

import com.study.content.clients.fallback.MediaServiceClientFallbackFactory;
import com.study.content.config.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * 远程调用media接口, 使用fallback不能拿到异常, 使用factory拿到异常信息
 */
@FeignClient(value = "media-api", configuration = MultipartSupportConfig.class,
        fallback = MediaServiceClientFallbackFactory.class)
public interface MediaServiceClient {

    @RequestMapping(value = "media/upload/courseFile", method = RequestMethod.POST,
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadImage(@RequestPart("file") MultipartFile file,
                                    @RequestPart(value = "objectName", required = false) String objectName);
}
