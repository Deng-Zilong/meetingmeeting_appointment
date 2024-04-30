package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingGroup;
import com.jfzt.meeting.entity.SysDepartmentUser;
import com.jfzt.meeting.entity.vo.MeetingGroupVO;
import com.jfzt.meeting.mapper.MeetingGroupMapper;
import com.jfzt.meeting.service.MeetingGroupService;

import com.jfzt.meeting.service.SysDepartmentUserService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zilong.deng
 * @description 针对表【meeting_group(群组表)】的数据库操作Service实现
 * @since 2024-04-28 11:33:49
 */
@Service
public class MeetingGroupServiceImpl extends ServiceImpl<MeetingGroupMapper, MeetingGroup>
        implements MeetingGroupService {
    @Resource
    private SysDepartmentUserService sysDepartmentUserService;

    @Override
    public Result<List<MeetingGroupVO>> checkGroup(String userId) {
        userId = "d";

        SysDepartmentUser user = sysDepartmentUserService.lambdaQuery()
                .eq(StringUtils.isNotBlank(userId), SysDepartmentUser::getUserId, userId)
                .list()
                .getFirst();

        List<MeetingGroup> groupList = lambdaQuery().list();
        List<MeetingGroupVO> collect = groupList.stream().map((item) -> {
            MeetingGroupVO meetingGroupVO = new MeetingGroupVO();
            BeanUtils.copyProperties(item, meetingGroupVO);
            meetingGroupVO.setUserName(user.getUserName());
            return meetingGroupVO;

        }).toList();
        return Result.success(collect);
    }
}




