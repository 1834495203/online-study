package com.study.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.study.base.exception.OSException;
import com.study.content.mapper.TeachplanMapper;
import com.study.content.mapper.TeachplanMediaMapper;
import com.study.content.model.dto.BindTeachPlanMediaDto;
import com.study.content.model.dto.SaveTeachPlanDto;
import com.study.content.model.dto.TeachPlanDto;
import com.study.content.model.po.Teachplan;
import com.study.content.model.po.TeachplanMedia;
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

    @Autowired
    private TeachplanMediaMapper teachplanMediaMapper;

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

    @Override
    public void associationMedia(BindTeachPlanMediaDto bindTeachPlanMediaDto) {

        Long teachPlanId = bindTeachPlanMediaDto.getTeachPlanId();
        Teachplan teachplan = teachplanMapper.selectById(teachPlanId);
        if (teachplan == null)
            OSException.cast("课程不存在");

        //先删除原有记录, 根据课程计划的id
        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachplanMedia::getTeachplanId, bindTeachPlanMediaDto.getTeachPlanId());
        int delete = teachplanMediaMapper.delete(queryWrapper);

        //再添加数据
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        BeanUtil.copyProperties(bindTeachPlanMediaDto, teachplanMedia);
        teachplanMedia.setCourseId(teachplan.getCourseId());
        teachplanMedia.setMediaFilename(bindTeachPlanMediaDto.getFileName());
        teachplanMediaMapper.insert(teachplanMedia);
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
