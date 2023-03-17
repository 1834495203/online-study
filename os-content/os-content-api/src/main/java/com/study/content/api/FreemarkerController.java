package com.study.content.api;

import com.study.content.clients.MediaServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FreemarkerController {

    @RequestMapping(value = "/test/freemarker", method = RequestMethod.GET)
    public ModelAndView test(){
        ModelAndView modelAndView = new ModelAndView();
        //指定模型
        modelAndView.addObject("names", "裴橘");
        //指定模板
        modelAndView.setViewName("test");
        return modelAndView;
    }
}
