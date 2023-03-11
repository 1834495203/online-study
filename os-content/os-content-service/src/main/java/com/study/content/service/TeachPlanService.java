package com.study.content.service;

import com.study.content.model.dto.BindTeachPlanMediaDto;
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

    /**
     * 教学计划绑定媒资
     * @param bindTeachPlanMediaDto 教学计划-媒资绑定提交数据
     */
    void associationMedia(BindTeachPlanMediaDto bindTeachPlanMediaDto);
}
