package com.study.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("media-api")
public interface MediaClient {

    @RequestMapping(value = "media/videos", method = RequestMethod.GET)
    String getUrl();
}
