package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.entity.MeetingGroupEntity;
import com.jfzt.meeting.mapper.MeetingGroupMapper;
import com.jfzt.meeting.service.MeetingGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("meetingGroupService")
public class MeetingGroupServiceImpl extends ServiceImpl<MeetingGroupMapper, MeetingGroupEntity> implements MeetingGroupService {

    @Autowired
    private MeetingGroupMapper meetingGroupMapper;

    @Override
    public List<MeetingGroupEntity> find () {
        List<MeetingGroupEntity> entities = meetingGroupMapper.selectList(null);
        System.out.println(entities);
        return null;
    }
}