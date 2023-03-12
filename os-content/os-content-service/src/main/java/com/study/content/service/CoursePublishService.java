package com.study.content.service;

import com.study.content.model.dto.CoursePreviewDto;

public interface CoursePublishService {
    /**
     * 获取课程预览信息
     * @param courseId 课程id
     * @return com.study.content.model.dto.CoursePreviewDto
     */
    public CoursePreviewDto getCoursePreviewInfo(Long courseId);

    /**
     * 提交审核
     * @param courseId  课程id
     */
    public void commitAudit(Long companyId,Long courseId);

    /**
     * 课程发布接口
     * @param companyId 机构id
     * @param courseId 课程id
     */
    public void publish(Long companyId,Long courseId);

}
