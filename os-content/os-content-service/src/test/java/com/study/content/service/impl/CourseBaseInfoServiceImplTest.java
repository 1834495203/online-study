package com.study.content.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import com.study.base.model.PageParams;
import com.study.base.model.PageResult;
import com.study.content.model.dto.QueryCourseParamsDto;
import com.study.content.model.po.CourseBase;
import com.study.content.service.CourseBaseInfoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.CompareTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

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

    @Test
    void testTime() throws ParseException {
        DateTime of = DateTime.of("2023-3-6", "yyyy-MM-dd");
        DateTime dateTime = new DateTime(System.currentTimeMillis());
        System.out.println(dateTime.between(of, DateUnit.DAY));
    }

    @Test
    void getCourseBaseInfo() {
        System.out.println("courseBaseInfoService.getCourseBaseInfo(118L) = " + courseBaseInfoService.getCourseBaseInfo(118L));
    }
}