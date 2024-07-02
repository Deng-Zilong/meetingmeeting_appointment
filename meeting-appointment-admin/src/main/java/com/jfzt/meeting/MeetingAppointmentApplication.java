package com.jfzt.meeting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 中心会议室预约
 * @author
 * 负责人：翟臣宇
 * 技术：卢振新
 * 后端：邓子龙、杨旭昌
 * 前端：寇梦梦、周颖
 * 测试：于淼
 * @since 2024-04-20 10:29:23
 */
@SpringBootApplication
@EnableTransactionManagement
public class MeetingAppointmentApplication {

    public static void main (String[] args) {
        SpringApplication.run(MeetingAppointmentApplication.class, args);
    }

}
