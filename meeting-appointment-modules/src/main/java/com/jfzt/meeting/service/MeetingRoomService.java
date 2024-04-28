package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingRoom;
import com.jfzt.meeting.entity.vo.MeetingRoomVO;

/**
 * @author zilong.deng
 * @description 针对表【meeting_room(会议室表)】的数据库操作Service
 * @createDate 2024-04-28 11:50:45
 */
public interface MeetingRoomService extends IService<MeetingRoom> {

    /**
     * @param meetingRoomVO 会议室
     * @return {@code Boolean}
     */
    Result<String> addMeetingRoom (MeetingRoomVO meetingRoomVO);

    Boolean deleteMeetingRoom (MeetingRoom meetingRoom);
}
