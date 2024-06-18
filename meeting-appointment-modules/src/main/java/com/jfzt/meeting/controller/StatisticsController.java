package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.dto.DatePeriodDTO;
import com.jfzt.meeting.entity.vo.MeetingRoomOccupancyVO;
import com.jfzt.meeting.entity.vo.MeetingRoomSelectionRateVO;
import com.jfzt.meeting.entity.vo.PeriodTimesVO;
import com.jfzt.meeting.service.MeetingRecordService;
import com.jfzt.meeting.service.MeetingRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 统计分析(Statistics)控制层
 * @author zilong.deng
 * @since 2024-06-04 10:59:43
 */
@RestController
@RequestMapping("/meeting/statistics")
public class StatisticsController {
    private MeetingRoomService meetingRoomService;
    private MeetingRecordService meetingRecordService;

    /**
     * setter注入
     */
    @Autowired
    public void setMeetingRecordService (MeetingRecordService meetingRecordService) {
        this.meetingRecordService = meetingRecordService;
    }

    @Autowired
    public void setMeetingRoomService (MeetingRoomService meetingRoomService) {
        this.meetingRoomService = meetingRoomService;
    }

    /**
     * 统计时间区间内各会议室选择率
     * @param datePeriodDTO 日期区间
     * @return 会议室占用次数比例
     */
    @GetMapping("/meeting-room-selection")
    public Result<List<MeetingRoomSelectionRateVO>> getMeetingRoomProportion (DatePeriodDTO datePeriodDTO) {
        return meetingRoomService.getAllMeetingRoomProportion(datePeriodDTO);
    }


    /**
     * 统计时间区间内各会议室占用率以及被占用次数前三的时间段
     * @param datePeriodDTO 日期区间
     * @return 用率以及被占用次数前三的时间段
     */
    @GetMapping("/meeting-room-occupancy")
    public Result<List<MeetingRoomOccupancyVO>> getMeetingRoomOccupancy (DatePeriodDTO datePeriodDTO) {
        return meetingRoomService.getAllMeetingRoomOccupancy(datePeriodDTO);
    }

    /**
     * 统计七日内各时间段预约频率
     * @return 预约频率
     */
    @GetMapping("/time-period")
    public Result<List<PeriodTimesVO>> getTimePeriodTimes () {
        return meetingRecordService.getTimePeriodTimes();
    }
}