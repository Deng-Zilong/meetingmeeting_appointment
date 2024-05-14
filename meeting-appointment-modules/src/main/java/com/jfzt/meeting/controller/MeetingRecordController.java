package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingRecord;
import com.jfzt.meeting.entity.dto.MeetingRecordDTO;
import com.jfzt.meeting.entity.vo.MeetingRecordVO;
import com.jfzt.meeting.exception.ErrorCodeEnum;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.service.MeetingRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.jfzt.meeting.constant.MessageConstant.*;

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
        return Result.success(recordvoList);
    }

    /**
     * @param userId 用户id
     * @param page   页码
     * @param limit  每页条数
     * @return {@code Result<List<MeetingRecordVO>>}
     * @description 根据用户id查询用户所有历史会议记录
     */
    @GetMapping("/meetingRecord/allMeetingRecord")
    public Result<List<MeetingRecordVO>> getRecordPage (@RequestParam String userId, @RequestParam Long page, @RequestParam Long limit) {
        List<MeetingRecordVO> recordVoList = meetingRecordService.getAllRecordVoListPage(userId, page, limit);
        return Result.success(recordVoList);
    }

    /**
     * 取消会议
     *
     * @param userId    用户id
     * @param meetingId 会议id
     * @return {@code Result<String>}
     */
    @PostMapping("/index/cancelMeetingRecord")
    public Result<String> cancelMeetingRecord (@RequestParam String userId, @RequestParam Long meetingId) {
        return meetingRecordService.cancelMeetingRecord(userId, meetingId);
    }

    /**
     * 删除会议
     *
     * @param userId    用户id
     * @param meetingId 会议id
     * @return {@code Result<String>}
     */
    @DeleteMapping("/index/deleteMeetingRecord")
    public Result<String> deleteMeetingRecord (@RequestParam String userId, @RequestParam Long meetingId) {
        return meetingRecordService.deleteMeetingRecord(userId, meetingId);
    }

    /**
     * @param meetingRecordDTO 会议信息
     * @return com.jfzt.meeting.common.Result<java.util.Objects>
     * @Description 新增会议
     */
    @PostMapping("/index/addMeetingRecord")
    public Result<Objects> addMeetingRecord (@RequestBody MeetingRecordDTO meetingRecordDTO) {
        return meetingRecordService.addMeeting(meetingRecordDTO);
    }

    /**
     * @param meetingRecordDTO 会议信息
     * @return com.jfzt.meeting.common.Result<java.util.List < com.jfzt.meeting.entity.vo.MeetingRecordVO>>
     * @Description 更新会议
     */
    @PutMapping("/index/updateMeetingRecord")
    public Result<List<MeetingRecordVO>> updateMeetingRecord (@RequestBody MeetingRecordDTO meetingRecordDTO) {
        return meetingRecordService.updateMeeting(meetingRecordDTO);
    }

    /**
     * @param pageNum  页码
     * @param pageSize 每页显示条数
     * @return com.jfzt.meeting.common.Result<java.util.List<com.jfzt.meeting.entity.vo.MeetingRecordVO>>
     * @description 查询所有会议记录
     */
    @GetMapping("/meetingRecord/selectAllMeetingRecord")
    public Result<List<MeetingRecordVO>> getRecordPage (@RequestParam Long pageNum, @RequestParam Long pageSize) {
        return meetingRecordService.getAllMeetingRecordVoListPage(pageNum, pageSize);
    }
}
