package com.study.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.content.model.po.CourseBase;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 课程基本信息 Mapper 接口
 * </p>
 *
 * @author GLaDOS
 */
@Mapper
public interface CourseBaseMapper extends BaseMapper<CourseBase> {

}
