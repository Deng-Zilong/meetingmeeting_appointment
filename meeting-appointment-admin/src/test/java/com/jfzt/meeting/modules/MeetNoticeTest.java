package com.jfzt.meeting.modules;

import com.jfzt.meeting.entity.vo.MeetingNoticeVO;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.service.MeetingNoticeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
public class MeetNoticeTest {

    @Autowired
    private MeetingNoticeService meetingNoticeService;

    @BeforeEach
    public void setUp () {
        // 初始化测试数据,重置状态等
    }

    @AfterEach
    public void tearDown () {
        // 清理测试数据
    }

    /**
     * 测试查询所有公告
     */
    @Test
    public void getMeetingNoticeTest () {
        assertNotNull(meetingNoticeService.selectAll());
    }


    /**
     * 测试新增公告
     */
    @Test
    public void addMeetingNoticeTest () {
        assertEquals("00000", meetingNoticeService.addNotice(new MeetingNoticeVO("哈哈哈哈1", 0, "dzl")).getCode());
        assertThrows(RRException.class, () -> meetingNoticeService.addNotice(new MeetingNoticeVO("哈哈哈哈2", 0, "")));
        assertThrows(RRException.class, () -> meetingNoticeService.addNotice(new MeetingNoticeVO("", 0, "dzl")));
        assertThrows(RRException.class, () -> meetingNoticeService.addNotice(new MeetingNoticeVO("哈哈哈哈1", 2, "dzl")));
    }
}
