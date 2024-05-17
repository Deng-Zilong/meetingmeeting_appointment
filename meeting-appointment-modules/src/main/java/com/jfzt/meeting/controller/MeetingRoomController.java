package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingRoom;
import com.jfzt.meeting.entity.dto.MeetingRoomDTO;
import com.jfzt.meeting.entity.dto.TimePeriodDTO;
import com.jfzt.meeting.entity.vo.MeetingRoomStatusVO;
import com.jfzt.meeting.entity.vo.MeetingRoomVO;
import com.jfzt.meeting.entity.vo.TimePeriodStatusVO;
import com.jfzt.meeting.service.MeetingRoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    private MeetingRoomService meetingRoomService;

    @Autowired
    public void setMeetingRoomService (MeetingRoomService meetingRoomService) {
        this.meetingRoomService = meetingRoomService;
    }

    /**
     * 根据会议室id和日期查询时间段可预约状态
     *
     * @param id   会议室id
     * @param date 日期
     * @return {@code Result<List<TimePeriodStatusVO>>}
     */
    @GetMapping("/index/isBusyByIdAndDate")
    public Result<List<TimePeriodStatusVO>> isBusyByIdAndDate (@RequestParam Long id, @RequestParam LocalDate date) {
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

    /**
     * 根据时间段查询可用会议室
     *
     * @param 时间段
     * @return {@code Result<List<MeetingRoomVO>>}
     */
    @GetMapping("/createMeeting/availableMeetingRooms")
    public Result<List<MeetingRoomVO>> getAvailableMeetingRooms (TimePeriodDTO timePeriodDTO) {
        return meetingRoomService.getAvailableMeetingRooms(timePeriodDTO.getStartTime(), timePeriodDTO.getEndTime());
    }

    /**
     * @param meetingRoom 会议室
     * @return {@code Result<String>}
     */
    @PostMapping("/addMeetingRoom")
    public Result<String> addMeetingRoom (@RequestBody MeetingRoom meetingRoom) {
        return meetingRoomService.addMeetingRoom(meetingRoom);

    }

    /**
     * @param meetingRoomId 会议室
     * @return {@code Result<String>}
     */
    @DeleteMapping("/deleteMeetingRoom")
    public Result<String> deleteMeetingRoom (@RequestParam Long meetingRoomId) {
        return meetingRoomService.deleteMeetingRoom(meetingRoomId);

    }

    /**
     * 修改会议室状态
     *
     * @param meetingRoomDTO 会议室DTO对象
     * @return com.jfzt.meeting.common.Result<java.lang.Integer>
     */
    @PutMapping("/updateStatus")
    public Result<Integer> updateStatus (@RequestBody MeetingRoomDTO meetingRoomDTO) {
        return meetingRoomService.updateStatus(meetingRoomDTO);
    }


    /**
     * 查询被禁用的会议室的id
     *
     * @param currentLevel 当前登录用户的权限等级
     * @return com.jfzt.meeting.common.Result<java.util.List < < java.lang.Integer>>
     */
    @GetMapping("/index/allRoom")
    public Result<List<Long>> selectUsableRoom (@RequestParam("currentLevel") Integer currentLevel) {
        return meetingRoomService.selectUsableRoom(currentLevel);

    }
}
