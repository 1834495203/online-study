package com.study.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.study.base.exception.CommonError;
import com.study.base.exception.OSException;
import com.study.base.model.PageParams;
import com.study.base.model.PageResult;
import com.study.base.model.RestResponse;
import com.study.content.mapper.CourseBaseMapper;
import com.study.content.mapper.CourseCategoryMapper;
import com.study.content.mapper.CourseMarketMapper;
import com.study.content.mapper.TeachplanMapper;
import com.study.content.model.dto.AddCourseDto;
import com.study.content.model.dto.AlterCourseDto;
import com.study.content.model.dto.CourseBaseInfoDto;
import com.study.content.model.dto.QueryCourseParamsDto;
import com.study.content.model.po.CourseBase;
import com.study.content.model.po.CourseCategory;
import com.study.content.model.po.CourseMarket;
import com.study.content.model.po.Teachplan;
import com.study.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Autowired
    private CourseMarketMapper courseMarketMapper;

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Autowired
    private CourseMarketServiceImpl courseMarketService;

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {

        //分页查询
        LambdaQueryWrapper<CourseBase> wrapper = new LambdaQueryWrapper<>();

        //拼接查询条件
        //根据课程名称模糊查询
        wrapper.like(
                StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),
                CourseBase::getName,
                queryCourseParamsDto.getCourseName()
        );

        //根据课程审核状态
        wrapper.eq(
                StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()),
                CourseBase::getAuditStatus,
                queryCourseParamsDto.getAuditStatus()
        );

        //TODO 根据课程发布状态
        wrapper.eq(
                StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()),
                CourseBase::getStatus,
                queryCourseParamsDto.getPublishStatus()
        );

        //分页参数
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        //E page 分页参数, @Param("ew") Wrapper<T> queryWrapper 查询条件
        Page<CourseBase> result = courseBaseMapper.selectPage(page, wrapper);

        //数据列表
        List<CourseBase> records = result.getRecords();
        //总记录数
        long total = result.getTotal();

        //准备返回数据 items counts page pageSize
        return new PageResult<>(records, total, pageParams.getPageNo(), pageParams.getPageSize());
    }

    @Override
    @Transactional
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {

        //TODO 对参数的合法性进行校验 使用了JSR303校验

        //对数据进行封装, 调用mapper进行数据持久化
        CourseBase courseBase = new CourseBase();
        //将dto中与courseBase属性名相同的拷贝到courseBase
        BeanUtil.copyProperties(dto, courseBase);

        //设置机构id
        courseBase.setCompanyId(companyId);

        //设置创建时间
        courseBase.setCreateDate(LocalDateTime.now());

        //审核状态默认为未提交
        courseBase.setAuditStatus("202002");

        //发布状态默认为未发布
        courseBase.setStatus("203001");

        //向课程基本表插入一条记录
        int base = courseBaseMapper.insert(courseBase);
        //获取课程id
        Long id = courseBase.getId();

        //将dto中属性名与courseMarket相同的拷贝到courseMarket中
        CourseMarket courseMarket = new CourseMarket();
        BeanUtil.copyProperties(dto, courseMarket);
        courseMarket.setId(id);

        int i = this.saveCourseMarket(courseMarket);
        if (i <= 0 || base <= 0) throw new RuntimeException("提交失败");

        //组装要返回的结果
        return getCourseBaseInfo(courseBase.getId());
    }

    @Override
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId) {
        //基本信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        //营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);

        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();

        BeanUtil.copyProperties(courseBase, courseBaseInfoDto);
        BeanUtil.copyProperties(courseMarket, courseBaseInfoDto);

        //根据课程分类的id查询分类名称
        String mt = courseBase.getMt();
        String st = courseBase.getSt();

        //根据课程分类的编号在课程目录中找
        CourseCategory mtCategory = courseCategoryMapper.selectById(mt);
        CourseCategory stCategory = courseCategoryMapper.selectById(st);

        if (mtCategory != null){
            //获取大分类的名字
            String name = mtCategory.getName();
            courseBaseInfoDto.setMtName(name);
        }
        if (stCategory != null){
            //获取小分类的名字
            String name = stCategory.getName();
            courseBaseInfoDto.setStName(name);
        }

        return courseBaseInfoDto;
    }

    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, AlterCourseDto alterCourseDto) {

        //校验
        Long id = alterCourseDto.getId();
        CourseBase courseBase = courseBaseMapper.selectById(id);
        if (courseBase == null) OSException.cast(CommonError.OBJECT_NULL);

        //封装基本信息的数据
        BeanUtil.copyProperties(alterCourseDto, courseBase);
        courseBase.setChangeDate(LocalDateTime.now());

        //更新课程基本信息
        courseBaseMapper.updateById(courseBase);

        //验证本机构只能修改本机构的课程
        if (!courseBase.getCompanyId().equals(companyId))
            OSException.cast("本机构只能修改本机构的课程");

        //封装营销消息的数据
        CourseMarket courseMarket = new CourseMarket();
        BeanUtil.copyProperties(alterCourseDto, courseMarket);

        //校验如果课程要收费, 则价格必须输入
        String charge = alterCourseDto.getCharge();
        if (charge.equals("201001") && courseMarket.getPrice() <= 0)
            throw new OSException("课程为收费时价格必须输入");

        //对营销表有则更新, 没有则添加
        this.saveCourseMarket(courseMarket);
        return getCourseBaseInfo(id);
    }

    @Override
    public RestResponse<Boolean> deleteCourseById(Long id) {
        //先删除courseBase中的课程信息
        int i = courseBaseMapper.deleteById(id);
        if (i >= 0) {
            //再删除其教学计划
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getCourseId, id);
            List<Teachplan> teachPlans = teachplanMapper.selectList(queryWrapper);
            if (teachPlans != null){
                //当教学计划不为空时删除
                for (Teachplan tp : teachPlans) {
                    //删除其教学计划
                    teachplanMapper.deleteById(tp.getId());
                }
            }
            return RestResponse.success(true);
        }
        return RestResponse.success(false, "没有此id, 删除失败");
    }

    private int saveCourseMarket(CourseMarket courseMarket){
        //校验如果课程要收费, 则价格必须输入
        String charge = courseMarket.getCharge();
        if (StringUtils.isBlank(charge)) OSException.cast("收费规则没有选择");
        if (charge.equals("201001") && courseMarket.getPrice() <= 0)
            throw new OSException("课程为收费时价格必须输入");

        //对营销表有则更新, 没有则添加
        boolean b = courseMarketService.saveOrUpdate(courseMarket);
        return b ? 1 : 0;
    }
}
