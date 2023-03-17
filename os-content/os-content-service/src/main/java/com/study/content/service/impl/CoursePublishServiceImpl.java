package com.study.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.study.base.exception.CommonError;
import com.study.base.exception.OSException;
import com.study.content.clients.MediaServiceClient;
import com.study.content.config.MultipartSupportConfig;
import com.study.content.mapper.CourseBaseMapper;
import com.study.content.mapper.CourseMarketMapper;
import com.study.content.mapper.CoursePublishMapper;
import com.study.content.mapper.CoursePublishPreMapper;
import com.study.content.model.dto.CourseBaseInfoDto;
import com.study.content.model.dto.CoursePreviewDto;
import com.study.content.model.dto.TeachPlanDto;
import com.study.content.model.po.CourseBase;
import com.study.content.model.po.CourseMarket;
import com.study.content.model.po.CoursePublish;
import com.study.content.model.po.CoursePublishPre;
import com.study.content.service.CourseBaseInfoService;
import com.study.content.service.CoursePublishService;
import com.study.content.service.TeachPlanService;
import com.study.messagesdk.model.po.MqMessage;
import com.study.messagesdk.service.MqMessageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class CoursePublishServiceImpl implements CoursePublishService {

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @Autowired
    private TeachPlanService teachplanService;

    @Autowired
    private CourseMarketMapper courseMarketMapper;

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Autowired
    private CoursePublishPreMapper coursePublishPreMapper;

    @Autowired
    private CoursePublishMapper coursePublishMapper;

    @Autowired
    private MqMessageService mqMessageService;

    @Autowired
    private MediaServiceClient mediaServiceClient;

    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {

        //课程基本信息、营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);

        //课程计划信息
        List<TeachPlanDto> teachplanTree= teachplanService.findTeachPlanTree(courseId);

        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(teachplanTree);
        return coursePreviewDto;
    }

    @Override
    public void commitAudit(Long companyId, Long courseId) {
        //约束校验
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        //课程审核状态
        String auditStatus = courseBase.getAuditStatus();
        //当前审核状态为已提交不允许再次提交
        if("202003".equals(auditStatus)){
            OSException.cast("当前为等待审核状态，审核完成可以再次提交。");
        }
        //本机构只允许提交本机构的课程
        if(!courseBase.getCompanyId().equals(companyId)){
            OSException.cast("不允许提交其它机构的课程。");
        }

        //课程图片是否填写
        if(StringUtils.isEmpty(courseBase.getPic())){
            OSException.cast("提交失败，请上传课程图片");
        }

        //添加课程预发布记录
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        //课程基本信息加部分营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        BeanUtils.copyProperties(courseBaseInfo,coursePublishPre);
        //课程营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        //转为json
        String courseMarketJson = JSON.toJSONString(courseMarket);
        //将课程营销信息json数据放入课程预发布表
        coursePublishPre.setMarket(courseMarketJson);

        //查询课程计划信息
        List<TeachPlanDto> teachplanTree = teachplanService.findTeachPlanTree(courseId);
        if(teachplanTree.size() <= 0){
            OSException.cast("提交失败，还没有添加课程计划");
        }
        //转json
        String teachplanTreeString = JSON.toJSONString(teachplanTree);
        coursePublishPre.setTeachplan(teachplanTreeString);

        //设置预发布记录状态,已提交
        coursePublishPre.setStatus("202003");
        //教学机构id
        coursePublishPre.setCompanyId(companyId);
        //提交时间
        coursePublishPre.setCreateDate(LocalDateTime.now());
        CoursePublishPre coursePublishPreUpdate = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPreUpdate == null){
            //添加课程预发布记录
            coursePublishPreMapper.insert(coursePublishPre);
        }else{
            coursePublishPreMapper.updateById(coursePublishPre);
        }

        //更新课程基本表的审核状态
        courseBase.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBase);
    }

    @Transactional
    @Override
    public void publish(Long companyId, Long courseId) {
        //约束校验
        //查询课程预发布表
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPre == null){
            OSException.cast("请先提交课程审核，审核通过才可以发布");
        }
        //本机构只允许提交本机构的课程
        if(!coursePublishPre.getCompanyId().equals(companyId)){
            OSException.cast("不允许提交其它机构的课程。");
        }

        //课程审核状态
        String auditStatus = coursePublishPre.getStatus();
        //审核通过方可发布
        if(!"202004".equals(auditStatus)){
            OSException.cast("操作失败，课程审核通过方可发布。");
        }

        //保存课程发布信息
        saveCoursePublish(courseId);

        //保存消息表
        saveCoursePublishMessage(courseId);

        //删除课程预发布表对应记录
        coursePublishPreMapper.deleteById(courseId);
    }

    @Override
    public File generateCourseHtml(Long courseId) {
        File file = null;
        try {
            Configuration configuration = new Configuration(Configuration.getVersion());

            //拿到classpath路径
            String path = Objects.requireNonNull(this.getClass().getResource("/")).getPath();

            //指定模板目录
            configuration.setDirectoryForTemplateLoading(new File(path+"/templates/"));

            //指定编码
            configuration.setDefaultEncoding("UTF-8");

            //得到模板
            Template template = configuration.getTemplate("test.ftl");

            HashMap<String, Object> map = new HashMap<>();

            map.put("names", "裴橘");

            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);

            //输入流
            InputStream inputStream = IOUtils.toInputStream(html, "utf-8");
            //输出文件
            file = File.createTempFile("coursePublish", ".html");
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            IOUtils.copy(inputStream, fileOutputStream);
        }catch (Exception e){
            log.error("页面静态化出错, 错误为: {}", e.getMessage());
            e.printStackTrace();
        }
        return file;
    }

    @Override
    public void uploadCourseHtml(Long courseId, File file) {
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        String s = mediaServiceClient.uploadImage(multipartFile, "course/"+courseId+".html");
        if (s == null) {
            log.error("远程调用走降级逻辑, 返回值为null, 课程id为: {}", courseId);
            OSException.cast("上传静态文件异常");
        }
    }

    /**
     * 保存课程发布信息
     * @param courseId  课程id
     */
    private void saveCoursePublish(Long courseId){
        //整合课程发布信息
        //查询课程预发布表
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPre == null){
            OSException.cast("课程预发布数据为空");
        }

        CoursePublish coursePublish = new CoursePublish();

        //拷贝到课程发布对象
        BeanUtils.copyProperties(coursePublishPre,coursePublish);
        coursePublish.setStatus("203002");
        CoursePublish coursePublishUpdate = coursePublishMapper.selectById(courseId);
        if(coursePublishUpdate == null){
            coursePublishMapper.insert(coursePublish);
        }else{
            coursePublishMapper.updateById(coursePublish);
        }
        //更新课程基本表的发布状态
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setStatus("203002");
        courseBaseMapper.updateById(courseBase);

    }

    /**
     * 保存消息表记录
     * @param courseId  课程id
     */
    private void saveCoursePublishMessage(Long courseId){
        MqMessage course_publish = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if (course_publish == null)
            OSException.cast(CommonError.UNKNOWN_ERROR);
    }
}
