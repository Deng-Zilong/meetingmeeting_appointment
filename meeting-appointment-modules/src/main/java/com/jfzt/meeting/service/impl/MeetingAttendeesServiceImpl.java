package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.entity.MeetingAttendees;
import com.jfzt.meeting.mapper.MeetingAttendeesMapper;
import com.jfzt.meeting.service.MeetingAttendeesService;
import org.springframework.stereotype.Service;

/**
 * @author zilong.deng
 * @description 针对表【meeting_attendees(参会人员表)】的数据库操作Service实现
 * @since 2024-04-28 11:30:57
 */
@Service
public class MeetingAttendeesServiceImpl extends ServiceImpl<MeetingAttendeesMapper, MeetingAttendees>
        implements MeetingAttendeesService {

}




