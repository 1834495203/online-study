package com.study.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.study.base.model.PageParams;
import com.study.base.model.PageResult;
import com.study.content.mapper.CourseBaseMapper;
import com.study.content.mapper.CourseCategoryMapper;
import com.study.content.mapper.CourseMarketMapper;
import com.study.content.model.dto.AddCourseDto;
import com.study.content.model.dto.CourseBaseInfoDto;
import com.study.content.model.dto.QueryCourseParamsDto;
import com.study.content.model.po.CourseBase;
import com.study.content.model.po.CourseCategory;
import com.study.content.model.po.CourseMarket;
import com.study.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        //TODO 对参数的合法性进行校验
        if (StringUtils.isBlank(dto.getName())) {
            throw new RuntimeException("课程名称为空");
        }

        if (StringUtils.isBlank(dto.getMt())) {
            throw new RuntimeException("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getSt())) {
            throw new RuntimeException("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getGrade())) {
            throw new RuntimeException("课程等级为空");
        }

        if (StringUtils.isBlank(dto.getTeachmode())) {
            throw new RuntimeException("教育模式为空");
        }

        if (StringUtils.isBlank(dto.getUsers())) {
            throw new RuntimeException("适应人群为空");
        }

        if (StringUtils.isBlank(dto.getCharge())) {
            throw new RuntimeException("收费规则为空");
        }

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

        //校验如果课程要收费, 则价格必须输入
        String charge = dto.getCharge();
        if (charge.equals("201001") && courseMarket.getPrice() <= 0)
            throw new RuntimeException("课程为收费时价格必须输入");

        //向营销表中插入一条数据
        int market = courseMarketMapper.insert(courseMarket);

        if (market <= 0 || base <= 0) throw new RuntimeException("提交失败");

        //组装要返回的结果

        return getCourseBaseInfo(courseBase.getId());
    }

    /**
     * 根据id查询课程的基本信息与营销信息
     * @param courseId 课程id
     * @return 全部课程信息
     */
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId){
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
            //大分类
            String name = mtCategory.getName();
            courseBaseInfoDto.setMtName(name);
        }
        if (stCategory != null){
            //小分类
            String name = stCategory.getName();
            courseBaseInfoDto.setStName(name);
        }

        return courseBaseInfoDto;
    }
}
