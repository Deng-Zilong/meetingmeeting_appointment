package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.dto.MeetingRecordDTO;
import com.jfzt.meeting.entity.dto.UpdateMeetingDTO;
import com.jfzt.meeting.entity.vo.MeetingRecordVO;
import com.jfzt.meeting.service.MeetingRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @author zilong.deng
 * @date 2024/04/29
 */
@Slf4j
@RestController
@RequestMapping("/meeting")
public class MeetingRecordController {

    private MeetingRecordService meetingRecordService;

    /**
     * setter注入
     */
    @Autowired
    public void setMeetingRecordService (MeetingRecordService meetingRecordService) {
        this.meetingRecordService = meetingRecordService;
    }

    /**
     * @return {@code Result<Integer>}
     * @description 查询当日会议总数
     */
    @GetMapping("/index/meeting-record-number")
    public Result<Integer> queryRecordNumber () {
        return Result.success(meetingRecordService.getRecordNumber());
    }

    /**
     * @param userId 用户id
     * @return {@code Result<List<MeetingRecordVO>>}
     * @description 查询用户当天所有参与的今日会议记录
     */
    @GetMapping("/index/today-meeting-record")
    public Result<List<MeetingRecordVO>> queryRecordVoList (@RequestParam String userId) {
        List<MeetingRecordVO> recordvoList = meetingRecordService.getTodayMeetingRecord(userId);
        return Result.success(recordvoList);
    }

    /**
     * @param userId 用户id
     * @param page   页码
     * @param limit  每页条数
     * @return {@code Result<List<MeetingRecordVO>>}
     * @description 根据用户id查询用户所有历史会议记录
     */
    @GetMapping("/meeting-record/all-meeting-record")
    public Result<List<MeetingRecordVO>> getRecordPage (@RequestParam String userId, @RequestParam Long page, @RequestParam Long limit) {
        List<MeetingRecordVO> recordVoList = meetingRecordService.getAllRecordVoListPage(userId, page, limit);
        return Result.success(recordVoList);
    }

    /**
     * @return {@code Result<String>}
     * @description 取消会议
     */
    @PutMapping("/index/cancel-meeting-record")
    public Result<String> cancelMeetingRecord (@RequestBody UpdateMeetingDTO updateMeetingDTO) {
        return meetingRecordService.cancelMeetingRecord(updateMeetingDTO.getUserId(), updateMeetingDTO.getMeetingId());
    }


    /**
     * @return {@code Result<String>}
     * @description 删除会议, 首页今日会议不展示，历史记录不做删除，非取消会议
     */
    @DeleteMapping("/index/meeting-record")
    public Result<String> deleteMeetingRecord (@RequestParam String userId, @RequestParam Long meetingId) {
        return meetingRecordService.deleteMeetingRecord(userId, meetingId);
    }

    /**
     * @param meetingRecordDTO 会议信息
     * @return com.jfzt.meeting.common.Result<java.util.Objects>
     * @Description 新增会议
     */
    @PostMapping("/index/meeting-record")
    public Result<Objects> addMeetingRecord (@RequestBody MeetingRecordDTO meetingRecordDTO) {
        return meetingRecordService.addMeeting(meetingRecordDTO);
    }

    /**
     * @param meetingRecordDTO 会议信息
     * @return com.jfzt.meeting.common.Result<java.util.List < com.jfzt.meeting.entity.vo.MeetingRecordVO>>
     * @Description 更新会议
     */
    @PutMapping("/index/meeting-record")
    public Result<List<MeetingRecordVO>> updateMeetingRecord (@RequestBody MeetingRecordDTO meetingRecordDTO) {
        return meetingRecordService.updateMeeting(meetingRecordDTO);
    }

    /**
     * @param pageNum      页码
     * @param pageSize     每页显示条数
     * @param currentLevel 当前登录用户的权限等级
     * @return com.jfzt.meeting.common.Result<java.util.List < com.jfzt.meeting.entity.vo.MeetingRecordVO>>
     * @description 查询所有会议记录
     */
    @GetMapping("meeting-record/select-all-meeting-record")
    public Result<List<MeetingRecordVO>> getRecordPage (@RequestParam Long pageNum, @RequestParam Long pageSize,
                                                        @RequestParam("currentLevel") Integer currentLevel) {
        List<MeetingRecordVO> allMeetingRecordVoListPage =
                meetingRecordService.getAllMeetingRecordVoListPage(pageNum, pageSize, currentLevel);
        return Result.success(allMeetingRecordVoListPage);

    }
}
