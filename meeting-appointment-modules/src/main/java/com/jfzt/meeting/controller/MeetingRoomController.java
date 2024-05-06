package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.vo.MeetingRoomStatusVO;
import com.jfzt.meeting.entity.vo.MeetingRoomVO;
import com.jfzt.meeting.service.MeetingRoomService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zilong.deng
 * @date 2024/04/28
 */
@Slf4j
@RestController
@RequestMapping("/meeting/meetingRoom")
public class MeetingRoomController {

    @Resource
    private MeetingRoomService meetingRoomService;


    /**
     * 查询会议室状态
     *
     * @return {@code Result<List<MeetingRoomStatusVO>>}
     */
    @GetMapping("/index/meetingRoomStatus")
    public Result<List<MeetingRoomStatusVO>> getMeetingRoomStatus () {
        List<MeetingRoomStatusVO> meetingRoomStatusVOList = meetingRoomService.getMeetingRoomStatus();
        log.info("会议室状态： {}", meetingRoomStatusVOList);
        return Result.success(meetingRoomStatusVOList);
    }

    @PostMapping("/addMeetingRoom")
    public Result<String> addMeetingRoom(@RequestBody @Valid MeetingRoomVO meetingRoomVO) {

        return meetingRoomService.addMeetingRoom(meetingRoomVO);

    }

    @DeleteMapping("/deleteMeetingRoom")
    public Result<String> deleteMeetingRoom(@RequestBody @Valid MeetingRoomVO meetingRoomVO) {

        Boolean result = meetingRoomService.deleteMeetingRoom(meetingRoomVO);
        if (result) {
            return Result.success("添加成功");
        }
        return Result.fail("添加失败!");
    }

}
