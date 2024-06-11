package com.jfzt.meeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jfzt.meeting.entity.MeetingAttendees;
import org.apache.ibatis.annotations.Mapper;

/**
 * 针对表【meeting_attendees(参会人员表)】的数据库操作Mapper
 * @author zilong.deng
 * @since  2024-04-28 11:30:57
 */
@Mapper
public interface MeetingAttendeesMapper extends BaseMapper<MeetingAttendees> {

}




