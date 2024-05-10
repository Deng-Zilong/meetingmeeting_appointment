package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.dto.TimePeriodDTO;
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

import static com.jfzt.meeting.service.impl.SysUserServiceImpl.ADMIN_LEVEL;
import static com.jfzt.meeting.service.impl.SysUserServiceImpl.SUPER_ADMIN_LEVEL;

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

    @PostMapping("/addMeetingRoom")
    public Result<String> addMeetingRoom (@RequestBody @Valid MeetingRoomVO meetingRoomVO) {
        return meetingRoomService.addMeetingRoom(meetingRoomVO);

    }

    @DeleteMapping("/deleteMeetingRoom")
    public Result<String> deleteMeetingRoom (@RequestBody @Valid MeetingRoomVO meetingRoomVO) {
        if (meetingRoomService.deleteMeetingRoom(meetingRoomVO)) {
            return Result.success("删除成功");
        }
        return Result.fail("删除失败");

    }

    /**
     * 修改会议室状态
     * @param id 会议室id
     * @param status 会议室状态（0暂停使用,1可使用/空闲 2为使用中不保存至数据库，实时获取）
     * @return
     */
    @PutMapping("/updateStatus")
    public Result<Integer> updateStatus (@RequestParam("id") Long id, @RequestParam("status") Integer status) {
        // 获取当前登录用户的权限等级
        //Integer level = BaseContext.getCurrentLevel();
        Integer level = 1;
        if (SUPER_ADMIN_LEVEL.equals(level) || ADMIN_LEVEL.equals(level)){
            boolean result = meetingRoomService.updateStatus(id, status);
            if (result) {
                return Result.success("会议室状态修改成功!");
            }
            return Result.fail("会议室状态修改失败!");
        }
        return Result.fail("修改失败，请联系管理员获取权限！");

    }
}
