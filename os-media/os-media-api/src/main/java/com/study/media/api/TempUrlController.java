package com.study.media.api;

import com.study.media.service.TempUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TempUrlController {

    @Autowired
    private TempUrlService tempUrlService;

    @RequestMapping(value = "videos", method = RequestMethod.GET)
    public String getTempUrl(){
        return tempUrlService.getTempUrl();
    }
}
