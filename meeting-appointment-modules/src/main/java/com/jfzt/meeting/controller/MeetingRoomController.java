package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.vo.MeetingRoomStatusVO;
import com.jfzt.meeting.entity.vo.MeetingRoomVO;
import com.jfzt.meeting.entity.vo.TimePeriodStatusVO;
import com.jfzt.meeting.service.MeetingRoomService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * @author zilong.deng
 * @date 2024/04/28
 */

@Slf4j
@RestController
@RequestMapping("/meeting")
public class MeetingRoomController {

    @Resource
    private MeetingRoomService meetingRoomService;

    /**
     * 根据会议室id和日期查询时间段可预约状态
     *
     * @param id   会议室id
     * @param date 日期
     * @return {@code Result<List<TimePeriodStatusVO>>}
     */
    @GetMapping("/index/isBusyByIdAndDate")
    public Result<List<TimePeriodStatusVO>> isBusyByLocationAndDate (@RequestParam Long id, @RequestParam LocalDate date) {
        return meetingRoomService.isBusyByIdAndDate(id, date);
    }

    /**
     * 查询当日时间段可预约状态（0：已过期1：已占用2：可预约）
     *
     * @return {@code Result<List<Integer>>}
     */
    @GetMapping("/index/isBusy")
    public Result<List<Integer>> isBusy () {
        List<Integer> result = meetingRoomService.isBusy();
        return Result.success(result);
    }

    /**
     * 查询会议室状态
     *
     * @return {@code Result<List<MeetingRoomStatusVO>>}
     */
    @GetMapping("/index/meetingRoomStatus")
    public Result<List<MeetingRoomStatusVO>> getMeetingRoomStatus () {
        List<MeetingRoomStatusVO> meetingRoomStatusVOList = meetingRoomService.getMeetingRoomStatus();
        return Result.success(meetingRoomStatusVOList);
    }

    @PostMapping("/addMeetingRoom")
    public Result<String> addMeetingRoom (@RequestBody @Valid MeetingRoomVO meetingRoomVO) {
        return meetingRoomService.addMeetingRoom(meetingRoomVO);

    }

    @DeleteMapping("/deleteMeetingRoom")
    public Result<String> deleteMeetingRoom (@RequestBody @Valid MeetingRoomVO meetingRoomVO) {

        Boolean result = meetingRoomService.deleteMeetingRoom(meetingRoomVO);
        if (result) {
            return Result.success("添加成功");
        }
        return Result.fail("添加失败!");
    }

}
