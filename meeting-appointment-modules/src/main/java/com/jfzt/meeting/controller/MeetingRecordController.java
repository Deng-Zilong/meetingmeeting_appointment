package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.dto.MeetingRecordDTO;
import com.jfzt.meeting.entity.dto.UpdateMeetingDTO;
import com.jfzt.meeting.entity.vo.MeetingPromptVO;
import com.jfzt.meeting.entity.vo.MeetingRecordVO;
import com.jfzt.meeting.service.MeetingRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 会议记录表(MeetingRecord)表控制层
 * @author zilong.deng
 * @since 2024-06-05 10:15:56
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
     * 查询当日会议总数
     * @return 会议总数
     */
    @GetMapping("/index/meeting-record-number")
    public Result<Integer> queryRecordNumber () {
        return Result.success(meetingRecordService.getRecordNumber());
    }

    /**
     * 查询用户当天所有参与的今日会议记录
     * @param userId 用户id
     * @return 会议记录VO
     */
    @GetMapping("/index/today-meeting-record")
    public Result<List<MeetingRecordVO>> queryRecordVoList (@RequestParam String userId) {
        List<MeetingRecordVO> recordvoList = meetingRecordService.getTodayMeetingRecord(userId);
        return Result.success(recordvoList);
    }

    /**
     * 根据用户id查询用户所有历史会议记录
     * @param userId 用户id
     * @param page   页码
     * @param limit  每页条数
     * @return 会议记录VO
     */
    @GetMapping("/meeting-record/all-meeting-record")
    public Result<List<MeetingRecordVO>> getRecordPage (@RequestParam String userId,
                                                        @RequestParam Long page, @RequestParam Long limit) {
        List<MeetingRecordVO> recordVoList = meetingRecordService.getAllRecordVoListPage(userId, page, limit);
        return Result.success(recordVoList);
    }

    /**
     * 取消会议
     * @param updateMeetingDTO 会议信息
     * @return 取消结果
     */
    @PutMapping("/index/cancel-meeting-record")
    public Result<String> cancelMeetingRecord (@RequestBody UpdateMeetingDTO updateMeetingDTO) {
        return meetingRecordService.cancelMeetingRecord(updateMeetingDTO.getUserId(), updateMeetingDTO.getMeetingId());
    }


    /**
     * 删除会议, 首页今日会议不展示，历史记录不做删除，非取消会议
     * @param userId    用户id
     * @param meetingId 会议id
     * @return 删除结果
     */
    @DeleteMapping("/index/meeting-record")
    public Result<String> deleteMeetingRecord (@RequestParam String userId, @RequestParam Long meetingId) {
        return meetingRecordService.deleteMeetingRecord(userId, meetingId);
    }

    /**
     * 添加会议
     * @param meetingRecordDTO 会议信息
     * @return 添加结果
     */
    @PostMapping("/index/meeting-record")
    public Result<Objects> addMeetingRecord (@RequestBody MeetingRecordDTO meetingRecordDTO) {
        return meetingRecordService.addMeeting(meetingRecordDTO);
    }

    /**
     * 更新会议
     * @param meetingRecordDTO 会议信息
     * @return 更新结果
     */
    @PutMapping("/index/meeting-record")
    public Result<List<MeetingRecordVO>> updateMeetingRecord (@RequestBody MeetingRecordDTO meetingRecordDTO) {
        return meetingRecordService.updateMeeting(meetingRecordDTO);
    }

    /**
     * 查询所有会议记录
     * @param pageNum      页码
     * @param pageSize     每页显示条数
     * @param currentLevel 当前登录用户的权限等级
     * @return 会议记录VO
     */
    @GetMapping("meeting-record/select-all-meeting-record")
    public Result<List<MeetingRecordVO>> getRecordPage (@RequestParam Long pageNum, @RequestParam Long pageSize,
                                                        @RequestParam("currentLevel") Integer currentLevel) {
        List<MeetingRecordVO> allMeetingRecordVoListPage =
                meetingRecordService.getAllMeetingRecordVoListPage(pageNum, pageSize, currentLevel);
        return Result.success(allMeetingRecordVoListPage);

    }

    /**
     * 会议创建自动提示
     * @param userId 用户id
     * @return 会议提示VO
     */
    @GetMapping("/index/meeting-record-prompt")
    public Result<MeetingPromptVO> prompt(@RequestParam String userId) {
        return meetingRecordService.prompt(userId);
    }
}
