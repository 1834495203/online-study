package com.study.content.service.impl;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.io.IOUtil;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

@SpringBootTest
public class FreemarkerTest {

    @Test
    public void testGenerateHtml() throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.getVersion());

        //拿到classpath路径
        String path = Objects.requireNonNull(this.getClass().getResource("/")).getPath();

        //指定模板目录
        configuration.setDirectoryForTemplateLoading(new File(path+"/templates/"));

        //指定编码
        configuration.setDefaultEncoding("UTF-8");

        //得到模板
        Template template = configuration.getTemplate("test.ftl");

        HashMap<String, Object> map = new HashMap<>();

        map.put("name", "裴橘");

        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);

        //输入流
        InputStream inputStream = IOUtils.toInputStream(html, "utf-8");
        //输出文件
        FileOutputStream fileOutputStream = new FileOutputStream("D:\\test.html");

        IOUtils.copy(inputStream, fileOutputStream);
    }
}
