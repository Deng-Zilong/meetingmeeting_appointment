package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingNotice;
import com.jfzt.meeting.entity.vo.MeetingNoticeVO;
import com.jfzt.meeting.service.MeetingNoticeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 会议公告控制类
 * @Author: xuchang.yang
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
     * @param meetingNoticeVO 公告信息VO对象
     * @return com.jfzt.meeting.common.Result<java.lang.Integer>
     */
    @PostMapping("/addNotice")
    public Result<Integer> addNotice (@RequestBody MeetingNoticeVO meetingNoticeVO) {
        return meetingNoticeService.addNotice(meetingNoticeVO);
    }

    /**
     * 查询所有公告
     * @param meetingNotice 公告信息对象
     * @return com.jfzt.meeting.common.Result<java.util.List<java.lang.String>>
     */
    @GetMapping("/getNotice")
    public Result<List<String>> getNotice (MeetingNotice meetingNotice) {
        return meetingNoticeService.selectAll(meetingNotice);

    }
}