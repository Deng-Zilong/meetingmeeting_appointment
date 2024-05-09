package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.constant.MeetingRecordStatusConstant;
import com.jfzt.meeting.entity.MeetingAttendees;
import com.jfzt.meeting.entity.MeetingRecord;
import com.jfzt.meeting.entity.MeetingRoom;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.dto.MeetingRecordDTO;
import com.jfzt.meeting.entity.vo.MeetingRecordVO;
import com.jfzt.meeting.mapper.MeetingAttendeesMapper;
import com.jfzt.meeting.mapper.MeetingRecordMapper;
import com.jfzt.meeting.service.MeetingAttendeesService;
import com.jfzt.meeting.service.MeetingRecordService;
import com.jfzt.meeting.service.MeetingRoomService;
import com.jfzt.meeting.service.SysUserService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.jfzt.meeting.constant.MessageConstant.STRAT_TIME_GT_END_TiME;
import static com.jfzt.meeting.constant.MessageConstant.STRAT_TIME_LT_NOW;
import java.util.*;

import static com.jfzt.meeting.constant.MeetingRecordStatusConstant.*;

/**
 * @author zilong.deng
 * @description 针对表【meeting_record(会议记录表)】的数据库操作Service实现
 * @since 2024-04-28 11:47:39
 */
@Service
public class MeetingRecordServiceImpl extends ServiceImpl<MeetingRecordMapper, MeetingRecord>
        implements MeetingRecordService {

    @Resource
    private MeetingAttendeesService meetingAttendeesService;
    @Autowired
    private MeetingRoomService meetingRoomService;
    @Resource
    private SysUserService userService;
    @Resource
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

        recordQueryWrapper.and(wq -> wq.between(MeetingRecord::getStartTime, startOfDay, endOfDay)
                .or().between(MeetingRecord::getEndTime, startOfDay, endOfDay));
        recordQueryWrapper
                .and(wq -> wq
                        //删除的不展示
                        .eq(MeetingRecord::getIsDeleted, 0));
        //展示未取消的会议
        recordQueryWrapper
                .and(wq -> wq
                        .eq(MeetingRecord::getStatus, MeetingRecordStatusConstant.MEETING_RECORD_STATUS_NOT_START)
                        .or().eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_PROCESSING)
                        .or().eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_END));
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

                List<SysUser> userList = userService
                        .list(new LambdaQueryWrapper<SysUser>()
                                .eq(SysUser::getUserId, attendee.getUserId()));
                if (userList.isEmpty()) {
                    return;
                }
                SysUser user = userList.getFirst();

                attendees.append(user.getUserName());
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
                List<MeetingRoom> meetingRoomList = meetingRoomService.
                        list(new LambdaQueryWrapper<MeetingRoom>().eq(MeetingRoom::getId, meetingRecord.getMeetingRoomId()));
                if (meetingRoomList != null) {
                    MeetingRoom meetingRoom = meetingRoomList.getFirst();
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
        }).filter(Objects::nonNull).filter(meetingRecordVO -> meetingRecordVO.getId() != null).toList();

    }

    private StringBuffer getStringBuffer (List<String> userIds) {
        HashSet<String> userIdSet = new HashSet<>(userIds);
        StringBuffer attendees = new StringBuffer();
        userIdSet.forEach(userId1 -> {
            SysUser user = userService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserId, userId1));
            if (user != null) {
                //拼接参会人姓名
                attendees.append(user.getUserName());
                if (userIds.indexOf(userId1) != userIds.size() - 1) {
                    attendees.append(",");
                }
            }
        });
        return attendees;
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
            meetingRecord.setStatus(MEETING_RECORD_STATUS_PROCESSING);
            this.updateById(meetingRecord);
        } else if (meetingRecord.getEndTime().isBefore(LocalDateTime.now())) {
            //会议已结束
            meetingRecord.setStatus(MEETING_RECORD_STATUS_END);
            this.updateById(meetingRecord);
        }
    }

    /**
     * 更新今日所有会议状态
     */
    @Override
    public void updateTodayRecordStatus () {
        LambdaQueryWrapper<MeetingRecord> recordQueryWrapper = new LambdaQueryWrapper<>();
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 获取当天开始时间（00:00:00）
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        // 获取当天结束时间（23:59:59）
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        recordQueryWrapper.between(MeetingRecord::getStartTime, startOfDay, endOfDay)
                .or().between(MeetingRecord::getEndTime, startOfDay, endOfDay);
        recordQueryWrapper.and(queryWrapper -> queryWrapper.eq(MeetingRecord::getIsDeleted, 0));
        //展示未取消的会议
        recordQueryWrapper.and(queryWrapper -> queryWrapper.eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_NOT_START).or().eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_PROCESSING).or().eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_END));
        recordQueryWrapper.orderByDesc(MeetingRecord::getStartTime);
        //获取当天所有会议
        List<MeetingRecord> meetingRecords = this.list(recordQueryWrapper);
        //更新
        meetingRecords.forEach(this::updateRecordStatus);
    }

    /**
     * 分页获取用户参与的所有会议
     *
     * @param userId 用户id
     * @return {@code List<MeetingRecordVO>}
     */
    @Override
    public List<MeetingRecordVO> getAllRecordVoListPage (String userId, Long pageNum, Long pageSize) {

        if (pageNum == null || pageSize == null) {
            return null;
        }
        //通过参会表查询用户参与的所有会议id
        Page<MeetingAttendees> meetingAttendeesPage = attendeesMapper
                .selectPage(new Page<>(pageNum, pageSize)
                        , new LambdaQueryWrapper<MeetingAttendees>()
                                .eq(MeetingAttendees::getUserId, userId)
                                .orderByAsc(MeetingAttendees::getGmtModified));
        List<Long> recordIds = meetingAttendeesPage.getRecords().stream().map(MeetingAttendees::getMeetingRecordId).toList();

        if (recordIds.isEmpty()) {
            return null;
        }
        //遍历会议
        return recordIds.stream().map(recordId -> {
                    MeetingRecord meetingRecord = this.baseMapper.selectById(recordId);
                    if (Objects.equals(meetingRecord.getStatus(), MEETING_RECORD_STATUS_CANCEL)) {
                        return null;
                    }
                    MeetingRecordVO meetingRecordVO = new MeetingRecordVO();
                    //查询参会者id
                    List<String> userIds = attendeesMapper.selectUserIdsByRecordId(recordId);
                    StringBuffer attendees = getStringBuffer(userIds);
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
                    meetingRecordVO.setMeetingNumber(userIds.size());
                    return meetingRecordVO;
                }).filter(Objects::nonNull)
                .sorted(Comparator.comparing(MeetingRecordVO::getStartTime).reversed())
                .toList();
    }

    /**
     * 根据会议记录id取消会议
     *
     * @param userId    用户id
     * @param meetingId 会议id
     * @return {@code Boolean}
     */
    @Override
    public Boolean deleteMeetingRecord (String userId, Long meetingId) {

        //查询会议记录
        MeetingRecord meetingRecord = this.baseMapper.selectById(meetingId);
        //判断用户是否为参会人
        List<String> userIds = attendeesMapper.selectUserIdsByRecordId(meetingId);
        if (userIds.contains(userId)) {
            //删除
            meetingRecord.setIsDeleted(1);
            this.baseMapper.updateById(meetingRecord);
            return true;
        }
        return false;

    }

    /**
     * 根据会议记录id取消会议
     *
     * @param userId    用户id
     * @param meetingId 会议id
     * @return {@code Boolean}
     */
    @Override
    public Boolean cancelMeetingRecord (String userId, Long meetingId) {

        //查询会议记录
        MeetingRecord meetingRecord = this.baseMapper.selectById(meetingId);

        //会议不是未开始状态或者会议创建人不是当前用户无法取消
        if (meetingRecord.getStatus() == 0 && meetingRecord.getCreatedBy().equals(userId)) {
            //更新会议状态
            meetingRecord.setStatus(MEETING_RECORD_STATUS_CANCEL);
            this.baseMapper.updateById(meetingRecord);
            return true;
        }
        return false;
    }

    /**
     * @Description 新增会议
     * @Param [meetingRecordDTO]
     * @return com.jfzt.meeting.common.Result<java.util.Objects>
     */
    @Override
    @Transactional
    public Result<Objects> addMeeting(MeetingRecordDTO meetingRecordDTO) {
        // 创建一个新的MeetingRecord对象
        MeetingRecord meetingRecord = new MeetingRecord();
        // 将meetingRecordDTO中的属性复制到meetingRecord中
        BeanUtils.copyProperties(meetingRecordDTO, meetingRecord);
        // 判断meetingRecord的结束时间是否早于开始时间，如果是，则返回错误信息
        if (meetingRecord.getEndTime().isBefore(meetingRecord.getStartTime())) {
            return Result.fail(STRAT_TIME_GT_END_TiME);
        }else if (meetingRecord.getStartTime().isBefore(LocalDateTime.now())){
            // 判断meetingRecord的开始时间是否早于当前时间，如果是，则返回错误信息
            return Result.fail(STRAT_TIME_LT_NOW);
        }
        // 保存meetingRecord
        save(meetingRecord);
        // 创建一个MeetingAttendees的列表，并将meetingRecordDTO中的用户信息转换为MeetingAttendees对象
        List<MeetingAttendees> attendeesList = meetingRecordDTO.getUsers()
                .stream()
                .map(user -> MeetingAttendees.builder()
                        .userId(user.getUserId())
                        .meetingRecordId(meetingRecord.getId())
                        .build())
                .collect(Collectors.toList());
        // 保存MeetingAttendees列表
        meetingAttendeesService.saveBatch(attendeesList);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<List<MeetingRecordVO>> updateMeeting(MeetingRecordDTO meetingRecordDTO) {
        // 创建一个新的MeetingRecord对象
        MeetingRecord meetingRecord = new MeetingRecord();
        // 将meetingRecordDTO中的属性复制到meetingRecord中
        BeanUtils.copyProperties(meetingRecordDTO, meetingRecord);
        // 判断meetingRecord的结束时间是否早于开始时间，如果是，则返回错误信息
        if (meetingRecord.getEndTime().isBefore(meetingRecord.getStartTime())) {
            return Result.fail(STRAT_TIME_GT_END_TiME);
        }else if (meetingRecord.getStartTime().isBefore(LocalDateTime.now())){
            // 判断meetingRecord的开始时间是否早于当前时间，如果是，则返回错误信息
            return Result.fail(STRAT_TIME_LT_NOW);
        }
        // 使用meetingRecord中的数据更新数据库中的数据
        updateById(meetingRecord);
        List<MeetingAttendees> meetingAttendees = meetingAttendeesService.lambdaQuery()
                .eq(meetingRecord.getId() != null, MeetingAttendees::getMeetingRecordId, meetingRecord.getId())
                .list();
        attendeesMapper.deleteBatchIds(meetingAttendees);
        // 创建一个MeetingAttendees的列表，并将meetingRecordDTO中的用户信息转换为MeetingAttendees对象
        List<MeetingAttendees> attendeesList = meetingRecordDTO.getUsers()
                .stream()
                .map(user -> MeetingAttendees.builder()
                        .userId(user.getUserId())
                        .meetingRecordId(meetingRecord.getId())
                        .build())
                .collect(Collectors.toList());
        // 更新MeetingAttendees列表
        meetingAttendeesService.saveBatch(attendeesList);
        return Result.success();
    }


}




