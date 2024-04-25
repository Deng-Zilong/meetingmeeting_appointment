package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.entity.AttendeesEntity;
import com.jfzt.meeting.mapper.AttendeesMapper;
import com.jfzt.meeting.service.AttendeesService;
import org.springframework.stereotype.Service;


@Service("attendeesService")
public class AttendeesServiceImpl extends ServiceImpl<AttendeesMapper, AttendeesEntity> implements AttendeesService {


}