package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingRecord;
import com.jfzt.meeting.entity.vo.MeetingRecordVO;
import com.jfzt.meeting.service.MeetingRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author zilong.deng
 * @date 2024/04/29
 */
@Slf4j
@RestController
@RequestMapping("/meeting")
public class MeetingRecordController {

    @Autowired
    private MeetingRecordService meetingRecordService;

    @GetMapping("/index/queryRecordNumber")
    public Result<Integer> queryRecordNumber () {
        return Result.success(meetingRecordService.getRecordNumber());
    }

    /**
     * @param userId 用户id
     * @return {@code Result<List<MeetingRecordVO>>}
     * @description 查询用户当天所有参与的今日会议记录
     */
    @GetMapping("/index/todayMeetingRecord")
    public Result<List<MeetingRecordVO>> queryRecordVoList (@RequestParam String userId) {
        List<MeetingRecordVO> recordvoList = meetingRecordService.getRecordVoList(userId);
        log.info("用户{}当天会议记录:{}", userId, recordvoList);
        return Result.success(recordvoList);
    }

    /**
     * @param userId 用户id
     * @return {@code Result<Page<MeetingRecordVO>>}
     * @description 根据用户id查询用户所有历史会议记录
     */
    @GetMapping("/meetingRecord/allMeetingRecord")
    public Result<List<MeetingRecordVO>> getRecordPage (@RequestParam String userId) {

        List<MeetingRecordVO> recordVoList = meetingRecordService.getAllRecordVoList(userId);
        if (recordVoList == null) {
            log.info("用户{}没有历史会议记录", userId);
            return Result.success(null);
        }
        log.info("用户{}当天会议记录:{}", userId, recordVoList);
        return Result.success(recordVoList);
    }

    /**
     * @param location 会议室位置
     * @param date     日期
     * @return {@code Result<List<MeetingRecord>>}
     */
    @PostMapping("/index/queryRecordByLocation")
    public Result<List<MeetingRecord>> queryRecordByLocation (@RequestBody String location, Date date) {


        return null;
    }
}
