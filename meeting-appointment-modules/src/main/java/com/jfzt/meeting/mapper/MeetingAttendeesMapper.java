package com.jfzt.meeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jfzt.meeting.entity.MeetingAttendees;
import org.apache.ibatis.annotations.Mapper;

/**
 * 针对表【meeting_attendees(参会人员表)】的数据库操作Mapper
 * @author zilong.deng
 * @since 2024-06-24 15:36:27
 */
@Mapper
public interface MeetingAttendeesMapper extends BaseMapper<MeetingAttendees> {

}




