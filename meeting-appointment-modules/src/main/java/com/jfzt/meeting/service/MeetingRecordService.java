package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingRecord;
import com.jfzt.meeting.entity.dto.MeetingRecordDTO;
import com.jfzt.meeting.entity.vo.MeetingRecordVO;

import java.util.List;
import java.util.Objects;

/**
 * @author zilong.deng
 * @description 针对表【meeting_record(会议记录表)】的数据库操作Service
 * @since 2024-04-28 11:47:39
 */
public interface MeetingRecordService extends IService<MeetingRecord> {


    /**
     * 获取当天用户参与的所有会议
     *
     * @param userId 用户id
     * @return {@code List<MeetingRecordVO>}
     */
    List<MeetingRecordVO> getRecordVoList (String userId);

    /**
     * 查询今日中心会议总次数
     *
     * @return {@code Integer}
     */
    Integer getRecordNumber ();

    void updateRecordStatus (MeetingRecord meetingRecord);

    void updateTodayRecordStatus ();

    /**
     * 分页获取用户参与的所有会议
     *
     * @param userId 用户id
     * @return {@code List<MeetingRecordVO>}
     */
    List<MeetingRecordVO> getAllRecordVoListPage (String userId, Long pageNum, Long pageSize);


    /**
     * 分页获取所有会议记录
     *
     * @return {@code List<MeetingRecordVO>}
     */
    Result<List<MeetingRecordVO>> getAllMeetingRecordVoListPage (Long pageNum, Long pageSize);

    /**
     * 根据会议记录id删除会议（首页不展示，历史记录展示）
     *
     * @param userId    用户id
     * @param meetingId 会议id
     * @return {@code Boolean}
     */
    Result<String> deleteMeetingRecord (String userId, Long meetingId);

    /**
     * 根据会议记录id取消会议
     *
     * @param userId    用户id
     * @param meetingId 会议id
     * @return {@code Boolean}
     */
    Result<String> cancelMeetingRecord (String userId, Long meetingId);

    /**
     * @return com.jfzt.meeting.common.Result<java.util.Objects>
     * @throws
     * @Description 新增会议
     * @Param [meetingRecordDTO]
     */
    Result<Objects> addMeeting (MeetingRecordDTO meetingRecordDTO);

    /**
     * @return com.jfzt.meeting.common.Result<java.util.List < com.jfzt.meeting.entity.vo.MeetingRecordVO>>
     * @throws
     * @Description 更新会议
     * @Param [meetingRecordDTO]
     */
    Result<List<MeetingRecordVO>> updateMeeting (MeetingRecordDTO meetingRecordDTO);
}
