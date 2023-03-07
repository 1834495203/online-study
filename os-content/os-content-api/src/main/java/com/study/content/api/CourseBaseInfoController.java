package com.study.content.api;

import com.study.base.model.PageParams;
import com.study.base.model.PageResult;
import com.study.content.model.dto.AddCourseDto;
import com.study.content.model.dto.CourseBaseInfoDto;
import com.study.content.model.dto.QueryCourseParamsDto;
import com.study.content.model.po.CourseBase;
import com.study.content.service.CourseBaseInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "课程管理相关接口", tags = "课程管理相关接口")
public class CourseBaseInfoController {

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @ApiOperation("课程查询接口")
    @RequestMapping(value = "/course/list", method = RequestMethod.POST)
    public PageResult<CourseBase> list(PageParams params, @RequestBody QueryCourseParamsDto queryCourseParamsDto){
        return courseBaseInfoService.queryCourseBaseList(params, queryCourseParamsDto);
    }

    @ApiOperation("新增课程")
    @RequestMapping(value = "/course", method = RequestMethod.POST)
    public CourseBaseInfoDto createCourseBase(@RequestBody AddCourseDto addCourseDto){

        //TODO 获取当前用户的培训机构的id
        Long companyId = 22L;

        //调用service
        return courseBaseInfoService.createCourseBase(companyId, addCourseDto);
    }
}
