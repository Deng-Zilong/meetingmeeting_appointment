package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingNotice;
import com.jfzt.meeting.entity.dto.MeetingGroupDTO;
import com.jfzt.meeting.entity.vo.MeetingGroupVO;
import com.jfzt.meeting.service.MeetingGroupService;
import com.jfzt.meeting.service.MeetingNoticeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.util.List;

/**
 * 会议群组控制类
 *
 * @Author: chenyu.di
 * @since: 2024-04-29 16:44
 */
@CrossOrigin
@RestController
@RequestMapping("/meetingNotice")
public class MeetingNoticeController {

    @Resource
    private MeetingNoticeService meetingNoticeService;


    /**
     * 新增通告信息
     * @param meetingNotice
     * @return
     */
    @PostMapping("/addNotice")
    public Result<Object> addNotice(@RequestBody MeetingNotice meetingNotice) {
        return Result.success(meetingNoticeService.addNotice(meetingNotice));
    }

    /**
     * 删除通告
     * @param id
     * @return
     */
    @DeleteMapping("/deleteNotice/{id}")
    public Result<Object> deleteMeetingGroup(@PathVariable("id") Long id) {
        return Result.success(meetingNoticeService.deleteNotice(id));
    }

    /**
     * 查询所有公告
     * @param meetingNotice
     * @return
     */
    @GetMapping("/getNotice")
    public Result<List<String>> getNotice(MeetingNotice meetingNotice){
        return  Result.success(meetingNoticeService.selectAll(meetingNotice));
    }
}