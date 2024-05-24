package com.jfzt.meeting.modules;

import com.jfzt.meeting.entity.dto.MeetingRoomDTO;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.service.MeetingRoomService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

    /**
     * 测试修改会议室状态
     */
    @Test
    public void updateStatusTest () {
        assertEquals("00000", meetingRoomService.updateStatus(new MeetingRoomDTO(1L,0,0)).getCode());
        assertEquals("00000", meetingRoomService.updateStatus(new MeetingRoomDTO(1L,0,1)).getCode());
        assertThrows(RRException.class, () -> meetingRoomService.updateStatus(new MeetingRoomDTO(null,0,0)));
        assertThrows(RRException.class, () -> meetingRoomService.updateStatus(new MeetingRoomDTO(1L,null,0)));
        assertThrows(RRException.class, () -> meetingRoomService.updateStatus(new MeetingRoomDTO(1L,0,2)));
    }

    /**
     * 测试查询被禁用的会议室id
     */
    @Test
    public void selectUsableRoomTest () {
        assertNotNull(meetingRoomService.selectUsableRoom(0));
        assertNotNull(meetingRoomService.selectUsableRoom(1));
        assertThrows(RRException.class, () -> meetingRoomService.selectUsableRoom(2));
    }

}
