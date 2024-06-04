package com.jfzt.meeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jfzt.meeting.entity.MeetingRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author zilong.deng
 * @description 针对表【meeting_room(会议室表)】的数据库操作Mapper
 * @createDate 2024-04-28 11:50:45
 * @Entity com.jfzt.meeting.entity.MeetingRoom
 */
@Mapper
public interface MeetingRoomMapper extends BaseMapper<MeetingRoom> {

    /**
     * 更改会议室状态
     * @param id 会议室id
     * @param status 会议室状态（0暂停使用,1可使用/空闲 2为使用中不保存至数据库，实时获取）
     * @return java.lang.Integer
     */
    Integer updateStatus (@Param("id") Long id, @Param("status") Integer status);

    /**
     * 查询会议室信息，包括被逻辑删除的会议室
     * @param roomId 会议室id
     * @return 会议室
     **/
    @Select("select * from meeting_room where id = #{roomId}")
    MeetingRoom getByRoomId (Long roomId);

}




