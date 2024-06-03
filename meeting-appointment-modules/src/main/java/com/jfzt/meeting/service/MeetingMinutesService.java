package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingMinutes;
import com.jfzt.meeting.entity.MeetingMinutesVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zilong.deng
 * @description 针对表【meeting_minutes(会议纪要)】的数据库操作Service
 * @createDate 2024-05-29 18:04:16
 */
@Service
public interface MeetingMinutesService extends IService<MeetingMinutes> {

    /**
     * @param meetingMinutes 会议记录id或用户id
     * @return {@code List<MeetingMinutesVO>}
     * @description 根据会议记录id或用户id查询会议纪要
     */
    List<MeetingMinutesVO> getMeetingMinutes (MeetingMinutes meetingMinutes);


    /**
     * @param meetingMinutes userId id
     * @description 根据用户id 纪要id删除指定纪要
     */
    void deleteMeetingMinutes (MeetingMinutes meetingMinutes);

    /**
     * @param meetingRecordId 会议id
     * @description 根据会议id删除所有会议纪要
     */
    void deleteMeetingMinutes (Long meetingRecordId);

    /**
     * @return com.jfzt.meeting.common.Result<java.lang.Object>
     * @Description 保存会议纪要
     * @Param [meetingMinutes]
     */
    Result<Object> saveOrUpdateMinutes (MeetingMinutes meetingMinutes);
}
