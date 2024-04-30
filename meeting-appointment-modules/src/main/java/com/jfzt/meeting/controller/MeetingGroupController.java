package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingGroup;
import com.jfzt.meeting.entity.vo.MeetingGroupVO;
import com.jfzt.meeting.service.MeetingGroupService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会议群组控制类
 * @Author: chenyu.di
 * @since: 2024-04-29 16:44
 */
@RestController
@RequestMapping("/meetingGroup")
public class MeetingGroupController {

    @Resource
    private MeetingGroupService meetingGroupService;

    @GetMapping("/getMeetingGroupList")
    public Result<List<MeetingGroupVO>> getMeetingGroupList(String userId){
        return meetingGroupService.checkGroup(userId);

    }

    @PostMapping
    public Result<Object> addMeetingGroup(@RequestBody MeetingGroup meetingGroup) {
        return null;
    }
}