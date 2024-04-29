package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.entity.UserGroup;
import com.jfzt.meeting.mapper.UserGroupMapper;
import com.jfzt.meeting.service.UserGroupService;
import org.springframework.stereotype.Service;

/**
 * @author zilong.deng
 * @description 针对表【user_group(群组人员关联表)】的数据库操作Service实现
 * @createDate 2024-04-28 11:53:18
 */
@Service
public class UserGroupServiceImpl extends ServiceImpl<UserGroupMapper, UserGroup>
        implements UserGroupService {

}




