package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingMinutes;
import com.jfzt.meeting.entity.MeetingMinutesVO;
import com.jfzt.meeting.service.MeetingMinutesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zilong.deng
 * @date 2024/05/30
 */
@RestController
@RequestMapping("/meeting")
public class MeetingMinutesController {
    @Autowired
    MeetingMinutesService meetingMinutesService;

    @GetMapping("/minutes")
    public Result<List<MeetingMinutesVO>> getMeetingMinutes (MeetingMinutes meetingMinutes) {
        return Result.success(meetingMinutesService.getMeetingMinutes(meetingMinutes));
    }

    /**
     * @return com.jfzt.meeting.common.Result<java.lang.Object>
     * @Description 保存会议纪要
     * @Param [meetingMinutes]
     */
    @PostMapping("/minutes")
    public Result<Object> saveOrUpdateMinutes (@RequestBody MeetingMinutes meetingMinutes) {
        return meetingMinutesService.saveOrUpdateMinutes(meetingMinutes);
    }

    /**
     * 根据会议纪要id和用户id删除会议纪要
     *
     * @param meetingMinutes 会议纪要
     * @return {@code Result<String>}
     */
    @DeleteMapping("/minutes")
    public Result<String> deleteMeetingMinutes (MeetingMinutes meetingMinutes) {
        meetingMinutesService.deleteMeetingMinutes(meetingMinutes);
        return Result.success();
    }

}
