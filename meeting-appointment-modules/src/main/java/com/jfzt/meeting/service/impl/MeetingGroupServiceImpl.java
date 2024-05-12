package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.context.BaseContext;
import com.jfzt.meeting.entity.MeetingGroup;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.UserGroup;
import com.jfzt.meeting.entity.dto.MeetingGroupDTO;
import com.jfzt.meeting.entity.vo.MeetingGroupVO;
import com.jfzt.meeting.exception.ErrorCodeEnum;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.mapper.MeetingGroupMapper;
import com.jfzt.meeting.service.MeetingGroupService;
import com.jfzt.meeting.service.SysUserService;
import com.jfzt.meeting.service.UserGroupService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.jfzt.meeting.constant.MessageConstant.*;

/**
 * @author zilong.deng
 * @description 针对表【meeting_group(群组表)】的数据库操作Service实现
 * @since 2024-04-28 11:33:49
 */
@Service
@Slf4j
public class MeetingGroupServiceImpl extends ServiceImpl<MeetingGroupMapper, MeetingGroup>
        implements MeetingGroupService {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private UserGroupService userGroupService;


    /**
     * @return com.jfzt.meeting.common.Result<java.util.List < com.jfzt.meeting.entity.vo.MeetingGroupVO>>
     * @Description 群组查询
     * @Param [userId]
     */
    @Override
    public Result<List<MeetingGroupVO>> checkGroup (Integer pageNum, Integer pageSize) {
        String userId = BaseContext.getCurrentId();
        // 移除当前的ID
        BaseContext.removeCurrentId();
        // 返回Result.success(collect)
        ArrayList<MeetingGroup> joinList = new ArrayList<>();
        // 查询创建人姓名
        SysUser user = sysUserService.lambdaQuery()
                .eq(StringUtils.isNotBlank(userId), SysUser::getUserId, userId)
                .one();

        // 定义一个UserGroup类型的List，通过userGroupService的lambdaQuery方法获取满足条件的UserGroup列表
        List<UserGroup> userGroupList = userGroupService.lambdaQuery()
                .eq(StringUtils.isNotBlank(userId), UserGroup::getUserId, userId)
                .list();
        // 定义一个List，用于存放满足条件的MeetingGroup
        userGroupList.stream().peek((item) -> {
            // 通过lambdaQuery方法获取满足条件的MeetingGroup
            MeetingGroup joinUser = lambdaQuery()
                    .eq(item.getGroupId() != null, MeetingGroup::getId, item.getGroupId())
                    .one();
            // 将满足条件的MeetingGroup添加到endList中
            joinList.add(joinUser);
        }).toList();


        // 定义一个List，用于存放满足条件的MeetingGroupVO
        List<MeetingGroupVO> collectVO = joinList.stream().map((item) -> {
            // 创建一个MeetingGroupVO实例
            MeetingGroupVO meetingGroupVO = new MeetingGroupVO();
            // 使用BeanUtils.copyProperties方法将item复制到meetingGroupVO实例中
            BeanUtils.copyProperties(item, meetingGroupVO);
            // 将user的userName添加到meetingGroupVO中
            SysUser one = sysUserService.getOne(
                    new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getUserId, item.getCreatedBy()));
            meetingGroupVO.setUserName(one.getUserName());
            // 将会议创建人添加到参会人员
            meetingGroupVO.getUsers().add(user.getUserName());
            // 使用lambdaQuery查询出所有UserGroup，其中groupId为item.getId()
            List<UserGroup> userGroups = userGroupService.lambdaQuery()
                    .eq(item.getId() != null, UserGroup::getGroupId, item.getId())
                    .orderByAsc(UserGroup::getGmtModified)
                    .list();
            // 遍历userGroups，将每个userGroup的用户名添加到meetingGroupVO中
            userGroups.stream().peek((userGroup) -> {
                // 使用lambdaQuery查询出SysDepartmentUser，其中用户ID为userGroup.getUserId()
                SysUser sysUser = sysUserService.lambdaQuery()
                        .eq(StringUtils.isNotBlank(userGroup.getUserId()), SysUser::getUserId, userGroup.getUserId())
                        .one();
                // 将sysDepartmentUser的用户名添加到meetingGroupVO中
                meetingGroupVO.getUsers().add(sysUser.getUserName());

            }).toList();
            // 返回meetingGroupVO实例
            return meetingGroupVO;

        }).toList();

        return Result.success(collectVO);
    }


    /**
     * @return com.jfzt.meeting.common.Result<java.lang.Object>
     * @Description 群组添加
     */
    @Override
    @Transactional
    public Result<Object> addMeetingGroup (MeetingGroupDTO meetingGroupDTO) {

        if (meetingGroupDTO.getUsers().isEmpty()) {
            log.error(NO_USER + EXCEPTION_TYPE, RRException.class);
            throw new RRException(NO_USER, ErrorCodeEnum.SERVICE_ERROR_A0410.getCode());
        }
        // 创建一个新的MeetingGroup对象
        MeetingGroup meetingGroup = new MeetingGroup();

        // 将meetingGroupDTO中的属性复制到meetingGroup中
        BeanUtils.copyProperties(meetingGroupDTO, meetingGroup);
        // 使用lambdaQuery查询 MeetingGroup 表中 GroupName 字段等于 meetingGroupDTO 中 getGroupName() 字段的数量
        Long sameName = lambdaQuery()
                .eq(meetingGroupDTO.getGroupName() != null
                        , MeetingGroup::getGroupName, meetingGroupDTO.getGroupName())
                .count();
        if (sameName > 0) {
            // 保存meetingGroup
            save(meetingGroup);
        } else {
            log.error(SAME_NAME + EXCEPTION_TYPE, RRException.class);
            throw new RRException(SAME_NAME, ErrorCodeEnum.SERVICE_ERROR_A0421.getCode());
        }
        // 根据meetingGroupDTO中的groupName查询MeetingGroup表，获取groupId
        Long groupId = lambdaQuery()
                .eq(StringUtils.isNotBlank(meetingGroupDTO.getGroupName()), MeetingGroup::getGroupName, meetingGroupDTO.getGroupName())
                .one()
                .getId();

        // 获取meetingGroupDTO中的用户列表
        List<UserGroup> userList = meetingGroupDTO.getUsers();

        // 对用户列表进行处理，将每个用户关联到meetingGroup中
        userList.stream().peek((item) -> {
            // 创建一个新的UserGroup对象
            UserGroup group = UserGroup.builder()
                    .userId(item.getUserId())
                    .groupId(groupId)
                    .build();
            // 保存UserGroup
            userGroupService.save(group);
        }).toList();
        UserGroup group = UserGroup.builder()
                .userId(meetingGroupDTO.getCreatedBy())
                .groupId(groupId)
                .build();
        // 保存UserGroup
        userGroupService.save(group);


        return Result.success(ADD_SUCCESS);
    }

    /**
     * @return com.jfzt.meeting.common.Result<java.lang.Object>
     * @Description 群组修改
     * @Param [meetingGroupDTO]
     */
    @Override
    @Transactional
    public Result<Object> updateMeetingGroup (MeetingGroupDTO meetingGroupDTO) {

        MeetingGroup beforeGroup = lambdaQuery()
                .eq(meetingGroupDTO.getId() != null, MeetingGroup::getId, meetingGroupDTO.getId())
                .one();
        if (beforeGroup == null) {
            log.info(GROUP_NOT_EXIST + EXCEPTION_TYPE, RRException.class);
            throw new RRException(GROUP_NOT_EXIST, ErrorCodeEnum.SERVICE_ERROR_A0400.getCode());
        }
        // 根据meetingGroupDTO中的groupName查询MeetingGroup表，获取groupId
        MeetingGroup one = lambdaQuery()
                .eq(StringUtils.isNotBlank(beforeGroup.getGroupName()), MeetingGroup::getGroupName, beforeGroup.getGroupName())
                .one();
        // 创建一个新的MeetingGroup对象
        MeetingGroup meetingGroup = new MeetingGroup();
        // 将meetingGroupDTO中的属性复制到meetingGroup中
        BeanUtils.copyProperties(meetingGroupDTO, meetingGroup);
        // 保存meetingGroup
        updateById(meetingGroup);
        // 获取meetingGroupDTO中的用户列表
        List<UserGroup> userList = meetingGroupDTO.getUsers();
        List<UserGroup> beforeList = userGroupService.lambdaQuery().eq(UserGroup::getGroupId, meetingGroupDTO.getId()).list();
        userGroupService.removeBatchByIds(beforeList);
        // 对用户列表进行处理，将每个用户关联到meetingGroup中
        userList.stream().peek((item) -> {
            // 创建一个新的UserGroup对象
            UserGroup group = UserGroup.builder()
                    .userId(item.getUserId())
                    .groupId(one.getId())
                    .build();
            // 保存UserGroup
            userGroupService.save(group);
        }).toList();

        return Result.success(UPDATE_SUCCESS);
    }

    /**
     * @return com.jfzt.meeting.common.Result<java.lang.Object>
     * @throws
     * @Description 群组删除
     * @Param [meetingGroupDTO]
     */
    @Override
    @Transactional
    public Result<Object> deleteMeetingGroup (Long id) {
        // 查询出MeetingGroup对象之前的UserGroup对象列表
        List<UserGroup> userGroupList = userGroupService.lambdaQuery().eq(UserGroup::getGroupId, id).list();
        if (userGroupList == null) {
            log.info(DELETE_FAIL + EXCEPTION_TYPE, RRException.class);
            throw new RRException(DELETE_FAIL, ErrorCodeEnum.SERVICE_ERROR_C0111.getCode());
        }
        // 删除MeetingGroup对象
        removeById(id);
        // 按照beforeList中的id删除UserGroup对象
        userGroupService.removeBatchByIds(userGroupList);
        return Result.success(DELETE_SUCCESS);
    }
}




