package com.study.content.service;

import com.study.base.model.PageParams;
import com.study.base.model.PageResult;
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
     * @return com.xuecheng.base.model.PageResult<com.xuecheng.content.model.po.CourseBase>
     * @author GLaDOS
     */
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);
}
