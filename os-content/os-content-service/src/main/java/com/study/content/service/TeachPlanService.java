package com.study.content.service;

import com.study.content.model.dto.SaveTeachPlanDto;
import com.study.content.model.dto.TeachPlanDto;

import java.util.List;

public interface TeachPlanService {

    /**
     * 获取课程的全部计划
     * @param courseId 课程id
     * @return 课程的全部计划 包括二级计划
     */
    List<TeachPlanDto> findTeachPlanTree(Long courseId);

    /**
     * 添加或修改课程计划
     * @param saveTeachPlanDto 课程计划
     */
    void saveTeachPlan(SaveTeachPlanDto saveTeachPlanDto);
}
