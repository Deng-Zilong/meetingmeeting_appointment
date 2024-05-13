package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.constant.MeetingRecordStatusConstant;
import com.jfzt.meeting.constant.MessageConstant;
import com.jfzt.meeting.context.BaseContext;
import com.jfzt.meeting.entity.MeetingRecord;
import com.jfzt.meeting.entity.MeetingRoom;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.vo.MeetingRoomStatusVO;
import com.jfzt.meeting.entity.vo.MeetingRoomVO;
import com.jfzt.meeting.entity.vo.TimePeriodStatusVO;
import com.jfzt.meeting.exception.ErrorCodeEnum;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.mapper.MeetingAttendeesMapper;
import com.jfzt.meeting.mapper.MeetingRoomMapper;
import com.jfzt.meeting.service.MeetingRecordService;
import com.jfzt.meeting.service.MeetingRoomService;
import com.jfzt.meeting.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.jfzt.meeting.constant.IsDeletedConstant.NOT_DELETED;
import static com.jfzt.meeting.constant.MeetingRecordStatusConstant.*;
import static com.jfzt.meeting.constant.MeetingRoomStatusConstant.*;
import static com.jfzt.meeting.constant.TimePeriodStatusConstant.*;
import static com.jfzt.meeting.context.BaseContext.removeCurrentLevel;

/**
 * @author zilong.deng
 * @description 针对表【meeting_room(会议室表)】的数据库操作Service实现
 * @createDate 2024-04-28 11:50:45
 */
@Slf4j
@Service
public class MeetingRoomServiceImpl extends ServiceImpl<MeetingRoomMapper, MeetingRoom>
        implements MeetingRoomService {

    private MeetingRoomMapper meetingRoomMapper;

    private MeetingRecordService meetingRecordService;

    private SysUserService userService;


    private MeetingAttendeesMapper attendeesMapper;

    @Autowired
    public void setMeetingRoomMapper (MeetingRoomMapper meetingRoomMapper) {
        this.meetingRoomMapper = meetingRoomMapper;
    }

    @Autowired
    public void setMeetingRecordService (MeetingRecordService meetingRecordService) {
        this.meetingRecordService = meetingRecordService;
    }

    @Autowired
    public void setUserService (SysUserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setAttendeesMapper (MeetingAttendeesMapper attendeesMapper) {
        this.attendeesMapper = attendeesMapper;
    }

    /**
     * @param meetingRoom 会议室对象
     * @return {@code Boolean}
     */
    @Override
    public Result<String> addMeetingRoom (MeetingRoom meetingRoom) {
        int result = meetingRoomMapper.insert(meetingRoom);
        if (result > 0) {
            return Result.success(MessageConstant.SUCCESS);
        }
        return Result.fail(MessageConstant.FAIL);
    }

    /**
     * @param meetingRoomId 会议室id
     * @return {@code Boolean}
     */
    @Override
    public Result<String> deleteMeetingRoom (Long meetingRoomId) {
        // 获取当前登录用户的权限等级
        Integer level = BaseContext.getCurrentLevel();
        removeCurrentLevel();
        if (MessageConstant.SUPER_ADMIN_LEVEL.equals(level) || MessageConstant.ADMIN_LEVEL.equals(level)) {
            // 删除会议室
            if (meetingRoomId != null) {
                int result = meetingRoomMapper.deleteById(meetingRoomId);
                if (result > 0) {
                    return Result.success();
                }
            }
            return Result.fail(ErrorCodeEnum.SERVICE_ERROR_A0400);
        }
        return Result.fail(ErrorCodeEnum.SERVICE_ERROR_A0312);
    }

    /**
     * 修改会议室状态
     *
     * @param id     会议室id
     * @param status 会议室状态
     */
    @Override
    public Result<Integer> updateStatus (Long id, Integer status) {
        // 获取当前登录用户的权限等级
        Integer level = BaseContext.getCurrentLevel();
        removeCurrentLevel();
        if (MessageConstant.SUPER_ADMIN_LEVEL.equals(level) || MessageConstant.ADMIN_LEVEL.equals(level)) {
            boolean result = meetingRoomMapper.updateStatus(id, status);
            if (result) {
                return Result.success(MessageConstant.SUCCESS);
            }
            return Result.fail(MessageConstant.FAIL);
        }
        return Result.fail(MessageConstant.HAVE_NO_AUTHORITY);
    }


    /**
     * 查询会议室状态
     *
     * @return {@code Result<List<MeetingRoomStatusVO>>}
     */
    @Override
    public List<MeetingRoomStatusVO> getMeetingRoomStatus () {
        //查询当前时间段会议室的使用信息,当前时间在开始或结束区间内的会议,填充到会议室VO,不更新会议室,状态只有01

        //查询所有会议室
        List<MeetingRoom> meetingRoomList = list(
                new LambdaQueryWrapper<MeetingRoom>()
                        .eq(MeetingRoom::getIsDeleted, NOT_DELETED)
                        .orderByAsc(MeetingRoom::getLocation));

        return meetingRoomList.stream().map(meetingRoom -> {
            MeetingRoomStatusVO meetingRoomStatusVO = new MeetingRoomStatusVO();
            //复制到VO
            BeanUtils.copyProperties(meetingRoom, meetingRoomStatusVO);
            //暂停使用直接返回
            if (Objects.equals(meetingRoomStatusVO.getStatus(), MEETINGROOM_STATUS_PAUSE)) {
                return meetingRoomStatusVO;
            }
            LambdaQueryWrapper<MeetingRecord> recordQueryWrapper = new LambdaQueryWrapper<>();
            recordQueryWrapper
                    .lt(MeetingRecord::getStartTime, LocalDateTime.now())
                    .gt(MeetingRecord::getEndTime, LocalDateTime.now())
                    .eq(MeetingRecord::getMeetingRoomId, meetingRoom.getId());
            recordQueryWrapper.and(wrapper -> wrapper.eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_NOT_START)
                    .or().eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_PROCESSING)
                    .or().eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_END));

            List<MeetingRecord> meetingRecords = meetingRecordService.list(recordQueryWrapper);
            if (meetingRecords.isEmpty()) {
                //当前没有会议
                meetingRoomStatusVO.setStatus(MEETINGROOM_STATUS_AVAILABLE);
            } else {
                MeetingRecord meetingRecord = meetingRecords.getFirst();
                //更新会议室状态
                meetingRoomStatusVO.setStatus(MEETINGROOM_STATUS_USING);
                //更新会议状态
                meetingRecordService.updateRecordStatus(meetingRecord);
                meetingRecord = meetingRecordService.getById(meetingRecord.getId());
                //有会议将会议信息返回
                meetingRoomStatusVO.setTitle(meetingRecord.getTitle());
                meetingRoomStatusVO.setDescription(meetingRecord.getDescription());
                meetingRoomStatusVO.setRecordStatus(meetingRecord.getStatus());
                meetingRoomStatusVO.setMeetingEndTime(meetingRecord.getEndTime());
                meetingRoomStatusVO.setMeetingStartTime(meetingRecord.getStartTime());
                //插入参会人拼接字符串
                List<String> userIds = attendeesMapper.selectUserIdsByRecordId(meetingRecord.getId());
                StringBuffer attendees = new StringBuffer();
                userIds.forEach(userId1 -> {
                    SysUser user = userService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserId, userId1));
                    //拼接参会人姓名
                    if (user != null) {
                        SysUser first = userService.lambdaQuery()
                                .eq(SysUser::getUserId, userId1)
                                .list()
                                .getFirst();
                        attendees.append(first.getUserName());
                        if (userIds.indexOf(userId1) != userIds.size() - 1) {
                            attendees.append(",");
                        }
                    }
                });
                meetingRoomStatusVO.setAttendees(attendees.toString());
            }
            return meetingRoomStatusVO;
        }).collect(Collectors.toList());
    }

    /**
     * 查询当天各个时间段会议室占用情况
     *
     * @return {@code List<Integer>}
     */
    @Override
    public List<Integer> isBusy () {
        List<Integer> timeStatus = new LinkedList<>();
        LocalDateTime now = LocalDateTime.now();
        //更新今日所有会议记录状态
        meetingRecordService.updateTodayRecordStatus();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime startTime = startOfDay.plusHours(8);
        LocalDateTime endTime = startTime.plusMinutes(30);
        //每段30分钟遍历时间，在当前时间之前的时间段状态为0已过期
        for (int i = 0; i < 30; i++) {
            if (now.isAfter(endTime)) {
                timeStatus.add(i, TIME_PERIOD_OVERDUE);
            } else {
                //之后的时间，判断是否有会议的开始时间或结束时间包含在里面，有的话且包含所有的会议室则为1已预订
                LambdaQueryWrapper<MeetingRecord> recordQueryWrapper = new LambdaQueryWrapper<>();
                //开始时间或结束时间包含在时间段内
                recordQueryWrapper
                        .between(MeetingRecord::getStartTime, startTime, endTime)
                        .or().between(MeetingRecord::getEndTime, startTime, endTime)
                        .or().lt(MeetingRecord::getStartTime, startTime).gt(MeetingRecord::getEndTime, endTime);
                //没有逻辑删除
                recordQueryWrapper.and(recordQueryWrapper1 -> recordQueryWrapper1.eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_NOT_START)
                        .or().eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_PROCESSING));
                List<MeetingRecord> meetingRecords = meetingRecordService.list(recordQueryWrapper);
                //根据会议室id收集获得占用不同会议室的数量
                long size = meetingRecords.stream().collect(Collectors.groupingBy(MeetingRecord::getMeetingRoomId))
                        .size();
                //获取可使用的会议室总数
                long count = this.count(new LambdaQueryWrapper<MeetingRoom>()
                        .eq(MeetingRoom::getIsDeleted, NOT_DELETED)
                        .eq(MeetingRoom::getStatus, MEETINGROOM_STATUS_AVAILABLE));
                if (count == size) {
                    System.out.println(timeStatus);
                    //相同表示时间段全被占用
                    timeStatus.add(i, TIME_PERIOD_BUSY);
                } else {
                    //没全部占用
                    timeStatus.add(i, TIME_PERIOD_AVAILABLE);
                }
            }
            startTime = startTime.plusMinutes(30);
            endTime = endTime.plusMinutes(30);
        }
        return timeStatus;
    }

    /**
     * 查询指定会议室当天各个时间段占用情况
     *
     * @param id   会议室id
     * @param date 日期
     * @return {@code Result<List<TimePeriodStatusVO>>}
     */
    @Override
    public Result<List<TimePeriodStatusVO>> isBusyByIdAndDate (Long id, LocalDate date) {
        if (id == null || date == null) {
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0400);
        }
        LocalDateTime now = LocalDateTime.now();
        List<TimePeriodStatusVO> timePeriodStatusVOList = new LinkedList<>();
        LocalDateTime startTime = LocalDateTime.of(date, LocalTime.of(8, 0));
        LocalDateTime endTime = startTime.plusMinutes(30);
        for (int i = 0; i < 30; i++) {
            TimePeriodStatusVO timePeriodStatusVO = new TimePeriodStatusVO();
            //判断是否过期
            if (now.isAfter(endTime)) {
                timePeriodStatusVO.setStartTime(startTime);
                timePeriodStatusVO.setEndTime(endTime);
                timePeriodStatusVO.setStatus(TIME_PERIOD_OVERDUE);
            } else {
                LambdaQueryWrapper<MeetingRecord> recordQueryWrapper = new LambdaQueryWrapper<>();
                recordQueryWrapper.eq(MeetingRecord::getMeetingRoomId, id);
                recordQueryWrapper.and(wrapper -> wrapper.eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_NOT_START)
                        .or().eq(MeetingRecord::getStatus, MeetingRecordStatusConstant.MEETING_RECORD_STATUS_PROCESSING));
                LocalDateTime finalStartTime = startTime;
                LocalDateTime finalEndTime = endTime;
                recordQueryWrapper
                        .and(wrapper -> wrapper
                                .between(MeetingRecord::getStartTime, finalStartTime, finalEndTime)
                                .or().between(MeetingRecord::getEndTime, finalStartTime, finalEndTime)
                                .or().lt(MeetingRecord::getStartTime, finalStartTime).gt(MeetingRecord::getEndTime, finalEndTime));
                List<MeetingRecord> meetingRecords = meetingRecordService.list(recordQueryWrapper);
                //判断是否被占用
                if (!meetingRecords.isEmpty()) {
                    MeetingRecord meetingRecord = meetingRecords.getFirst();
                    timePeriodStatusVO.setStartTime(startTime);
                    timePeriodStatusVO.setEndTime(endTime);
                    timePeriodStatusVO.setStatus(TIME_PERIOD_BUSY);
                    timePeriodStatusVO.setMeetingTitle(meetingRecord.getTitle());
                    SysUser user = userService.getOne(new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getUserId, meetingRecord.getCreatedBy()));
                    if (user != null) {
                        timePeriodStatusVO.setMeetingAdminUserName(user.getUserName());
                    }
                } else {
                    timePeriodStatusVO.setStartTime(startTime);
                    timePeriodStatusVO.setEndTime(endTime);
                    timePeriodStatusVO.setStatus(TIME_PERIOD_AVAILABLE);
                }
            }
            timePeriodStatusVOList.add(timePeriodStatusVO);

            startTime = startTime.plusMinutes(30);
            endTime = endTime.plusMinutes(30);
        }
        return Result.success(timePeriodStatusVOList);
    }

    /**
     * 根据时间段获取可用的会议室
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return {@code Result<List<MeetingRoomVO>>}
     */
    @Override
    public Result<List<MeetingRoomVO>> getAvailableMeetingRooms (LocalDateTime startTime, LocalDateTime endTime) {
        //根据时间段把占用的会议查出来
        LambdaQueryWrapper<MeetingRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.notIn(MeetingRecord::getStatus, MEETING_RECORD_STATUS_CANCEL);
        //开始时间或结束时间在时间段内 或 开始时间与结束时间之间包含时间段
        queryWrapper.and(recordQueryWrapper -> recordQueryWrapper.between(MeetingRecord::getStartTime, startTime, endTime)
                .or().between(MeetingRecord::getEndTime, startTime, endTime)
                .or().lt(MeetingRecord::getStartTime, startTime).gt(MeetingRecord::getEndTime, endTime));
        List<MeetingRecord> meetingRecords = meetingRecordService.list(queryWrapper);
        List<MeetingRoom> meetingRooms = this.list(new LambdaQueryWrapper<MeetingRoom>()
                .notIn(MeetingRoom::getStatus, MEETINGROOM_STATUS_PAUSE));
        Iterator<MeetingRoom> iterator = meetingRooms.iterator();
        while (iterator.hasNext()) {
            MeetingRoom meetingRoom = iterator.next();
            for (MeetingRecord meetingRecord : meetingRecords) {
                if (meetingRoom.getId().equals(meetingRecord.getMeetingRoomId())) {
                    //删除被占用的会议室
                    iterator.remove();
                    break;
                }
            }
        }
        List<MeetingRoomVO> meetingRoomVOList = new ArrayList<>();
        for (MeetingRoom meetingRoom : meetingRooms) {
            MeetingRoomVO meetingRoomVO = new MeetingRoomVO();
            meetingRoomVO.setMeetingRoomId(meetingRoom.getId());
            meetingRoomVO.setRoomName(meetingRoom.getRoomName());
            meetingRoomVOList.add(meetingRoomVO);
        }
        return Result.success(meetingRoomVOList);
    }
}




