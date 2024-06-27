package com.jfzt.meeting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author zilong.deng
 * @date 2024/04/25
 */
@SpringBootApplication
@EnableAsync
public class MeetingAppointmentApplication {

    public static void main (String[] args) {
        SpringApplication.run(MeetingAppointmentApplication.class, args);
    }

}
