package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.vo.MeetingNoticeVO;
import com.jfzt.meeting.service.MeetingNoticeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 会议公告控制类
 * @author xuchang.yang
 * @since 2024-04-29 16:44
 */
@RestController
@RequestMapping("meeting/meeting-notice")
public class MeetingNoticeController {

    @Resource
    private MeetingNoticeService meetingNoticeService;

    /**
     * 新增通告信息
     * @param meetingNoticeVO 公告信息VO对象
     * @return 添加结果
     */
    @PostMapping("/add-notice")
    public Result<Integer> addNotice (@RequestBody MeetingNoticeVO meetingNoticeVO) {
        return meetingNoticeService.addNotice(meetingNoticeVO);
    }

    /**
     * 查询所有公告
     * @return 公告
     */
    @GetMapping("/get-notice")
    public Result<List<String>> getNotice () {
        return Result.success(meetingNoticeService.selectAll());

    }
}