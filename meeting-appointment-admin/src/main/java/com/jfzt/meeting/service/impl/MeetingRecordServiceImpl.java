package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.entity.MeetingRecordEntity;
import com.jfzt.meeting.mapper.MeetingRecordMapper;
import com.jfzt.meeting.service.MeetingRecordService;
import org.springframework.stereotype.Service;


@Service("meetingRecordService")
public class MeetingRecordServiceImpl extends ServiceImpl<MeetingRecordMapper, MeetingRecordEntity> implements MeetingRecordService {

}