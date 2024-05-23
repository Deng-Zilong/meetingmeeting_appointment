package com.jfzt.meeting.modules;

import com.jfzt.meeting.entity.vo.MeetingNoticeVO;
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
        // ��ʼ���������ݣ�����״̬��
    }

    @AfterEach
    public void tearDown () {
        // �����������
    }

    /**
     * ���Բ�ѯ���й���
     */
    @Test
    public void getMeetingNoticeTest () {
        assertNull(meetingNoticeService.selectAll());
    }


    /**
     * ������������
     */
    //@Test
    public void addMeetingNoticeTest () {
        assertEquals("00000", meetingNoticeService.addNotice(new MeetingNoticeVO("ԤԼϵͳ��ɣ�", 0, "dzl")).getCode());
        assertEquals("00000", meetingNoticeService.addNotice(new MeetingNoticeVO("ԤԼϵͳ��ɣ�", 0, null)).getCode());
        assertEquals("00000", meetingNoticeService.addNotice(new MeetingNoticeVO(null, 2, "dzl")).getCode());
    }
}
