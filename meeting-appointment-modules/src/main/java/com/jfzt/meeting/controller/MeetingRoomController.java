package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.vo.MeetingRoomVO;
import com.jfzt.meeting.service.MeetingRoomService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zilong.deng
 * @date 2024/04/28
 */
@RestController
@RequestMapping("/meeting/meetingRoom")
public class MeetingRoomController {

    @Resource
    private MeetingRoomService meetingRoomService;



    @PostMapping("/addMeetingRoom")
    public Result<String> addMeetingRoom (@RequestBody @Valid MeetingRoomVO meetingRoomVO) {

        return meetingRoomService.addMeetingRoom(meetingRoomVO);

    }

    @DeleteMapping("/deleteMeetingRoom")
    public Result<String> deleteMeetingRoom (@RequestBody @Valid MeetingRoomVO meetingRoomVO) {

        Boolean result = meetingRoomService.deleteMeetingRoom(meetingRoomVO);
        if (result) {
            return Result.success("添加成功");
        }
        return Result.fail("添加失败!");
    }

}
