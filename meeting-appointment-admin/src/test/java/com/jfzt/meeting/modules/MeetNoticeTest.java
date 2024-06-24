package com.jfzt.meeting.modules;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.constant.IsDeletedConstant;
import com.jfzt.meeting.constant.MessageConstant;
import com.jfzt.meeting.entity.MeetingNotice;
import com.jfzt.meeting.entity.vo.MeetingNoticeVO;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.mapper.MeetingNoticeMapper;
import com.jfzt.meeting.service.impl.MeetingNoticeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class MeetNoticeTest {

    @InjectMocks
    private MeetingNoticeServiceImpl meetingNoticeService;

    @Mock
    private MeetingNoticeMapper meetingNoticeMapper;

    private MeetingNoticeVO meetingNoticeVO;

    @BeforeEach
    void setUp() {
        // 初始化Mockito框架，用于创建模拟对象。
        MockitoAnnotations.openMocks(this);
        meetingNoticeVO = new MeetingNoticeVO();
        meetingNoticeVO.setCurrentLevel(MessageConstant.SUPER_ADMIN_LEVEL);
        meetingNoticeVO.setCurrentUserId("testUserId");
        meetingNoticeVO.setSubstance("Test Substance");
    }

    @Test
    void addNoticeTest() {
        when(meetingNoticeMapper.insert(any(MeetingNotice.class))).thenReturn(1);
        Result<Integer> result = meetingNoticeService.addNotice(meetingNoticeVO);
        verify(meetingNoticeMapper, times(1)).insert(any(MeetingNotice.class));
        assertNotNull(result);
        assertEquals(1, result.getData());
    }

    @Test
    void addNoticeNotAdminTest() {
        meetingNoticeVO.setCurrentLevel(2);
        assertThrows(RRException.class, () -> meetingNoticeService.addNotice(meetingNoticeVO));
        verify(meetingNoticeMapper, never()).insert(any(MeetingNotice.class));
    }

    @Test
    void addNoticeDataIsInvalidTest() {
        meetingNoticeVO.setCurrentUserId("");
        meetingNoticeVO.setSubstance("");
        assertThrows(RRException.class, () -> meetingNoticeService.addNotice(meetingNoticeVO));
        verify(meetingNoticeMapper, never()).insert(any(MeetingNotice.class));
    }

    @Test
    void selectAllNoticesTest() {
        List<MeetingNotice> notices = new ArrayList<>();
        notices.add(new MeetingNotice(1L, "substance1", "admin", LocalDateTime.now(), LocalDateTime.now(), IsDeletedConstant.NOT_DELETED));
        notices.add(new MeetingNotice(2L, "substance2", "admin", LocalDateTime.now(), LocalDateTime.now(), IsDeletedConstant.IS_DELETED));
        when(meetingNoticeMapper.selectList(any())).thenReturn(notices);
        List<String> result = meetingNoticeService.selectAll();
        assertEquals(1, result.size());
        assertTrue(result.contains("substance1"));
        assertFalse(result.contains("substance2"));
    }
}