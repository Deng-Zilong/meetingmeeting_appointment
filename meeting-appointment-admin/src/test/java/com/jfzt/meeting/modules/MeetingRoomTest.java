package com.jfzt.meeting.modules;

import com.jfzt.meeting.service.MeetingRoomService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
public class MeetingRoomTest {
    @Autowired
    private MeetingRoomService meetingRoomService;

    @BeforeEach
    public void setUp () {
        // 初始化测试数据，重置状态等
    }

    @AfterEach
    public void tearDown () {
        // 清理测试数据
    }

    @Test
    public void getMeetingRoomStatusTest () {
        assertDoesNotThrow(() -> meetingRoomService.getMeetingRoomStatus());
        assertNotNull(meetingRoomService.getMeetingRoomStatus());
    }

    @Test
    public void getTodayTimePeriodStatusTest () {
        assertNotNull(meetingRoomService.getTodayTimePeriodStatus());
        assertDoesNotThrow(() -> meetingRoomService.getTodayTimePeriodStatus());
    }

    @Test
    public void getTimePeriodStatusByIdAndDateTest () {
        assertThrows(RRException.class, () -> meetingRoomService.getTimePeriodStatusByIdAndDate(null, LocalDateTime.now().toLocalDate()));
        assertThrows(RRException.class, () -> meetingRoomService.getTimePeriodStatusByIdAndDate(1L, null));
    }

    @Test
    public void getAvailableMeetingRoomsTest () {
        assertDoesNotThrow(() -> meetingRoomService.getAvailableMeetingRooms(LocalDateTime.now(), LocalDateTime.now().plusHours(1)));
        assertNotNull(meetingRoomService.getAvailableMeetingRooms(LocalDateTime.now(), LocalDateTime.now().plusHours(1)));
    }

}
