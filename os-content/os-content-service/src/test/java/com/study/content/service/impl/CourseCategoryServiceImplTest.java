package com.study.content.service.impl;

import cn.hutool.core.lang.Assert;
import com.study.content.model.dto.CourseCategoryTreeDto;
import com.study.content.service.CourseCategoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CourseCategoryServiceImplTest {

    @Autowired
    private CourseCategoryService courseCategoryService;

    @Test
    void queryTreeNodes() {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryService.queryTreeNodes("1-1");
        Assertions.assertNotNull(courseCategoryTreeDtos);
    }
}