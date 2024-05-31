package com.jfzt.meeting.modules;

import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.service.MeetingRecordService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author zilong.deng
 * @date 2024/05/20
 * @description 会议记录测试
 * 等于
 * assertEquals(0, meetingRecordService.getAllRecordVoListPage(null, 1L, 10L));
 * 不等于
 * assertNotEquals(0, meetingRecordService.getAllRecordVoListPage(null, 1, 10).getTotal());
 * 空
 * assertNull(meetingRecordService.getAllRecordVoListPage(null, 0, 10));
 * 非空
 * assertNotNull(meetingRecordService.getAllRecordVoListPage(null, 1, 0));
 * 抛异常
 * assertThrows(RRException.class, () -> meetingRecordService.getAllRecordVoListPage(null, pageNum, pageSize));
 * 不抛异常
 * assertDoesNotThrow(() -> meetingRecordService.getAllRecordVoListPage(null, 1, 10));
 */
@Slf4j
@SpringBootTest
public class MeetingRecordTest {
    @Autowired
    private MeetingRecordService meetingRecordService;

    @BeforeEach
    public void setUp () {
        // 初始化测试数据，重置状态等
        //        MockitoAnnotations.openMocks(this);

    }

    @AfterEach
    public void tearDown () {
        // 清理测试数据
    }

    @Test
    public void recordNumberTest () {
        assertNotNull(meetingRecordService.getRecordNumber());
        assertDoesNotThrow(() -> meetingRecordService.getRecordNumber());
    }

    @Test
    public void getAllRecordVoListPageTest () {
        assertThrows(RRException.class, () -> meetingRecordService.getAllRecordVoListPage(null, 1L, 10L));
        assertThrows(RRException.class, () -> meetingRecordService.getAllRecordVoListPage("admin", null, 10L));
        assertThrows(RRException.class, () -> meetingRecordService.getAllRecordVoListPage("admin", 1L, null));
    }

    @Test
    public void cancelMeetingRecordTest () {
        assertThrows(RRException.class, () -> meetingRecordService.cancelMeetingRecord(null, 3L));
        assertThrows(RRException.class, () -> meetingRecordService.cancelMeetingRecord("admin", null));
        assertThrows(RRException.class, () -> meetingRecordService.cancelMeetingRecord("test", 0L));
    }

    /**
     * 测试查询所有的会议记录信息
     */
    @Test
    public void getAllMeetingRecordVoListPageTest () {
        assertNotNull(meetingRecordService.getAllMeetingRecordVoListPage(1L, 10L, 0));
        assertNotNull(meetingRecordService.getAllMeetingRecordVoListPage(1L, 10L, 1));
        assertThrows(RRException.class, () -> meetingRecordService.getAllMeetingRecordVoListPage(1L, 10L, 2));
        assertThrows(RRException.class, () -> meetingRecordService.getAllMeetingRecordVoListPage(null, 10L, 0));
        assertThrows(RRException.class, () -> meetingRecordService.getAllMeetingRecordVoListPage(1L, null, 0));
    }



}
