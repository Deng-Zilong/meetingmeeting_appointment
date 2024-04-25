package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.entity.MeetingRoomEntity;
import com.jfzt.meeting.mapper.MeetingRoomMapper;
import com.jfzt.meeting.service.MeetingRoomService;
import org.springframework.stereotype.Service;


@Service("meetingRoomService")
public class MeetingRoomServiceImpl extends ServiceImpl<MeetingRoomMapper, MeetingRoomEntity> implements MeetingRoomService {


}