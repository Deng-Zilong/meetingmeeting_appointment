package com.jfzt.meeting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author zilong.deng
 * @date 2024/05/29
 * @description 线程池任务调度器
 */
@Configuration
public class SchedulerConfig {
    @Bean
    public ThreadPoolTaskScheduler taskScheduler () {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        taskScheduler.setThreadNamePrefix("TaskScheduler-");
        taskScheduler.initialize();
        return taskScheduler;
    }
}
