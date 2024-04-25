package com.meeting_appointment_back.service.impl;

import com.meeting_appointment_back.mapper.AttendeesMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.meeting_appointment_back.entity.AttendeesEntity;
import com.meeting_appointment_back.service.AttendeesService;


@Service("attendeesService")
public class AttendeesServiceImpl extends ServiceImpl<AttendeesMapper, AttendeesEntity> implements AttendeesService {


}