package com.study.content.service.impl;

import com.study.content.mapper.TeachplanMapper;
import com.study.content.model.dto.TeachPlanDto;
import com.study.content.service.TeachPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TeachPlanServiceImpl implements TeachPlanService {

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Override
    public List<TeachPlanDto> findTeachPlanTree(Long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }
}
