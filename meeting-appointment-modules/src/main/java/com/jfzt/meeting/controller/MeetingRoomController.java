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
 * 会议室管理
 * @author zilong.deng
 * @since 2024-06-05 11:03:58
 */
@Slf4j
@RestController
@RequestMapping("/meeting")
public class MeetingRoomController {

    private MeetingRoomService meetingRoomService;

    /**
     * setter注入
     **/
    @Autowired
    public void setMeetingRoomService (MeetingRoomService meetingRoomService) {
        this.meetingRoomService = meetingRoomService;
    }

    /**
     * 根据会议室id和日期查询时间段可预约状态
     * @param id   会议室id
     * @param date 日期
     * @return 时间段状态
     */
    @GetMapping("/index/time-period-status")
    public Result<List<TimePeriodStatusVO>> getTimePeriodStatusByIdAndDate (@RequestParam Long id, @RequestParam LocalDate date) {
        return meetingRoomService.getTimePeriodStatusByIdAndDate(id, date);
    }

    /**
     * 查询当日可预约（0：已过期1：已占用2：可预约）
     * @return 时间段状态
     */
    @GetMapping("/index/today-time-period-status")
    public Result<List<Integer>> getTodayTimePeriodStatus () {
        List<Integer> result = meetingRoomService.getTodayTimePeriodStatus();
        return Result.success(result);
    }

    /**
     * 查询会议室状态
     * @return 会议室状态
     */
    @GetMapping("/index/meeting-room-status")
    public Result<List<MeetingRoomStatusVO>> getMeetingRoomStatus () {
        List<MeetingRoomStatusVO> meetingRoomStatusVOList = meetingRoomService.getMeetingRoomStatus();
        return Result.success(meetingRoomStatusVOList);
    }

    /**
     * 根据时间段查询可用会议室
     * @param timePeriodDTO 时间段
     * @return 可用会议室
     */
    @GetMapping("/create-meeting/available-meeting-rooms")
    public Result<List<MeetingRoomVO>> getAvailableMeetingRooms (TimePeriodDTO timePeriodDTO) {
        return meetingRoomService.getAvailableMeetingRooms(timePeriodDTO.getStartTime(), timePeriodDTO.getEndTime());
    }

    /**
     * 新增会议室
     * @param meetingRoom 会议室
     * @return 新增结果
     */
    @PostMapping("/add-meeting-room")
    public Result<Integer> addMeetingRoom (@RequestBody MeetingRoom meetingRoom) {
        return meetingRoomService.addMeetingRoom(meetingRoom);
    }


    /**
     * 删除会议室
     * @param id 会议室id
     * @return 删除结果
     */
    @DeleteMapping("/delete-meeting-room")
    public Result<Integer> deleteMeetingRoom (@RequestParam Long id, @RequestParam("currentLevel") Integer currentLevel) {
        return meetingRoomService.deleteMeetingRoom(id, currentLevel);
    }

    /**
     * 修改会议室状态
     * @param meetingRoomDTO 会议室DTO对象
     * @return 修改结果
     */
    @PutMapping("/update-status")
    public Result<Integer> updateStatus (@RequestBody MeetingRoomDTO meetingRoomDTO) {
        return meetingRoomService.updateStatus(meetingRoomDTO);
    }

    /**
     * 根据会议室id修改会议室
     * @param meetingRoomDTO 会议室DTO对象
     * @return 修改结果
     */
    @PutMapping("/update-room")
    public Result<Integer> updateRoom (@RequestBody MeetingRoomDTO meetingRoomDTO) {
        return meetingRoomService.updateRoom(meetingRoomDTO);
    }


    /**
     * 查询被禁用的会议室的id
     * @param currentLevel 当前登录用户的权限等级
     * @return 会议室的id
     */
    @GetMapping("/index/all-room")
    public Result<List<Long>> selectUsableRoom (@RequestParam("currentLevel") Integer currentLevel) {
        return meetingRoomService.selectUsableRoom(currentLevel);

    }
}
