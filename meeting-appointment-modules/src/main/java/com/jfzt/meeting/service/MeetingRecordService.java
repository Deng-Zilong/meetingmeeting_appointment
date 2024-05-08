package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.entity.MeetingRecord;
import com.jfzt.meeting.entity.vo.MeetingRecordVO;

import java.util.List;

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

    //    /**
    //     * 获取用户参与的所有会议
    //     *
    //     * @param userId 用户id
    //     * @return {@code List<MeetingRecordVO>}
    //     */
    //    List<MeetingRecordVO> getAllRecordVoList (String userId);
}
