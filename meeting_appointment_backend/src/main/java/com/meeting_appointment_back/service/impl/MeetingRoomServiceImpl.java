package com.meeting_appointment_back.service.impl;

import com.meeting_appointment_back.mapper.MeetingRoomMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.meeting_appointment_back.entity.MeetingRoomEntity;
import com.meeting_appointment_back.service.MeetingRoomService;


@Service("meetingRoomService")
public class MeetingRoomServiceImpl extends ServiceImpl<MeetingRoomMapper, MeetingRoomEntity> implements MeetingRoomService {


}