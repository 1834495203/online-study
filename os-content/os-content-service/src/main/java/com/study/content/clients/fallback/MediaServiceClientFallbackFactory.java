package com.study.content.clients.fallback;

import com.study.content.clients.MediaServiceClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {
    //拿到了熔断的异常消息
    @Override
    public MediaServiceClient create(Throwable throwable) {
        return new MediaServiceClient() {
            //发生熔断调用此方法
            @Override
            public String uploadImage(MultipartFile file, String objectName) {
                log.error("远程调用上传文件的接口发生了熔断{}", throwable.getMessage());
                return null;
            }
        };
    }
}
