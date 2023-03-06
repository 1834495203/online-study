package com.study.content.model.dto;

import com.study.content.model.po.CourseCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 返回给前端的dto
 * 是课程类型的树形结构
 * 包含了课程的一级分类与二级分类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CourseCategoryTreeDto extends CourseCategory {
    List<CourseCategory> childrenTreeNodes;
}
