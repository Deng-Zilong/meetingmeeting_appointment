package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.vo.MeetingRoomOccupancyVO;
import com.jfzt.meeting.service.MeetingRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import com.jfzt.meeting.entity.vo.PeriodTimesVO;
import com.jfzt.meeting.service.MeetingRecordService;
import jakarta.annotation.Resource;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: chenyu.di
 * @since: 2024-05-27 16:41
 */
@RestController
@RequestMapping("/meeting/statistics")
public class StatisticsController {

    @Resource
    private MeetingRecordService meetingRecordService;


    @Autowired
    MeetingRoomService meetingRoomService;

    /**
     * @return {@code Result<List<MeetingRoomOccupancyVO>>}
     * @description 统计七日内各会议室占用率
     */
    @GetMapping("/meeting-room-occupancy")
    public Result<List<MeetingRoomOccupancyVO>> getMeetingRoomOccupancy () {
        return meetingRoomService.getAllMeetingRoomOccupancy();
    }

    @GetMapping("/time-period")
    public Result<List<PeriodTimesVO>> getTimePeriodTimes () {
        return meetingRecordService.getTimePeriodTimes();
    }
}