package com.jfzt.meeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jfzt.meeting.entity.MeetingAttendees;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zilong.deng
 * @description 针对表【meeting_attendees(参会人员表)】的数据库操作Mapper
 * @createDate 2024-04-28 11:30:57
 */
@Mapper
public interface MeetingAttendeesMapper extends BaseMapper<MeetingAttendees> {


    @Select("SELECT meeting_attendees.user_id FROM meeting_attendees WHERE meeting_record_id = #{recordId} order by meeting_attendees.gmt_create desc")
    List<String> selectUserIdsByRecordId (Long recordId);

    @Select("SELECT meeting_attendees.meeting_record_id FROM meeting_attendees WHERE meeting_attendees.user_id = #{userId} order by meeting_attendees.gmt_create desc")
    List<Long> selectRecordIdsByUserId (String userId);
}




