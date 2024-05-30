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
     * @Description 保存会议纪要
     * @Param [meetingMinutes]
     * @return com.jfzt.meeting.common.Result<java.lang.Object>
     */
    Result<Object> saveOrUpdateMinutes(MeetingMinutes meetingMinutes);
}
