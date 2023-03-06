package com.study.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.content.model.dto.CourseCategoryTreeDto;
import com.study.content.model.po.CourseCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author GLaDOS
 */
@Mapper
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

    /**
     * 根据id查询课程的子类型
     * @param id 根节点
     * @return 课程子类型
     */
    List<CourseCategoryTreeDto> selectTreeNodes(String id);
}
