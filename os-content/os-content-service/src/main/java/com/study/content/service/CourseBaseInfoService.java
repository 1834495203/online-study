package com.study.content.service;

import com.study.base.model.PageParams;
import com.study.base.model.PageResult;
import com.study.content.model.dto.AddCourseDto;
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
}
