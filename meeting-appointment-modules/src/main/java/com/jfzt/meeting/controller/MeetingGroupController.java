package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.dto.MeetingGroupDTO;
import com.jfzt.meeting.entity.dto.MeetingGroupPageDTO;
import com.jfzt.meeting.entity.vo.MeetingGroupVO;
import com.jfzt.meeting.service.MeetingGroupService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 会议群组控制类
 * @author zilong.deng
 * @since 2024-06-04 11:03:18
 */
@RestController
@RequestMapping("/meeting")
public class MeetingGroupController {

    @Resource
    private MeetingGroupService meetingGroupService;

    /**
     * @return com.jfzt.meeting.common.Result<java.util.List < com.jfzt.meeting.entity.vo.MeetingGroupVO>>
     * @Description 群组查询
     * @Param [userId]
     */
    @GetMapping("/meeting-group-list")
    public Result<List<MeetingGroupVO>> getMeetingGroupList (MeetingGroupPageDTO meetingGroupPageDTO) {
        return meetingGroupService.checkGroup(meetingGroupPageDTO.getPageNum(),
                meetingGroupPageDTO.getPageSize(),
                meetingGroupPageDTO.getUserId(),
                meetingGroupPageDTO.getGroupName());
    }

    /**
     * @param meetingGroupDTO 群组入参
     * @return com.jfzt.meeting.common.Result<java.lang.Object>
     */
    @PostMapping("/meeting-group")
    public Result<Object> addMeetingGroup (@RequestBody MeetingGroupDTO meetingGroupDTO) {
        return meetingGroupService.addMeetingGroup(meetingGroupDTO);

    }

    /**
     * @return com.jfzt.meeting.common.Result<java.lang.Object>
     * @Description 群组修改
     * @Param [meetingGroupDTO]
     */

    @PutMapping("/meeting-group")
    public Result<Object> updateMeetingGroup (@RequestBody MeetingGroupDTO meetingGroupDTO) {
        return meetingGroupService.updateMeetingGroup(meetingGroupDTO);
    }

    /**
     * @return com.jfzt.meeting.common.Result<java.lang.Object>
     * @Description 群组删除
     * @Param [meetingGroupDTO]
     */
    @DeleteMapping("/meeting-group")
    public Result<Object> deleteMeetingGroup (@RequestParam Long id) {
        return meetingGroupService.deleteMeetingGroup(id);
    }
}