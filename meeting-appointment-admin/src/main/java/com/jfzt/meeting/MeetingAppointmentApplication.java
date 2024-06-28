package com.jfzt.meeting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author zilong.deng
 * @since 2024-06-28 10:29:23
 */
@SpringBootApplication
@EnableTransactionManagement
public class MeetingAppointmentApplication {

    public static void main (String[] args) {
        SpringApplication.run(MeetingAppointmentApplication.class, args);
    }

}
