package com.meeting_appointment_back.service.impl;

import com.meeting_appointment_back.mapper.MeetingGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.meeting_appointment_back.entity.MeetingGroupEntity;
import com.meeting_appointment_back.service.MeetingGroupService;

import java.util.List;


@Service("meetingGroupService")
public class MeetingGroupServiceImpl extends ServiceImpl<MeetingGroupMapper, MeetingGroupEntity> implements MeetingGroupService {

    @Autowired
    private MeetingGroupMapper meetingGroupMapper;
    @Override
    public List<MeetingGroupEntity> find() {
        List<MeetingGroupEntity> entities = meetingGroupMapper.selectList(null);
        System.out.println(entities);
        return null;
    }
}