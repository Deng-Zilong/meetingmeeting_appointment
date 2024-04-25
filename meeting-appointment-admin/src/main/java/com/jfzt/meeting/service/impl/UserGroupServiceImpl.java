package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.entity.UserGroupEntity;
import com.jfzt.meeting.mapper.UserGroupMapper;
import com.jfzt.meeting.service.UserGroupService;
import org.springframework.stereotype.Service;


@Service("userGroupService")
public class UserGroupServiceImpl extends ServiceImpl<UserGroupMapper, UserGroupEntity> implements UserGroupService {


}