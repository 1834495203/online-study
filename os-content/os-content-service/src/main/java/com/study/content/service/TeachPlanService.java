package com.study.content.service;

import com.study.content.model.dto.TeachPlanDto;

import java.util.List;

public interface TeachPlanService {

    List<TeachPlanDto> findTeachPlanTree(Long courseId);
}
