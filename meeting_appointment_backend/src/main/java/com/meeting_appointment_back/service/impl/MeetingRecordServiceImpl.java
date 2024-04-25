package com.meeting_appointment_back.service.impl;

import com.meeting_appointment_back.mapper.MeetingRecordMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.meeting_appointment_back.entity.MeetingRecordEntity;
import com.meeting_appointment_back.service.MeetingRecordService;


@Service("meetingRecordService")
public class MeetingRecordServiceImpl extends ServiceImpl<MeetingRecordMapper, MeetingRecordEntity> implements MeetingRecordService {

}