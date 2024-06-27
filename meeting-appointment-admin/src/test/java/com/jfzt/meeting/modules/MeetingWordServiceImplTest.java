package com.jfzt.meeting.modules;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingRecord;
import com.jfzt.meeting.entity.MeetingWord;
import com.jfzt.meeting.mapper.MeetingWordMapper;
import com.jfzt.meeting.service.MeetingRecordService;
import com.jfzt.meeting.service.impl.MeetingWordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringBootTest
public class MeetingWordServiceImplTest {

    @InjectMocks
    private MeetingWordServiceImpl meetingWordService;

    @MockBean
    private MeetingWordMapper meetingWordMapper;

    @MockBean
    private MeetingRecordService meetingRecordService;

    private MeetingWord meetingWord;
    private MeetingRecord meetingRecord;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        meetingWord = new MeetingWord();
        meetingWord.setMeetingRecordId(1L);
        meetingWord.setContent("测试内容");

        meetingRecord = new MeetingRecord();
        meetingRecord.setId(1L);
        meetingRecord.setTitle("测试会议");
    }

    /**
     * 测试保存或更新会议记录成功的场景
     */
    @Test
    public void testSaveOrUpdateWordSuccess() {
        // Arrange
        when(meetingRecordService.getById(meetingWord.getMeetingRecordId())).thenReturn(meetingRecord);

        // Act
        Result<Object> result = meetingWordService.saveOrUpdateWord(meetingWord);

        // Assert
        assertNotNull(result);
        assertEquals("00000", result.getCode());
        assertEquals("保存成功", result.getMsg());
    }

    /**
     * 测试保存或更新会议记录失败的场景
     */
    @Test
    public void testSaveOrUpdateWordFail() {
        // Arrange
        when(meetingRecordService.getById(meetingWord.getMeetingRecordId())).thenReturn(null);

        // Act
        Result<Object> result = meetingWordService.saveOrUpdateWord(meetingWord);

        // Assert
        assertNotNull(result);
        // Assuming fail method returns a Result with "fail" as the code and a specific message.
        assertEquals("B0001", result.getCode());
        assertEquals("会议不存在", result.getMsg());
    }

}

