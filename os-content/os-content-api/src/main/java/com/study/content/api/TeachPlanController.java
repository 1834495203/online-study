package com.study.content.api;

import com.study.content.model.dto.BindTeachPlanMediaDto;
import com.study.content.model.dto.SaveTeachPlanDto;
import com.study.content.model.dto.TeachPlanDto;
import com.study.content.service.TeachPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "课程计划管理相关的接口", tags = "课程计划管理相关的接口")
@RestController
@Slf4j
public class TeachPlanController {

    @Autowired
    private TeachPlanService teachPlanService;

    @ApiOperation("获取某课程的课程计划")
    @RequestMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachPlanDto> getTreeNodes(@PathVariable Long courseId){
        return teachPlanService.findTeachPlanTree(courseId);
    }

    @ApiOperation("添加或修改课程计划")
    @RequestMapping(value = "teachplan", method = RequestMethod.POST)
    public void saveTeachPlan(@RequestBody SaveTeachPlanDto saveTeachPlanDto){
        teachPlanService.saveTeachPlan(saveTeachPlanDto);
    }

    @ApiModelProperty("教学计划和媒资信息绑定")
    @RequestMapping(value = "teachplan/association/media", method = RequestMethod.POST)
    public void associationMedia(@RequestBody BindTeachPlanMediaDto bindTeachPlanMediaDto){

    }
}
