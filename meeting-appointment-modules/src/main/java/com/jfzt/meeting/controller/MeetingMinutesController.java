package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingMinutes;
import com.jfzt.meeting.entity.vo.MeetingMinutesVO;
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

    private MeetingMinutesService meetingMinutesService;
    /**
     * setter注入
     **/
    @Autowired
    public void setMeetingMinutesService (MeetingMinutesService meetingMinutesService) {
        this.meetingMinutesService = meetingMinutesService;
    }

    /**
     * @param meetingMinutes 会议纪要
     * @return 会议纪要VO
     **/
    @GetMapping("/minutes")
    public Result<MeetingMinutesVO> getMeetingMinutes (MeetingMinutes meetingMinutes) {
        List<MeetingMinutesVO> minutesVOList = meetingMinutesService.getMeetingMinutes(meetingMinutes);
        return Result.success(!minutesVOList.isEmpty() ? minutesVOList.getFirst() : null);
    }

    /**
     * 保存或更新会议纪要
     * @param meetingMinutes 会议纪要
     * @return 结果
     */
    @PostMapping("/minutes")
    public Result<Object> saveOrUpdateMinutes (@RequestBody MeetingMinutes meetingMinutes) {
        return meetingMinutesService.saveOrUpdateMinutes(meetingMinutes);
    }

    /**
     * 根据会议纪要id和用户id删除会议纪要
     * @param meetingMinutes 会议纪要
     * @return 删除结果
     */
    @DeleteMapping("/minutes")
    public Result<String> deleteMeetingMinutes (MeetingMinutes meetingMinutes) {
        meetingMinutesService.deleteMeetingMinutes(meetingMinutes);
        return Result.success();
    }

}
