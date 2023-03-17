package com.study.content.api;

import com.study.base.exception.ValidationGroups;
import com.study.base.model.PageParams;
import com.study.base.model.PageResult;
import com.study.base.model.RestResponse;
import com.study.content.model.dto.AddCourseDto;
import com.study.content.model.dto.AlterCourseDto;
import com.study.content.model.dto.CourseBaseInfoDto;
import com.study.content.model.dto.QueryCourseParamsDto;
import com.study.content.model.po.CourseBase;
import com.study.content.service.CourseBaseInfoService;
import com.study.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    //@Validated 注解开启了JSR303校验, 如不符合则抛出MethodArgumentNotValidException异常
    public CourseBaseInfoDto createCourseBase(@RequestBody
                                                  @Validated(value = ValidationGroups.Insert.class)
                                                          AddCourseDto addCourseDto){
        //TODO 获取当前用户的培训机构的id
        Long companyId = 22L;

        //调用service
        return courseBaseInfoService.createCourseBase(companyId, addCourseDto);
    }

    @ApiOperation("通过id获取课程信息")
    @RequestMapping(value = "/course/{courseId}", method = RequestMethod.GET)
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId){
        //获取当前用户身份 测试
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        System.out.println(principal);
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        System.out.println(user);
        return courseBaseInfoService.getCourseBaseInfo(courseId);
    }

    @ApiOperation("修改课程信息")
    @RequestMapping(value = "/course", method = RequestMethod.PUT)
    public CourseBaseInfoDto updateCourseBase(@RequestBody AlterCourseDto alterCourseDto){

        //TODO 获取当前用户的培训机构的id
        Long companyId = 22L;

        return courseBaseInfoService.updateCourseBase(companyId, alterCourseDto);
    }

    @ApiOperation("删除课程信息")
    @RequestMapping(value = "/course/{id}", method = RequestMethod.DELETE)
    public RestResponse<Boolean> deleteCourseById(@PathVariable Long id){
        return courseBaseInfoService.deleteCourseById(id);
    }
}
