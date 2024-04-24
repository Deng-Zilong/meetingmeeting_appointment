package com.meeting_appointment_back.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meeting_appointment_back.mapper.UserGroupMapper;
import org.springframework.stereotype.Service;


import com.meeting_appointment_back.entity.UserGroupEntity;
import com.meeting_appointment_back.service.UserGroupService;


@Service("userGroupService")
public class UserGroupServiceImpl extends ServiceImpl<UserGroupMapper, UserGroupEntity> implements UserGroupService {


}