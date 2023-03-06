package com.study.content.service.impl;

import com.study.base.model.PageParams;
import com.study.base.model.PageResult;
import com.study.content.model.dto.QueryCourseParamsDto;
import com.study.content.model.po.CourseBase;
import com.study.content.service.CourseBaseInfoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CourseBaseInfoServiceImplTest {

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @Test
    void queryCourseBaseList() {
        PageParams pageParams = new PageParams();
        QueryCourseParamsDto dto = new QueryCourseParamsDto();
        dto.setCourseName("java");
        PageResult<CourseBase> list = courseBaseInfoService.queryCourseBaseList(pageParams, dto);
        Assertions.assertNotNull(list);
    }
}