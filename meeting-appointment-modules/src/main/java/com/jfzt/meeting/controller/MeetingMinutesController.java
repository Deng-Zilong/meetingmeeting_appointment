package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingMinutes;
import com.jfzt.meeting.entity.dto.MeetingMinutesDTO;
import com.jfzt.meeting.entity.vo.MeetingMinutesVO;
import com.jfzt.meeting.service.MeetingMinutesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会议纪要controller
 * @author zilong.deng
 * @since 2024-06-05 11:03:29
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
     * @param meetingMinutesDTO 迭代内容入参请求体
     * @return 保存结果
     */
    @PostMapping("/minutes")
    public Result<Object> saveOrUpdateMinutes (@RequestBody MeetingMinutesDTO meetingMinutesDTO) {
        return meetingMinutesService.saveOrUpdateMinutes(meetingMinutesDTO);
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
