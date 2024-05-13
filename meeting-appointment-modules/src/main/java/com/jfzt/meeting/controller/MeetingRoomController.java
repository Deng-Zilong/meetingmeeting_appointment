package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingRoom;
import com.jfzt.meeting.entity.dto.TimePeriodDTO;
import com.jfzt.meeting.entity.vo.MeetingRoomStatusVO;
import com.jfzt.meeting.entity.vo.MeetingRoomVO;
import com.jfzt.meeting.entity.vo.TimePeriodStatusVO;
import com.jfzt.meeting.service.MeetingRoomService;
import jakarta.annotation.Resource;
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
     * @param timePeriodDTO 时间段
     * @return {@code Result<List<MeetingRoomVO>>}
     */
    @PostMapping("/createMeeting/availableMeetingRooms")
    public Result<List<MeetingRoomVO>> getAvailableMeetingRooms (@RequestBody TimePeriodDTO timePeriodDTO) {
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
     * @param meetingRoomVO 会议室
     * @return {@code Result<String>}
     */
    @DeleteMapping("/deleteMeetingRoom")
    public Result<String> deleteMeetingRoom (@RequestParam Long meetingRoomId) {
        return meetingRoomService.deleteMeetingRoom(meetingRoomId);

    }

    /**
     * 修改会议室状态
     *
     * @param id     会议室id
     * @param status 会议室状态（0暂停使用,1可使用/空闲 2为使用中不保存至数据库，实时获取）
     * @return {@code Result<Integer>}
     */
    @PutMapping("/updateStatus")
    public Result<Integer> updateStatus (@RequestParam("id") Long id, @RequestParam("status") Integer status) {
        return meetingRoomService.updateStatus(id, status);
    }
}
