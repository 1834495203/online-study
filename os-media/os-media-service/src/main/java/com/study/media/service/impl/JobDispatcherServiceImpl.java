package com.study.media.service.impl;

import com.study.media.service.JobDispatcherService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Service;

@Service
public class JobDispatcherServiceImpl implements JobDispatcherService {

    @XxlJob("jobTest")
    @Override
    public void testJob() {
        System.out.println("================测试调度器================");
    }
}
