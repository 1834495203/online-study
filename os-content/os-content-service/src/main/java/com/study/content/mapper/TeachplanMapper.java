package com.study.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.content.model.dto.TeachPlanDto;
import com.study.content.model.po.Teachplan;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author GLaDOS
 */
@Mapper
public interface TeachplanMapper extends BaseMapper<Teachplan> {
    /**
     * 查询某课程的课程计划，组成树型结构
     * @param courseId 课程id
     * @return 课程计划的树形结构
     * @author GLaDOS
     */
    List<TeachPlanDto> selectTreeNodes(long courseId);
}
