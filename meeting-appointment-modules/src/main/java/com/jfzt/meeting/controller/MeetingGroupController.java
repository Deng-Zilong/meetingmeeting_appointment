package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingGroup;
import com.jfzt.meeting.entity.dto.MeetingGroupDTO;
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
@CrossOrigin
@RestController
@RequestMapping("/meeting/meetingGroup")
public class MeetingGroupController {

    @Resource
    private MeetingGroupService meetingGroupService;

    /**
     * @Description 群组查询
     * @Param [userId]
     * @return com.jfzt.meeting.common.Result<java.util.List<com.jfzt.meeting.entity.vo.MeetingGroupVO>>
     * @exception
     */
    @GetMapping("/getMeetingGroupList")
    public Result<List<MeetingGroupVO>> getMeetingGroupList(@RequestParam String userId) {
        return meetingGroupService.checkGroup(userId);
    }

    /**
     * @Description 群组添加
     * @Param [meetingGroupDTO]
     * @return com.jfzt.meeting.common.Result<java.lang.Object>
     * @exception
     */
    @PostMapping("/addMeetingGroup")
    public Result<Object> addMeetingGroup(@RequestBody MeetingGroupDTO meetingGroupDTO) {
        return meetingGroupService.addMeetingGroup(meetingGroupDTO);
    }

    /**
     * @Description 群组修改
     * @Param [meetingGroupDTO]
     * @return com.jfzt.meeting.common.Result<java.lang.Object>
     * @exception
     */

    @PutMapping("/updateMeetingGroup")
    public Result<Object> updateMeetingGroup(@RequestBody MeetingGroupDTO meetingGroupDTO) {
        return meetingGroupService.updateMeetingGroup(meetingGroupDTO);
    }
    /**
     * @Description 群组删除
     * @Param [meetingGroupDTO]
     * @return com.jfzt.meeting.common.Result<java.lang.Object>
     * @exception
     */
    @DeleteMapping("/deleteMeetingGroup/{id}")
    public Result<Object> deleteMeetingGroup(@PathVariable("id") Long id) {
        return meetingGroupService.deleteMeetingGroup(id);
    }
}