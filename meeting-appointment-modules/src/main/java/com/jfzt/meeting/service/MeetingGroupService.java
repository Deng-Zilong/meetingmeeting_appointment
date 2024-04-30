package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingGroup;
import com.jfzt.meeting.entity.dto.MeetingGroupDTO;
import com.jfzt.meeting.entity.vo.MeetingGroupVO;

import java.util.List;

/**
 * @author zilong.deng
 * @description 针对表【meeting_group(群组表)】的数据库操作Service
 * @since 2024-04-28 11:33:49
 */
public interface MeetingGroupService extends IService<MeetingGroup> {


    Result<List<MeetingGroupVO>> checkGroup(String userId);

    Result<Object> addMeetingGroup(MeetingGroupDTO meetingGroupDTO);
}
