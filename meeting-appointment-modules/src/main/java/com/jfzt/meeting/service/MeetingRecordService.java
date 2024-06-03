package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingRecord;
import com.jfzt.meeting.entity.dto.MeetingRecordDTO;
import com.jfzt.meeting.entity.vo.MeetingPromptVO;
import com.jfzt.meeting.entity.vo.MeetingRecordVO;
import com.jfzt.meeting.entity.vo.PeriodTimesVO;

import java.util.List;
import java.util.Objects;

/**
 * @author zilong.deng
 * @description 针对表【meeting_record(会议记录表)】的数据库操作Service
 * @since 2024-04-28 11:47:39
 */
public interface MeetingRecordService extends IService<MeetingRecord> {


    /**
     * @param userId 用户id
     * @return {@code List<MeetingRecordVO>}
     * @description 获取当天用户参与的所有会议
     */
    List<MeetingRecordVO> getTodayMeetingRecord (String userId);

    /**
     * @return {@code Integer}
     * @description 查询今日中心会议总次数
     */
    Integer getRecordNumber ();

    /**
     * @param meetingRecord 会议记录
     * @return {@code MeetingRecord}
     * @description 更新会议记录
     */
    MeetingRecord updateRecordStatus (MeetingRecord meetingRecord);

    /**
     * @description 更新今日会议记录状态
     */
    void updateTodayRecordStatus ();

    /**
     * @param userId 用户id
     * @return {@code List<MeetingRecordVO>}
     * @description 分页获取用户参与的所有会议
     */
    List<MeetingRecordVO> getAllRecordVoListPage (String userId, Long pageNum, Long pageSize);


    /**
     * @param pageNum      页码
     * @param pageSize     每页条数
     * @param currentLevel 当前登录用户的权限等级
     * @return @return {@code List<MeetingRecordVO>}
     * @description 分页获取所有会议记录
     */
    List<MeetingRecordVO> getAllMeetingRecordVoListPage (Long pageNum, Long pageSize, Integer currentLevel);


    /**
     * @param userId    用户id
     * @param meetingId 会议id
     * @return {@code Boolean}
     * @description 根据会议记录id删除会议（首页不展示，历史记录展示）
     */
    Result<String> deleteMeetingRecord (String userId, Long meetingId);

    /**
     * @param userId    用户id
     * @param meetingId 会议id
     * @return {@code Boolean}
     * @description 根据会议记录id取消会议
     */
    Result<String> cancelMeetingRecord (String userId, Long meetingId);

    /**
     * @return com.jfzt.meeting.common.Result<java.util.Objects>
     * @Description 新增会议
     */
    Result<Objects> addMeeting (MeetingRecordDTO meetingRecordDTO);

    /**
     * @return com.jfzt.meeting.common.Result<java.util.List < com.jfzt.meeting.entity.vo.MeetingRecordVO>>
     * @Description 更新会议
     * @Param [meetingRecordDTO]
     */
    Result<List<MeetingRecordVO>> updateMeeting (MeetingRecordDTO meetingRecordDTO);
    /**
     * @Description 统计七日内各时间段预约频率
     * @return com.jfzt.meeting.common.Result<java.util.List<com.jfzt.meeting.entity.vo.PeriodTimesVO>>
     */
    Result<List<PeriodTimesVO>> getTimePeriodTimes();
    /**
     * @Description 会议创建自动提示
     * @Param userId
     * @return MeetingPromptVO
     */
    Result<MeetingPromptVO> prompt(String userId);
}
