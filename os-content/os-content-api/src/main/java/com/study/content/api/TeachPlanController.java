package com.study.content.api;

import com.study.content.mapper.TeachplanMapper;
import com.study.content.model.dto.TeachPlanDto;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "课程计划管理相关的接口", tags = "课程计划管理相关的接口")
@RestController
@Slf4j
public class TeachPlanController {

    @Autowired
    private TeachplanMapper teachplanMapper;

    @RequestMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachPlanDto> getTreeNodes(@PathVariable Long courseId){
        return teachplanMapper.selectTreeNodes(courseId);
    }
}
