package com.study.content.service;

import com.study.base.model.PageParams;
import com.study.base.model.PageResult;
import com.study.base.model.RestResponse;
import com.study.content.model.dto.AddCourseDto;
import com.study.content.model.dto.AlterCourseDto;
import com.study.content.model.dto.CourseBaseInfoDto;
import com.study.content.model.dto.QueryCourseParamsDto;
import com.study.content.model.po.CourseBase;

/**
 * 课程管理service
 */
public interface CourseBaseInfoService {

    /**
     * 课程查询接口
     * @param pageParams 分页参数
     * @param queryCourseParamsDto 条件条件
     * @return com.study.base.model.PageResult<com.study.content.model.po.CourseBase>
     * @author GLaDOS
     */
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    /**
     * 新增课程信息
     * @param addCourseDto 课程信息
     * @param companyId 企业机构id
     * @return 返回值包括基本信息, 营销信息
     */
    CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    /**
     * 根据id查询课程的基本信息与营销信息
     * @param courseId 课程id
     * @return 全部课程信息
     */
    CourseBaseInfoDto getCourseBaseInfo(Long courseId);

    /**
     * 修改课程信息
     * @param companyId 机构id 本机构只能修改本机构的课程
     * @param alterCourseDto 课程信息
     * @return 全部课程信息
     */
    CourseBaseInfoDto updateCourseBase(Long companyId, AlterCourseDto alterCourseDto);

    /**
     * 根据课程id删除课程
     * @param id 课程id
     * @return msgModel
     */
    RestResponse<Boolean> deleteCourseById(Long id);
}
