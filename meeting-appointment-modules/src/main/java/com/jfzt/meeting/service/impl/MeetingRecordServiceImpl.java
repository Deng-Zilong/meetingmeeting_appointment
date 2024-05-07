package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.entity.MeetingAttendees;
import com.jfzt.meeting.entity.MeetingRecord;
import com.jfzt.meeting.entity.MeetingRoom;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.vo.MeetingRecordVO;
import com.jfzt.meeting.mapper.MeetingAttendeesMapper;
import com.jfzt.meeting.mapper.MeetingRecordMapper;
import com.jfzt.meeting.service.MeetingAttendeesService;
import com.jfzt.meeting.service.MeetingRecordService;
import com.jfzt.meeting.service.MeetingRoomService;
import com.jfzt.meeting.service.SysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zilong.deng
 * @description 针对表【meeting_record(会议记录表)】的数据库操作Service实现
 * @since 2024-04-28 11:47:39
 */
@Service
public class MeetingRecordServiceImpl extends ServiceImpl<MeetingRecordMapper, MeetingRecord>
        implements MeetingRecordService {

    @Autowired
    private MeetingAttendeesService meetingAttendeesService;
    private MeetingRoomService meetingRoomService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private MeetingAttendeesMapper attendeesMapper;


    @Autowired
    public void setMeetingRoomService (MeetingRoomService meetingRoomService) {
        this.meetingRoomService = meetingRoomService;
    }

    /**
     * 获取当天用户参与的所有会议
     *
     * @param userId 用户id
     * @return {@code List<MeetingRecordVO>}
     */
    @Override
    public List<MeetingRecordVO> getRecordVoList (String userId) {
        LambdaQueryWrapper<MeetingRecord> recordQueryWrapper = new LambdaQueryWrapper<>();
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 获取当天开始时间（00:00:00）
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        // 获取当天结束时间（23:59:59）
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        recordQueryWrapper.between(MeetingRecord::getStartTime, startOfDay, endOfDay)
                .or().between(MeetingRecord::getEndTime, startOfDay, endOfDay);
        recordQueryWrapper.eq(MeetingRecord::getIsDeleted, 0);
        //展示未取消的会议
        recordQueryWrapper.eq(MeetingRecord::getStatus, 0).or().eq(MeetingRecord::getStatus, 1).or().eq(MeetingRecord::getStatus, 2);
        recordQueryWrapper.orderByDesc(MeetingRecord::getStartTime);
        //获取当天所有会议
        List<MeetingRecord> meetingRecords = this.list(recordQueryWrapper);
        if (meetingRecords.isEmpty()) {
            return new ArrayList<>();
        }
        //获取当前用户所在的会议
        return meetingRecords.stream().map(meetingRecord -> {

            MeetingRecordVO meetingRecordVO = new MeetingRecordVO();
            //通过会议id找到参会人列表
            List<MeetingAttendees> attendeesList = meetingAttendeesService.list(
                    new LambdaQueryWrapper<MeetingAttendees>()
                            .eq(MeetingAttendees::getMeetingRecordId, meetingRecord.getId()));
            if (attendeesList.isEmpty()) {
                //没有匹配的参会人，返回空对象，最后过滤
                return null;
            }
            List<String> userIdList = new ArrayList<>();
            StringBuffer attendees = new StringBuffer();
            //遍历参会人，拼接姓名，获取userIdList
            attendeesList.forEach(attendee -> {
                userIdList.add(attendee.getUserId());
                SysUser first = userService.lambdaQuery()
                        .eq(SysUser::getUserId, attendee.getUserId())
                        .list()
                        .getFirst();
                attendees.append(first.getUserName());
                if (attendeesList.indexOf(attendee) != attendeesList.size() - 1) {
                    attendees.append(",");
                }
            });

            if (userIdList.contains(userId)) {
                //更新会议状态

                updateRecordStatus(meetingRecord);
                //插入会议信息
                BeanUtils.copyProperties(meetingRecord, meetingRecordVO);
                //插入会议室信息
                MeetingRoom meetingRoom = meetingRoomService.getOne(new LambdaQueryWrapper<MeetingRoom>().eq(MeetingRoom::getId, meetingRecord.getMeetingRoomId()));
                if (meetingRoom != null) {
                    meetingRecordVO.setMeetingRoomName(meetingRoom.getRoomName());
                    meetingRecordVO.setLocation(meetingRoom.getLocation());
                }
                //插入会议创建人信息
                SysUser user = userService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserId, meetingRecord.getCreatedBy()));
                if (user != null) {
                    meetingRecordVO.setAdminUserName(user.getUserName());
                }
                //插入参会人信息
                meetingRecordVO.setAttendees(String.valueOf(attendees));
                meetingRecordVO.setMeetingNumber(userIdList.size());
            }
            return meetingRecordVO;
        }).filter(Objects::nonNull).toList();

    }

    /**
     * 获取用户参与的所有会议
     *
     * @param userId 用户id
     * @return {@code List<MeetingRecordVO>}
     */
    @Override
    public List<MeetingRecordVO> getAllRecordVoList (String userId) {
        //通过参会表查询用户参与的所有会议id
        List<Long> recordIds = attendeesMapper.selectRecordIdsByUserId(userId);
        if (recordIds.isEmpty()) {
            return null;
        }
        //遍历会议
        return recordIds.stream().map(recordId -> {

            MeetingRecord meetingRecord = this.baseMapper.selectById(recordId);
            MeetingRecordVO meetingRecordVO = new MeetingRecordVO();
            //查询参会者id
            List<String> userIds = attendeesMapper.selectUserIdsByRecordId(recordId);
            StringBuffer attendees = new StringBuffer();
            userIds.forEach(userId1 -> {
                SysUser user = userService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserId, userId1));
                //拼接参会人姓名
                if (user != null) {
                    SysUser first = userService.lambdaQuery()
                            .eq(SysUser::getUserId, userId)
                            .list()
                            .getFirst();
                    attendees.append(first.getUserName());
                    if (userIds.indexOf(userId) != userIds.size() - 1) {
                        attendees.append(",");
                    }
                }
            });
            //更新会议状态
            updateRecordStatus(meetingRecord);
            //插入会议信息
            BeanUtils.copyProperties(meetingRecord, meetingRecordVO);
            //插入会议室信息
            MeetingRoom meetingRoom = meetingRoomService.getOne(new LambdaQueryWrapper<MeetingRoom>().eq(MeetingRoom::getId, meetingRecord.getMeetingRoomId()));
            if (meetingRoom != null) {
                meetingRecordVO.setMeetingRoomName(meetingRoom.getRoomName());
                meetingRecordVO.setLocation(meetingRoom.getLocation());
            }
            //插入会议创建人信息
            List<SysUser> userList = userService.list(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserId, meetingRecord.getCreatedBy()));
            if (userList != null) {
                SysUser first = userList.getFirst();
                meetingRecordVO.setAdminUserName(first.getUserName());
            }
            //插入参会人信息
            meetingRecordVO.setAttendees(String.valueOf(attendees));
            meetingRecordVO.setMeetingNumber(userIds.size());
            return meetingRecordVO;
        }).toList();

    }

    /**
     * 查询今日中心会议总次数
     *
     * @return {@code Integer}
     */
    @Override
    public Integer getRecordNumber () {

        return Math.toIntExact(this.baseMapper.selectCount(
                new LambdaQueryWrapper<MeetingRecord>()
                        .between(MeetingRecord::getStartTime, LocalDateTime.now().toLocalDate().atStartOfDay()
                                , LocalDateTime.now().toLocalDate().atTime(23, 59, 59))));
    }


    /**
     * 更新会议状态
     *
     * @param meetingRecord 会议记录
     */
    @Override
    public void updateRecordStatus (MeetingRecord meetingRecord) {
        if (meetingRecord.getStartTime().isBefore(LocalDateTime.now()) && meetingRecord.getEndTime().isAfter(LocalDateTime.now())) {
            //会议进行中
            meetingRecord.setStatus(1);
            this.updateById(meetingRecord);
        } else if (meetingRecord.getEndTime().isBefore(LocalDateTime.now())) {
            //会议已结束
            meetingRecord.setStatus(2);
            this.updateById(meetingRecord);
        }
    }


}




