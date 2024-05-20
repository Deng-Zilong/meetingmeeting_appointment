//package com.jfzt.meeting;//package com.jfzt.meeting;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.StringRedisTemplate;
//
//@SpringBootTest
//class MeetingAppointmentCommonApplicationTests {
//
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//    @Test
//    void contextLoads () {
//        String codes = "1"+"/"+"2";
//        stringRedisTemplate.opsForValue().set("name1",codes);
//        String name = stringRedisTemplate.opsForValue().get("name1");
//        System.out.println(name);
//    }
//
//
//}
