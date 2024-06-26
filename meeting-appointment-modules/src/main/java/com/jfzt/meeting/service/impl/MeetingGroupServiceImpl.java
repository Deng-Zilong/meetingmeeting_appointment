package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingGroup;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.UserGroup;
import com.jfzt.meeting.entity.dto.MeetingGroupDTO;
import com.jfzt.meeting.entity.vo.MeetingGroupVO;
import com.jfzt.meeting.entity.vo.SysUserVO;
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

import java.util.List;

import static com.jfzt.meeting.constant.MessageConstant.*;

/**
 * 针对表【meeting_group(群组表)】的数据库操作Service实现
 * @author zilong.deng
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
     * 群组查询
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param userId 用户id
     * @return 群组列表
     */
    @Override
    public Result<List<MeetingGroupVO>> checkGroup (Integer pageNum, Integer pageSize, String userId, String groupName) {
        SysUserVO userVO = new SysUserVO();
        // 用户参加过的所有群组集合
        // 判断userId是否为空
        if (StringUtils.isBlank(userId)) {
            return Result.fail(ErrorCodeEnum.SERVICE_ERROR_A0312);
        }
        // 获取当前登录用户信息
        SysUser user = sysUserService.lambdaQuery()
                .select(SysUser::getUserId, SysUser::getUserName)
                .eq(StringUtils.isNotBlank(userId), SysUser::getUserId, userId)
                .one();
        BeanUtils.copyProperties(user, userVO);

        // 查询当前用户参与过哪些群组
        List<UserGroup> groups = userGroupService.lambdaQuery().eq(UserGroup::getUserId, userId).list();
        List<Long> idList = groups.stream().map(UserGroup::getGroupId).toList();
        if (idList.isEmpty()) {
            return Result.success();
        }
        // 查询当前用户参与的所有群组
        Page<MeetingGroup> meetingGroupPage = this.baseMapper
                .selectPage(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<MeetingGroup>()
                .like(StringUtils.isNotBlank(groupName), MeetingGroup::getGroupName, groupName)
                .in(MeetingGroup::getId, idList)
                .orderByDesc(MeetingGroup::getGmtCreate));
        List<MeetingGroup> joinList = meetingGroupPage.getRecords();

        //遍历所参与的所有群组
        List<MeetingGroupVO> collectVO = joinList.stream().map((meetingGroup) -> {
            MeetingGroupVO meetingGroupVO = new MeetingGroupVO();
            BeanUtils.copyProperties(meetingGroup, meetingGroupVO);
            // 获取创建人信息,将其姓名填入表单
            SysUser creator = sysUserService.lambdaQuery()
                    .select(SysUser::getUserName)
                    .eq(SysUser::getUserId, meetingGroup.getCreatedBy())
                    .one();
            meetingGroupVO.setUserName(creator.getUserName());

            // 查询当前群组的所有成员
            List<UserGroup> userGroups = userGroupService.lambdaQuery()
                    .select(UserGroup::getUserId)
                    .eq(meetingGroup.getId() != null, UserGroup::getGroupId, meetingGroup.getId())
                    .orderByAsc(UserGroup::getGmtCreate)
                    .list();

            userGroups.stream().peek((userGroup) -> {
                SysUserVO sysUserVO = new SysUserVO();
                if (StringUtils.isBlank(userGroup.getUserId())) {
                    return;
                }
                // 获取具体成员信息
                SysUser sysUser = sysUserService.lambdaQuery()
                        .eq(StringUtils.isNotBlank(userGroup.getUserId()), SysUser::getUserId, userGroup.getUserId())
                        .one();
                BeanUtils.copyProperties(sysUser, sysUserVO);
                meetingGroupVO.getUsers().add(sysUserVO);
            }).toList();
            return meetingGroupVO;
        }).distinct().toList();

        return Result.success(collectVO);
    }


    /**
     * 群组添加
     * @param meetingGroupDTO 群组DTO
     * @return 添加结果
     */
    @Override
    @Transactional
    public Result<Object> addMeetingGroup (MeetingGroupDTO meetingGroupDTO) {

        meetingGroupDTO.setGroupName(meetingGroupDTO.getGroupName().replaceAll(" ", ""));

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
                .eq(meetingGroupDTO.getGroupName() != null,
                        MeetingGroup::getGroupName, meetingGroupDTO.getGroupName())
                .count();
        if (sameName <= 0) {
            // 保存meetingGroup
            save(meetingGroup);
        } else {
            log.error(SAME_NAME + EXCEPTION_TYPE, RRException.class);
            throw new RRException(SAME_NAME, ErrorCodeEnum.SERVICE_ERROR_A0421.getCode());
        }
        // 根据meetingGroupDTO中的groupName查询MeetingGroup表，获取groupId
        Long groupId = lambdaQuery()
                .eq(StringUtils.isNotBlank(meetingGroupDTO.getGroupName()),
                        MeetingGroup::getGroupName, meetingGroupDTO.getGroupName())
                .one()
                .getId();
        // 获取meetingGroupDTO中的用户列表
        List<SysUser> userList = meetingGroupDTO.getUsers();

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
     * 群组修改
     * @param meetingGroupDTO 群组DTO
     * @return 修改结果
     */
    @Override
    public Result<Object> updateMeetingGroup (MeetingGroupDTO meetingGroupDTO) {
        //查询修改之前的群组
        MeetingGroup beforeGroup = lambdaQuery()
                .eq(meetingGroupDTO.getId() != null, MeetingGroup::getId, meetingGroupDTO.getId())
                .one();
        if (beforeGroup == null) {
            log.info(GROUP_NOT_EXIST + EXCEPTION_TYPE, RRException.class);
            throw new RRException(GROUP_NOT_EXIST, ErrorCodeEnum.SERVICE_ERROR_A0400.getCode());
        }
        // 创建一个新的MeetingGroup对象
        MeetingGroup meetingGroup = new MeetingGroup();
        // 将meetingGroupDTO中的属性复制到meetingGroup中
        BeanUtils.copyProperties(meetingGroupDTO, meetingGroup);
        if (StringUtils.isNotBlank(meetingGroupDTO.getGroupName())) {
            meetingGroupDTO.setGroupName(meetingGroupDTO.getGroupName().replaceAll(" ", ""));
            Long count = lambdaQuery()
                    .eq(MeetingGroup::getGroupName, meetingGroupDTO.getGroupName())
                    .count();
            if (count > 0) {
                log.info(SAME_NAME + EXCEPTION_TYPE, RRException.class);
                throw new RRException(SAME_NAME, ErrorCodeEnum.SERVICE_ERROR_A0400.getCode());
            }
            updateById(meetingGroup);
        }else {
            return Result.fail(GROUP_NAME_NOT_NULL);
        }
        if (!meetingGroupDTO.getUsers().isEmpty()) {
            List<UserGroup> userGroups = userGroupService.lambdaQuery()
                    .select(UserGroup::getId)
                    .eq(UserGroup::getGroupId, meetingGroupDTO.getId())
                    .list();
            userGroupService.removeBatchByIds(userGroups);
            meetingGroupDTO.getUsers().stream().map((member) -> {
                UserGroup group = UserGroup.builder()
                        .groupId(meetingGroupDTO.getId())
                        .userId(member.getUserId())
                        .build();
                userGroupService.save(group);
                return group;
            }).toList();
            return Result.success(UPDATE_SUCCESS);
        }
        return Result.success(UPDATE_SUCCESS);
    }

    /**
     * 群组删除
     * @param id 群组id
     * @return 删除结果
     */
    @Override
    @Transactional
    public Result<Object> deleteMeetingGroup (Long id) {
        // 查询出MeetingGroup对象之前的UserGroup对象列表
        List<UserGroup> userGroupList = userGroupService.lambdaQuery()
                .select(UserGroup::getId)
                .eq(UserGroup::getGroupId, id)
                .list();
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




