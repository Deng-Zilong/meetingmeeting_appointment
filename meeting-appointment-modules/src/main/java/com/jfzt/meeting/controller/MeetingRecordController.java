package com.jfzt.meeting.controller;

import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.dto.MeetingRecordDTO;
import com.jfzt.meeting.entity.vo.MeetingRecordVO;
import com.jfzt.meeting.service.MeetingRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import static com.jfzt.meeting.constant.MessageConstant.DELETE_FAIL;

/**
 * @author zilong.deng
 * @date 2024/04/29
 */

@Slf4j
@RestController
@RequestMapping("/meeting")
public class MeetingRecordController {

    @Autowired
    private MeetingRecordService meetingRecordService;


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
        log.info("用户{}当天会议记录:{}", userId, recordvoList);
        return Result.success(recordvoList);
    }

    /**
     * @param userId 用户id
     * @return {@code Result<Page<MeetingRecordVO>>}
     * @description 根据用户id查询用户所有历史会议记录
     */
    @GetMapping("/meetingRecord/allMeetingRecord")
    public Result<List<MeetingRecordVO>> getRecordPage (@RequestParam String userId, @RequestParam Long page, @RequestParam Long limit) {

        List<MeetingRecordVO> recordVoList = meetingRecordService.getAllRecordVoListPage(userId, page, limit);
        if (recordVoList == null) {
            log.info("用户{}没有历史会议记录", userId);
            return Result.success();
        }
        log.info("用户{}当天会议记录:{}", userId, recordVoList);
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
        Boolean result = meetingRecordService.deleteMeetingRecord(userId, meetingId);
        if (result) {
            log.info("用户{}删除会议{}成功", userId, meetingId);
            return Result.success();
        }
        log.info("用户{}删除会议{}失败", userId, meetingId);
        return Result.fail(DELETE_FAIL);
    }

    /**
     * @return com.jfzt.meeting.common.Result<java.util.Objects>
     * @Description 新增会议
     * @Param [meetingRecordDTO]
     */
    @PostMapping("/index/addMeetingRecord")
    public Result<Objects> addMeetingRecord (@RequestBody MeetingRecordDTO meetingRecordDTO) {
        return meetingRecordService.addMeeting(meetingRecordDTO);
    }

    /**
     * @return com.jfzt.meeting.common.Result<java.util.List < com.jfzt.meeting.entity.vo.MeetingRecordVO>>
     * @throws
     * @Description 更新会议
     * @Param [meetingRecordDTO]
     */
    @PutMapping("/index/updateMeetingRecord")
    public Result<List<MeetingRecordVO>> updateMeetingRecord (@RequestBody MeetingRecordDTO meetingRecordDTO) {
        return meetingRecordService.updateMeeting(meetingRecordDTO);
    }

    /**
     * @param page  页码
     * @param limit 每页显示条数
     * @return {@code Result<List<MeetingRecordVO>>}
     * @description 查询所有会议记录
     */
    @GetMapping("/meetingRecord/selectAllMeetingRecord")
    public Result<List<MeetingRecordVO>> getRecordPage (@RequestParam Long page, @RequestParam Long limit) {
        return meetingRecordService.getAllMeetingRecordVoListPage(page, limit);
    }
}
