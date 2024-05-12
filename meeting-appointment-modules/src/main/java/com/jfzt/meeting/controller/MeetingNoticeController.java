package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.constant.MessageConstant;
import com.jfzt.meeting.entity.MeetingNotice;
import com.jfzt.meeting.service.MeetingNoticeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
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
    public Result<Integer> addNotice(@RequestBody MeetingNotice meetingNotice) {
        return meetingNoticeService.addNotice(meetingNotice);
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
        List<String> stringList = meetingNoticeService.selectAll(meetingNotice);
        if (!stringList.isEmpty()){
            return Result.success(stringList);
        }
        return Result.success(null);
    }
}