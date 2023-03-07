package com.study.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.study.content.mapper.TeachplanMapper;
import com.study.content.model.dto.SaveTeachPlanDto;
import com.study.content.model.dto.TeachPlanDto;
import com.study.content.model.po.Teachplan;
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

    @Override
    public void saveTeachPlan(SaveTeachPlanDto saveTeachPlanDto) {

        Long id = saveTeachPlanDto.getId();
        Teachplan teachplan;

        if ((teachplan = teachplanMapper.selectById(id)) == null){
            //则应该新增课程计划
            teachplan = new Teachplan();
            BeanUtil.copyProperties(saveTeachPlanDto, teachplan);
            //计算orderBy
            int count = getTeachPlanOrderBy(saveTeachPlanDto.getCourseId(), saveTeachPlanDto.getParentid());
            teachplan.setOrderby(count+1);

            teachplanMapper.insert(teachplan);
        }else {
            //则因该修改课程计划
            BeanUtil.copyProperties(saveTeachPlanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        }
    }

    /**
     * 计算新增课程计划的orderBy, 找到同级课程的数量
     * @param courseId 课程id
     * @param parentId 课程的上一级课程
     * @return 同一级课程的数量
     */
    private int getTeachPlanOrderBy(Long courseId, Long parentId){
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getCourseId, courseId);
        wrapper.eq(Teachplan::getParentid, parentId);
        return teachplanMapper.selectCount(wrapper);
    }
}
