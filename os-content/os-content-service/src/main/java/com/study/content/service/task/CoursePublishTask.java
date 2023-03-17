package com.study.content.service.task;

import com.study.base.exception.OSException;
import com.study.content.service.CoursePublishService;
import com.study.messagesdk.model.po.MqMessage;
import com.study.messagesdk.service.MessageProcessAbstract;
import com.study.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 课程发布的任务类
 */
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {

    @Autowired
    private CoursePublishService coursePublishService;

    //任务调度入口
    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler(){

        //分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        //调用父类执行任务
        process(shardIndex, shardTotal, "course_publish", 30, 60);
    }

    //执行发布任务的逻辑
    @Override
    public boolean execute(MqMessage mqMessage) {

        //从mqMessage拿到课程id
        long courseId = Long.parseLong(mqMessage.getBusinessKey1());

        //向elasticsearch写索引
        saveCourseIndex(mqMessage, courseId);

        //TODO 向redis写缓存

        //课程静态化上传minio
        createCourseHtml(mqMessage, courseId);

        //返回true表示任务完成
        return false;
    }

    //生成静态化页面并上传至文件系统
    private void createCourseHtml(MqMessage mqMessage, long courseId){

        //消息id
        Long id = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();

        //做任务幂等性处理
        //查询数据库, 取出该阶段执行状态
        int stageOne = mqMessageService.getStageOne(id);
        if (stageOne > 0) {
            log.info("课程静态化已完成");
            return;
        }

        //生成静态化页面
        File file = coursePublishService.generateCourseHtml(courseId);
        if (file == null)
            OSException.cast("生成的静态化页面为空");
        if (file.delete()) log.debug("删除文件: {}成功", file.getName());

        //将html上传minio
        coursePublishService.uploadCourseHtml(courseId, file);

        mqMessageService.completedStageOne(id);
    }

    //保存课程索引信息
    private void saveCourseIndex(MqMessage mqMessage, long courseId){
        //任务id
        Long id = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        //取出第二个阶段状态
        int stageTwo = mqMessageService.getStageTwo(id);

        if (stageTwo > 0 ) {
            log.info("课程索引已写入");
            return;
        }


        mqMessageService.completedStageTwo(id);
    }
}
