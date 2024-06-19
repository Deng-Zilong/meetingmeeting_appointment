package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingMinutes;
import com.jfzt.meeting.entity.dto.MeetingMinutesDTO;
import com.jfzt.meeting.entity.dto.MeetingWordDTO;
import com.jfzt.meeting.entity.vo.MeetingMinutesVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 针对表【meeting_minutes(会议纪要)】的数据库操作Service
 * @author zilong.deng
 * @since 2024-06-04 14:09:31
 */
@Service
public interface MeetingMinutesService extends IService<MeetingMinutes> {

    /**
     * 根据会议记录id或用户id查询会议纪要
     * @param meetingMinutes 会议记录id或用户id
     * @return 会议纪要VO
     */
    List<MeetingMinutesVO> getMeetingMinutes (MeetingMinutes meetingMinutes);


    /**
     * 根据用户id 纪要id删除指定纪要
     * @param meetingMinutes userId id
     */
    void deleteMeetingMinutes (MeetingMinutes meetingMinutes);

    /**
     * 根据会议id删除所有会议纪要
     * @param meetingRecordId 会议id
     */
    void deleteMeetingMinutes (Long meetingRecordId);

    /**
     * 保存会议纪要
     * @param meetingMinutesDTO 会议纪要
     * @return 保存结果
     */
    Result<Object> saveOrUpdateMinutes (MeetingMinutesDTO meetingMinutesDTO);
}
