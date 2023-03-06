package com.study.content.api;

import com.study.content.model.dto.CourseCategoryTreeDto;
import com.study.content.service.CourseCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 课程的类型 树形结构
 */
@RestController
@Slf4j
@Api(value = "课程分类相关接口", tags = "课程分类相关接口")
public class CourseCategoryController {

    @Autowired
    private CourseCategoryService courseCategoryService;

    @ApiOperation("课程分类查询接口")
    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes() {
        //1 查询根节点以下的全部节点
        return courseCategoryService.queryTreeNodes("1");
    }
}
