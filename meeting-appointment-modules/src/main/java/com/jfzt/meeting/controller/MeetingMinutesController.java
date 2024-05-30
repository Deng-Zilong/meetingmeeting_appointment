package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingMinutes;
import com.jfzt.meeting.entity.MeetingMinutesVO;
import com.jfzt.meeting.service.MeetingMinutesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zilong.deng
 * @date 2024/05/30
 */
@RestController("/meeting")
public class MeetingMinutesController {
    @Autowired
    MeetingMinutesService meetingMinutesService;

    @GetMapping("minutes")
    public Result<List<MeetingMinutesVO>> getMeetingMinutes (MeetingMinutes meetingMinutes) {
        return Result.success(meetingMinutesService.getMeetingMinutes(meetingMinutes));
    }

}
