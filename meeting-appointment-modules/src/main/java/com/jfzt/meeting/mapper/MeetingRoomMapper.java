package com.jfzt.meeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jfzt.meeting.entity.MeetingRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author zilong.deng
 * @description 针对表【meeting_room(会议室表)】的数据库操作Mapper
 * @createDate 2024-04-28 11:50:45
 * @Entity com.jfzt.meeting.entity.MeetingRoom
 */
@Mapper
public interface MeetingRoomMapper extends BaseMapper<MeetingRoom> {

    boolean updateStatus(@Param("id") Long id, @Param("status") Integer status);

}




