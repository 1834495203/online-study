package com.study.content.service;

import com.study.content.model.dto.CoursePreviewDto;

import java.io.File;

public interface CoursePublishService {
    /**
     * 获取课程预览信息
     * @param courseId 课程id
     * @return com.study.content.model.dto.CoursePreviewDto
     */
    CoursePreviewDto getCoursePreviewInfo(Long courseId);

    /**
     * 提交审核
     * @param courseId  课程id
     */
    void commitAudit(Long companyId,Long courseId);

    /**
     * 课程发布接口
     * @param companyId 机构id
     * @param courseId 课程id
     */
    void publish(Long companyId,Long courseId);

    /**
     * 课程静态化
     * @param courseId  课程id
     * @return File 静态化文件
     * @author Mr.M
     */
    File generateCourseHtml(Long courseId);

    /**
     * 上传课程静态化页面
     * @param file  静态化文件
     * @author Mr.M
     */
    void  uploadCourseHtml(Long courseId, File file);

}
