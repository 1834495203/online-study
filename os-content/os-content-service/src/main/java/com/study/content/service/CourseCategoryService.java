package com.study.content.service;

import com.study.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * 课程分类相关的service
 */
public interface CourseCategoryService {

    /**
     * 课程分类的查询
     * @param id 根节点id
     * @return 根节点下所有的子节点
     */
    List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
