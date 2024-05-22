package com.jfzt.meeting.modules;

import com.jfzt.meeting.service.MeetingRoomService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
public class MeetingRoomTest {
    @Autowired
    private MeetingRoomService meetingRoomService;

    @Test
    public void getMeetingRoomStatusTest () {
        assertDoesNotThrow(() -> meetingRoomService.getMeetingRoomStatus());
        assertNotNull(meetingRoomService.getMeetingRoomStatus());
    }

    @Test
    public void getAvailableMeetingRoomsTest () {
        assertDoesNotThrow(() -> meetingRoomService.getAvailableMeetingRooms(LocalDateTime.now(), LocalDateTime.now().plusHours(1)));
        assertNotNull(meetingRoomService.getAvailableMeetingRooms(LocalDateTime.now(), LocalDateTime.now().plusHours(1)));
    }

}
