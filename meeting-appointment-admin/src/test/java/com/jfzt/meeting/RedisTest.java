package com.jfzt.meeting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfzt.meeting.entity.vo.MeetingRecordVO;
import com.jfzt.meeting.service.MeetingRecordService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;

@Slf4j
@SpringBootTest
public class RedisTest {
    @Resource
    RedisTemplate<String, String> redisTemplate;
    @Autowired
    MeetingRecordService meetingRecordService;


    @Test
    void test () {
        redisTemplate.opsForValue().set("name", "dzl");
        String string = redisTemplate.opsForValue().get("name");
        System.out.println(string);
    }

    @Test
    void contextLoads () throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        List<MeetingRecordVO> recordVoList = meetingRecordService.getRecordVoList("dzl");
        log.info("recordVoList:{}", recordVoList);
        log.info("----------------------------------------");
        //转JSON
        String value = objectMapper.writeValueAsString(recordVoList);
        log.info("----------------------------------------");
        log.info("value:{}", value);
        //写缓存
        redisTemplate.opsForValue().set("recordVoList", value);
        //读缓存
        String s = redisTemplate.opsForValue().get("recordVoList");
        log.info("----------------------------------------");
        log.info("s:{}", s);
        //转对象
        List<MeetingRecordVO> recordVoList1 = objectMapper.readValue(s, new TypeReference<>() {
        });
        log.info("----------------------------------------");
        log.info("recordVoList1:{}", recordVoList1);

    }
}
