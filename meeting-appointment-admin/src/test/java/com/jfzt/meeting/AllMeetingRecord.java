package com.jfzt.meeting;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.vo.MeetingRecordVO;
import com.jfzt.meeting.service.MeetingRecordService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
public class AllMeetingRecord {


    @Autowired
    private MeetingRecordService meetingRecordService;


    @Test
    void test () {
        List<MeetingRecordVO> recordVoList = meetingRecordService.getAllRecordVoListPage("admin",1l,10l);
        log.info("List<MeetingRecordVO>"+ String.valueOf(recordVoList));
    }
    @Test
    void test1 () {
        Result<String> record = meetingRecordService.cancelMeetingRecord("admin", 1l);
        log.info(" Result<String> record"+ String.valueOf(record));
    }

}
