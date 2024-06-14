package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.constant.MeetingRecordStatusConstant;
import com.jfzt.meeting.constant.MessageConstant;
import com.jfzt.meeting.entity.*;
import com.jfzt.meeting.entity.dto.MeetingRoomDTO;
import com.jfzt.meeting.entity.vo.MeetingRoomOccupancyVO;
import com.jfzt.meeting.entity.vo.MeetingRoomStatusVO;
import com.jfzt.meeting.entity.vo.MeetingRoomVO;
import com.jfzt.meeting.entity.vo.TimePeriodStatusVO;
import com.jfzt.meeting.exception.ErrorCodeEnum;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.mapper.MeetingAttendeesMapper;
import com.jfzt.meeting.mapper.MeetingRoomMapper;
import com.jfzt.meeting.mapper.SysUserMapper;
import com.jfzt.meeting.service.MeetingDeviceService;
import com.jfzt.meeting.service.MeetingRecordService;
import com.jfzt.meeting.service.MeetingRoomService;
import com.jfzt.meeting.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.jfzt.meeting.constant.IsDeletedConstant.NOT_DELETED;
import static com.jfzt.meeting.constant.MeetingRecordStatusConstant.*;
import static com.jfzt.meeting.constant.MeetingRoomStatusConstant.*;
import static com.jfzt.meeting.constant.MessageConstant.*;
import static com.jfzt.meeting.constant.TimePeriodStatusConstant.*;

/**
 * 针对表【meeting_room(会议室表)】的数据库操作Service实现
 * @author zilong.deng
 * @since 2024-06-04 11:33:16
 */
@Slf4j
@Service
public class MeetingRoomServiceImpl extends ServiceImpl<MeetingRoomMapper, MeetingRoom> implements MeetingRoomService {

    private MeetingDeviceService meetingDeviceService;

    private SysUserMapper sysUserMapper;
    private MeetingRoomMapper meetingRoomMapper;
    private MeetingRecordService meetingRecordService;
    private SysUserService userService;
    private MeetingAttendeesMapper attendeesMapper;
    /**
     * setter注入
     */
    @Autowired
    public void setSysUserMapper (SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }
    @Autowired
    public void setMeetingDeviceService (MeetingDeviceService meetingDeviceService) {
        this.meetingDeviceService = meetingDeviceService;
    }
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
     * 新增会议室
     * @param meetingRoom 会议室对象
     * @return 结果
     */
    @Override
    public Result<Integer> addMeetingRoom (MeetingRoom meetingRoom) {
        if (meetingRoom.getCapacity()<=0){
            throw new RRException("会议室容量不正确",ErrorCodeEnum.SERVICE_ERROR_A0421.getCode());
        }
        // 根据创建人Id查询用户信息
        SysUser sysUser = sysUserMapper.selectByUserId(meetingRoom.getCreatedBy());
        // 查询会议室名称,判断是否有重复的会议室名称
        List<MeetingRoom> roomList = meetingRoomMapper.selectList(new QueryWrapper<>());
        List<String> roomName = roomList.stream().map(MeetingRoom::getRoomName).toList();
        for (String room : roomName) {
            // 会议室名称长度限制为15个字符
            if (meetingRoom.getRoomName().equals(room) || meetingRoom.getRoomName().length() > MAX_NAME_LENGTH) {
                throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0421);
            }
        }
        if (MessageConstant.SUPER_ADMIN_LEVEL.equals(sysUser.getLevel())
                || MessageConstant.ADMIN_LEVEL.equals(sysUser.getLevel())) {
            if (meetingRoom.getRoomName().isEmpty() || meetingRoom.getLocation().isEmpty()
                    || meetingRoom.getCapacity() == null) {
                throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0410);
            }
            int result = meetingRoomMapper.insert(meetingRoom);
            if (result > 0) {
                return Result.success(result);
            }
            return Result.fail(ErrorCodeEnum.SERVICE_ERROR_A0421);
        }
        return Result.fail(ErrorCodeEnum.SERVICE_ERROR_A0301);
    }

    /**
     * 删除会议室
     * @param id 会议室id
     * @return 删除结果
     */
    @Override
    public Result<Integer> deleteMeetingRoom (Long id, Integer currentLevel) {
        if (MessageConstant.SUPER_ADMIN_LEVEL.equals(currentLevel)
                || MessageConstant.ADMIN_LEVEL.equals(currentLevel)) {
            // 删除会议室
            if (id != null) {
                int result = meetingRoomMapper.deleteById(id);
                if (result > 0) {
                    return Result.success(result);
                } else {
                    throw new RRException(ErrorCodeEnum.SYSTEM_ERROR_B0001);
                }
            } else {
                throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0301);
            }
        } else {
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0301);
        }
    }


    /**
     * 修改会议室状态
     * @param meetingRoomDTO 会议室DTO对象
     * @return 修改结果
     */
    @Override
    public Result<Integer> updateStatus (MeetingRoomDTO meetingRoomDTO) {
        // 检查输入的会议室ID是否存在
        MeetingRoom meetingRoom = meetingRoomMapper.selectById(meetingRoomDTO.getId());
        if (meetingRoom == null) {
            log.error(UPDATE_FAIL + EXCEPTION_TYPE, RRException.class);
            throw new RRException(UPDATE_FAIL, ErrorCodeEnum.SERVICE_ERROR_A0421.getCode());
        }
        if (meetingRoomDTO.getStatus() == null || meetingRoomDTO.getCurrentLevel() == null) {
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0410);
        }
        // 获取当前登录用户的权限等级
        if (MessageConstant.SUPER_ADMIN_LEVEL.equals(meetingRoomDTO.getCurrentLevel())
                || MessageConstant.ADMIN_LEVEL.equals(meetingRoomDTO.getCurrentLevel())) {
            int row = meetingRoomMapper.updateStatus(meetingRoomDTO.getId(), meetingRoomDTO.getStatus());
            if (row > 0) {
                return Result.success(row);
            }
            log.error("修改会议室状态失败！");
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0421);
        }
        throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0301);
    }

    /**
     * 查询被禁用的会议室的id
     * @param currentLevel 当前登录用户的权限等级
     * @return 权限等级
     */
    @Override
    public Result<List<Long>> selectUsableRoom (Integer currentLevel) {
        if (MessageConstant.SUPER_ADMIN_LEVEL.equals(currentLevel)
                || MessageConstant.ADMIN_LEVEL.equals(currentLevel)) {
            List<MeetingRoom> roomList = meetingRoomMapper.selectList(new QueryWrapper<>());
            List<Long> collect = roomList
                    .stream()
                    .filter(room -> MEETINGROOM_STATUS_PAUSE.equals(room.getStatus()))
                    .map(MeetingRoom::getId)
                    .collect(Collectors.toList());
            return Result.success(collect);
        }
        throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0301);
    }


    /**
     * 查询近七天会议室占用率（9：00-18：00）不包括周末
     * @return 会议室占用率VO
     */
    @Override
    public Result<List<MeetingRoomOccupancyVO>> getAllMeetingRoomOccupancy () {
        List<MeetingRoom> meetingRooms = this.list();
        if (meetingRooms.isEmpty()){
            return Result.success(new ArrayList<>());
        }
        //查出七日内所有会议
        List<MeetingRecord> meetingRecordList = meetingRecordService.list(new LambdaQueryWrapper<MeetingRecord>()
                .gt(MeetingRecord::getStartTime, LocalDateTime.now().toLocalDate().atStartOfDay().minusDays(7))
                .lt(MeetingRecord::getEndTime, LocalDateTime.now().toLocalDate().atStartOfDay())
                .and(wrapper -> wrapper.eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_NOT_START)
                        .or().eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_PROCESSING)
                        .or().eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_END)));
        //遍历会议室
        List<MeetingRoomOccupancyVO> meetingRoomOccupancyVOList = meetingRooms.stream().map(meetingRoom -> {
            //被占用的时间段总数
            long count = 0L;
            long total = 0L;
            //统计前七天，九点开始，十八点结束，半小时为一段
            LocalDateTime startTime = LocalDateTime.now().toLocalDate().atStartOfDay().plusHours(9).minusDays(7);
            LocalDateTime endTime = startTime.plusMinutes(30);
            //遍历七天
            for (int i = 0; i < 7; i++) {
                // 跳过周末
                if (startTime.toLocalDate().getDayOfWeek() == DayOfWeek.SATURDAY
                        || startTime.toLocalDate().getDayOfWeek() == DayOfWeek.SUNDAY) {
                    startTime = startTime.plusDays(1);
                    endTime = endTime.plusDays(1);
                    continue;
                }
                log.info("第{}天,startTime:{}" + "endTime:{}", i + 1, startTime, endTime);
                //每天18个时间段
                for (int j = 0; j < 18; j++) {
                    LocalDateTime finalStartTime = startTime;
                    LocalDateTime finalEndTime = endTime;
                    count = count + meetingRecordList.stream().filter(record -> {
                        //开始时间在时间段内 前含后不含  8-9 属于8-8.5不属于7.5-8
                        return Objects.equals(record.getMeetingRoomId(), meetingRoom.getId())
                                && (record.getStartTime().isAfter(finalStartTime.minusSeconds(1))
                                && record.getStartTime().isBefore(finalEndTime)
                                //结束时间在时间段内 前不含后含  8-9属于8.5-9不属于9-9.5
                                || record.getEndTime().isAfter(finalStartTime)
                                && record.getEndTime().isBefore(finalEndTime.plusSeconds(1))
                                //时间段包含在开始时间(含)和结束时间(含)之间    8-9 属于8-8.5属于8.5-9
                                || record.getStartTime().isBefore(finalStartTime.plusSeconds(1))
                                && record.getEndTime().isAfter(finalEndTime.minusSeconds(1)));
                    }).count();
                    //被占用，总数加
                    log.info("时间段：{}--{},count:{}", startTime, endTime, count);
                    //下一时间段
                    startTime = startTime.plusMinutes(30);
                    endTime = endTime.plusMinutes(30);
                }
                //下一天
                startTime = startTime.plusHours(15);
                endTime = startTime.plusMinutes(30);
                total = total + 1;
            }
            MeetingRoomOccupancyVO meetingRoomOccupancyVO = new MeetingRoomOccupancyVO();
            meetingRoomOccupancyVO.setOccupied(count);
            meetingRoomOccupancyVO.setTotal((total * 18));
            float occupancyRate = ((float) count / (total * 18));
            meetingRoomOccupancyVO.setOccupancyRate(occupancyRate);
            meetingRoomOccupancyVO.setId(meetingRoom.getId());
            meetingRoomOccupancyVO.setName(meetingRoom.getRoomName());
            return meetingRoomOccupancyVO;
        }).sorted((o1, o2) -> Math.toIntExact(o2.getOccupied() - o1.getOccupied())).collect(Collectors.toList());
        return Result.success(meetingRoomOccupancyVOList);
    }

    /**
     * 查询最近五个工作日内各会议室占用比例
     * @return 会议室占用比例VO
     */
    @Override
    public Result<List<MeetingRoomOccupancyVO>> getAllMeetingRoomProportion () {
        //查询所有会议室
        List<MeetingRoom> meetingRoomList = list(new LambdaQueryWrapper<MeetingRoom>()
                .eq(MeetingRoom::getStatus, MEETINGROOM_STATUS_AVAILABLE));
        //查出七日内所有会议
        List<MeetingRecord> meetingRecordList = meetingRecordService.list(new LambdaQueryWrapper<MeetingRecord>()
                .in(MeetingRecord::getMeetingRoomId, meetingRoomList.stream()
                        .map(MeetingRoom::getId).collect(Collectors.toList()))
                .gt(MeetingRecord::getStartTime, LocalDateTime.now().toLocalDate().atStartOfDay().minusDays(7))
                .lt(MeetingRecord::getEndTime, LocalDateTime.now().toLocalDate().atStartOfDay())
                .and(wrapper -> wrapper.eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_NOT_START)
                        .or().eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_PROCESSING)
                        .or().eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_END))
        );
        long total = meetingRecordList.size();
        List<MeetingRoomOccupancyVO> occupancyVOList = meetingRoomList.stream().map(meetingRoom -> {
            MeetingRoomOccupancyVO roomOccupancyVO = new MeetingRoomOccupancyVO();
            long occupied = meetingRecordList.stream().filter(meetingRecord ->
                    Objects.equals(meetingRecord.getMeetingRoomId(), meetingRoom.getId())).count();
            if (total == 0) {
                roomOccupancyVO.setOccupied(0L);
            }
            long occupancyRate = occupied / total;
            roomOccupancyVO.setName(meetingRoom.getRoomName());
            roomOccupancyVO.setId(meetingRoom.getId());
            roomOccupancyVO.setTotal(total);
            roomOccupancyVO.setOccupied(occupied);
            roomOccupancyVO.setOccupancyRate(occupancyRate);
            return roomOccupancyVO;
        }).sorted((o1, o2) -> Math.toIntExact(o2.getOccupied() - o1.getOccupied())).toList();
        return Result.success(occupancyVOList);
    }

    @Override
    public Result<Integer> updateRoom(MeetingRoomDTO meetingRoomDTO) {
        // 检查输入的会议室ID是否存在
        MeetingRoom meetingRoom = meetingRoomMapper.selectById(meetingRoomDTO.getId());
        if (meetingRoom == null) {
            log.error(UPDATE_FAIL + EXCEPTION_TYPE, RRException.class);
            throw new RRException(UPDATE_FAIL, ErrorCodeEnum.SERVICE_ERROR_A0421.getCode());
        }
        if (meetingRoomDTO.getRoomName() == null || meetingRoomDTO.getCurrentLevel() == null ||
                meetingRoomDTO.getLocation() == null || meetingRoomDTO.getCapacity() == null ||
                meetingRoomDTO.getStatus() == null) {
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0410);
        }
        // 获取当前登录用户的权限等级
        if (MessageConstant.SUPER_ADMIN_LEVEL.equals(meetingRoomDTO.getCurrentLevel())
                || MessageConstant.ADMIN_LEVEL.equals(meetingRoomDTO.getCurrentLevel())) {
            int row = meetingRoomMapper.updateRoom(meetingRoomDTO.getId(), meetingRoomDTO.getRoomName(),
                    meetingRoomDTO.getLocation(), meetingRoomDTO.getCapacity(), meetingRoomDTO.getStatus());
            if (row > 0) {
                return Result.success(row);
            }
            log.error("修改会议室失败！");
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0421);
        }
        throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0301);
    }

    /**
     * 查询会议室状态
     * @return 会议室状态VO
     */
    @Override
    public List<MeetingRoomStatusVO> getMeetingRoomStatus () {
        //查询当前时间段会议室的使用信息,当前时间在开始或结束区间内的会议,填充到会议室VO,不更新会议室,状态只有01
        //查询所有会议室
        List<MeetingRoom> meetingRoomList = list(new LambdaQueryWrapper<MeetingRoom>()
                .eq(MeetingRoom::getIsDeleted, NOT_DELETED).orderByAsc(MeetingRoom::getId));

        return meetingRoomList.stream().map(meetingRoom -> {
            MeetingRoomStatusVO meetingRoomStatusVO = new MeetingRoomStatusVO();
            //复制到VO
            BeanUtils.copyProperties(meetingRoom, meetingRoomStatusVO);
            //插入设备信息
            meetingRoomStatusVO.setEquipment(
                    String.join(",", meetingDeviceService
                            .list(new LambdaQueryWrapper<MeetingDevice>()
                                    .eq(MeetingDevice::getRoomId, meetingRoom.getId()))
                            .stream().map(MeetingDevice::getDeviceName).toList()));
            //暂停使用直接返回
            if (Objects.equals(meetingRoomStatusVO.getStatus(), MEETINGROOM_STATUS_PAUSE)) {
                return meetingRoomStatusVO;
            }
            LambdaQueryWrapper<MeetingRecord> recordQueryWrapper = new LambdaQueryWrapper<>();
            recordQueryWrapper.lt(MeetingRecord::getStartTime, LocalDateTime.now())
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
                List<String> userIds = attendeesMapper.selectList(
                                new LambdaQueryWrapper<MeetingAttendees>()
                                        .eq(MeetingAttendees::getMeetingRecordId, meetingRecord.getId()))
                        .stream().map(MeetingAttendees::getUserId).toList();
                StringBuffer attendees = new StringBuffer();
                userService.getUserInfo(userIds, attendees, null);
                meetingRoomStatusVO.setAttendees(attendees.toString());
            }
            return meetingRoomStatusVO;
        }).collect(Collectors.toList());
    }

    /**
     * 查询当天各个时间段会议室占用情况
     * @return 时间段占用状态
     */
    @Override
    public List<Integer> getTodayTimePeriodStatus () {
        List<Integer> timeStatus = new LinkedList<>();
        //获取可使用的会议室总数
        List<Long> roomIds = this.list(new LambdaQueryWrapper<MeetingRoom>()
                        .eq(MeetingRoom::getStatus, MEETINGROOM_STATUS_AVAILABLE))
                .stream().map(MeetingRoom::getId)
                .distinct().toList();
        long count = roomIds.size();
        if (count == 0) {
            //无可用会议室
            for (int i = 0; i < 30; i++) {
                //全为已过期
                timeStatus.add(i, TIME_PERIOD_OVERDUE);
            }
            return timeStatus;
        }
        LocalDateTime now = LocalDateTime.now();
        //更新今日所有会议记录状态
        meetingRecordService.updateTodayRecordStatus();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime startTime = startOfDay.plusHours(8);
        LocalDateTime endTime = startTime.plusMinutes(30);
        //每段30分钟遍历时间，在当前时间之前的时间段状态为0已过期
        for (int i = 0; i < 30; i++) {
            //时间段开始在当前时间前
            if (now.isAfter(startTime)) {
                timeStatus.add(i, TIME_PERIOD_OVERDUE);
            } else {
                //之后的时间，判断是否有会议的开始时间或结束时间包含在里面，有的话且包含所有的会议室则为1已预订
                LambdaQueryWrapper<MeetingRecord> recordQueryWrapper = new LambdaQueryWrapper<>();
                recordQueryWrapper.in(MeetingRecord::getMeetingRoomId, roomIds);
                recordQueryWrapper.and(wrapper -> wrapper.eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_NOT_START)
                        .or().eq(MeetingRecord::getStatus, MeetingRecordStatusConstant.MEETING_RECORD_STATUS_PROCESSING));
                LocalDateTime finalStartTime = startTime;
                LocalDateTime finalEndTime = endTime;
                recordQueryWrapper.and(wrapper -> wrapper
                        //开始时间在时间段内 前含后不含  8-9 属于8-8.5不属于7.5-8
                        .between(MeetingRecord::getStartTime, finalStartTime, finalEndTime.minusSeconds(1))
                        //结束时间在时间段内 前不含后含  8-9属于8.5-9不属于9-9.5
                        .or().between(MeetingRecord::getEndTime, finalStartTime.plusSeconds(1), finalEndTime)
                        //时间段包含在开始时间(含)和结束时间(含)之间    8-9 属于8-8.5属于8.5-9
                        .or().lt(MeetingRecord::getStartTime, finalStartTime.plusSeconds(1))
                        .gt(MeetingRecord::getEndTime, finalEndTime.minusSeconds(1)));
                List<MeetingRecord> meetingRecords = meetingRecordService.list(recordQueryWrapper);
                //根据会议室id收集获得占用不同会议室的数量
                long size = meetingRecords.stream()
                        .collect(Collectors.groupingBy(MeetingRecord::getMeetingRoomId))
                        .size();
                //判断是否全部被占用
                if (size >= count) {
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
     * @param id   会议室id
     * @param date 日期
     * @return 时间段状态
     */
    @Override
    public Result<List<TimePeriodStatusVO>> getTimePeriodStatusByIdAndDate (Long id, LocalDate date) {
        if (id == null || date == null) {
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0400);
        }
        List<TimePeriodStatusVO> timePeriodStatusVOList = new LinkedList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = LocalDateTime.of(date, LocalTime.of(8, 0));
        LocalDateTime endTime = startTime.plusMinutes(30);
        for (int i = 0; i < 30; i++) {
            TimePeriodStatusVO timePeriodStatusVO = new TimePeriodStatusVO();
            //判断是否过期
            if (now.isAfter(startTime)) {
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
                recordQueryWrapper.and(wrapper -> wrapper
                        //开始时间在时间段内 前含后不含  8-9 属于8-8.5不属于7.5-8
                        .between(MeetingRecord::getStartTime, finalStartTime, finalEndTime.minusSeconds(1))
                        //结束时间在时间段内 前不含后含  8-9属于8.5-9不属于9-9.5
                        .or().between(MeetingRecord::getEndTime, finalStartTime.plusSeconds(1), finalEndTime)
                        //时间段包含在开始时间(含)和结束时间(含)之间    8-9 属于8-8.5属于8.5-9
                        .or().lt(MeetingRecord::getStartTime, finalStartTime.plusSeconds(1))
                        .gt(MeetingRecord::getEndTime, finalEndTime.minusSeconds(1)));
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
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 会议室VO
     */
    @Override
    public Result<List<MeetingRoomVO>> getAvailableMeetingRooms (LocalDateTime startTime, LocalDateTime endTime) {
        //根据时间段把占用的会议查出来
        LambdaQueryWrapper<MeetingRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.notIn(MeetingRecord::getStatus, MEETING_RECORD_STATUS_CANCEL);
        //开始时间或结束时间在时间段内 或 开始时间与结束时间之间包含时间段
        queryWrapper.and(recordQueryWrapper -> recordQueryWrapper
                //开始时间在时间段内 前含后不含  8-9 属于8-8.5不属于7.5-8
                .between(MeetingRecord::getStartTime, startTime, endTime.minusSeconds(1))
                //结束时间在时间段内 前不含后含  8-9属于8.5-9不属于9-9.5
                .or().between(MeetingRecord::getEndTime, startTime.plusSeconds(1), endTime)
                //时间段包含在开始时间(含)和结束时间(含)之间    8-9 属于8-8.5属于8.5-9
                .or().lt(MeetingRecord::getStartTime, startTime.plusSeconds(1))
                .gt(MeetingRecord::getEndTime, endTime.minusSeconds(1)));
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
            meetingRoomVO.setId(meetingRoom.getId());
            meetingRoomVO.setRoomName(meetingRoom.getRoomName());
            meetingRoomVO.setStatus(meetingRoom.getStatus());
            meetingRoomVOList.add(meetingRoomVO);
        }
        return Result.success(meetingRoomVOList);
    }
}




