package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.constant.MeetingRecordStatusConstant;
import com.jfzt.meeting.constant.MessageConstant;
import com.jfzt.meeting.entity.*;
import com.jfzt.meeting.entity.dto.DatePeriodDTO;
import com.jfzt.meeting.entity.dto.MeetingRoomDTO;
import com.jfzt.meeting.entity.vo.*;
import com.jfzt.meeting.exception.ErrorCodeEnum;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.mapper.MeetingAttendeesMapper;
import com.jfzt.meeting.mapper.MeetingRoomMapper;
import com.jfzt.meeting.mapper.SysUserMapper;
import com.jfzt.meeting.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.jfzt.meeting.constant.MeetingRecordStatusConstant.*;
import static com.jfzt.meeting.constant.MeetingRoomStatusConstant.*;
import static com.jfzt.meeting.constant.MessageConstant.*;
import static com.jfzt.meeting.constant.TimePeriodStatusConstant.*;
import static java.util.stream.Collectors.toList;

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
    private DeviceErrorMessageService deviceErrorMessageService;

    /**
     * setter注入
     */
    @Autowired
    public void setSysUserMapper (SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Autowired
    public void setDeviceErrorMessageService (DeviceErrorMessageService deviceErrorMessageService) {
        this.deviceErrorMessageService = deviceErrorMessageService;
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
        // meetingRoom的参数不能为空
        if (meetingRoom.getRoomName().isEmpty() || meetingRoom.getCapacity() == null
                || meetingRoom.getCapacity() <= 0) {
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0421);
        }
        // 会议室位置非空且长度限制30个字符，会议室名称长度限制为15个字符
        String location = meetingRoom.getLocation();
        if ((location != null && location.length() > MAX_ROOM_LOCATION_LENGTH)
                || meetingRoom.getRoomName().length() > MAX_ROOM_NAME_LENGTH) {
            return Result.fail("会议室位置长度或会议室名称过长！");
        }
        // 查询会议室名称,判断是否有重复的会议室名称
        List<MeetingRoom> roomList = meetingRoomMapper.selectList(new QueryWrapper<>());
        List<String> roomName = roomList.stream().map(MeetingRoom::getRoomName).toList();
        for (String room : roomName) {
            // 会议室名称长度限制为15个字符
            if (meetingRoom.getRoomName().equals(room)) {
                throw new RRException("会议室重复！");
            }
        }
        // 根据创建人Id查询用户信息
        SysUser sysUser = sysUserMapper.selectByUserId(meetingRoom.getCreatedBy());
        if (MessageConstant.SUPER_ADMIN_LEVEL.equals(sysUser.getLevel())
                || MessageConstant.ADMIN_LEVEL.equals(sysUser.getLevel())) {
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
    @Transactional
    @Override
    public Result<Integer> deleteMeetingRoom (Long id, Integer currentLevel) {
        if (MessageConstant.SUPER_ADMIN_LEVEL.equals(currentLevel)
                || MessageConstant.ADMIN_LEVEL.equals(currentLevel)) {
            // 根据id查询会议室信息
            MeetingRoom meetingRoom = meetingRoomMapper.selectById(id);
            if (meetingRoom == null) {
                throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0400);
            } else {
                int result = meetingRoomMapper.deleteById(id);
                if (result > 0) {
                    meetingDeviceService.remove(new LambdaQueryWrapper<MeetingDevice>().eq(MeetingDevice::getRoomId, id));
                    return Result.success(result);
                } else {
                    throw new RRException(ErrorCodeEnum.SYSTEM_ERROR_B0001);
                }
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
                    .collect(toList());
            return Result.success(collect);
        }
        throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0301);
    }

    /**
     * 统计时间区间内各会议室占用率以及被占用次数前三的时间段（只统计工作日9：00-18：00，参数为空默认前五个工作日）
     * @param datePeriodDTO 日期区间
     * @return 会议室占用率VO
     */
    @Override
    public Result<List<MeetingRoomOccupancyVO>> getAllMeetingRoomOccupancy (DatePeriodDTO datePeriodDTO) {
        //查询所有会议室，包括禁用的
        List<MeetingRoom> meetingRooms = this.list();
        if (meetingRooms.isEmpty()) {
            return Result.success(new ArrayList<>());
        }
        //无条件默认统计前七天
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now().minusDays(1);
        if (datePeriodDTO.getStartDate() != null && datePeriodDTO.getEndDate() != null) {
            startDate = datePeriodDTO.getStartDate();
            endDate = datePeriodDTO.getEndDate();
        }
        //查出开始时间段在时间段内9:00-18:00的所有会议
        List<MeetingRecord> meetingRecordList = meetingRecordService.list(new LambdaQueryWrapper<MeetingRecord>()
                .gt(MeetingRecord::getStartTime, startDate.atStartOfDay())
                .lt(MeetingRecord::getStartTime, endDate.atStartOfDay().plusHours(23).plusSeconds(59))
                .and(wrapper -> wrapper.eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_NOT_START)
                        .or().eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_PROCESSING)
                        .or().eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_END)));
        //遍历会议室
        LocalDate finalEndDate = endDate;
        LocalDate finalStartDate = startDate;
        List<MeetingRoomOccupancyVO> meetingRoomOccupancyVOList = meetingRooms.stream().map(meetingRoom -> {
            //被占用的时间段总数
            long totalOccupancy = 0L;
            //总时间段数
            long total = 0L;
            ArrayList<TimePeriodOccupancyVO> timePeriodList = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                TimePeriodOccupancyVO timePeriodOccupancyVO = new TimePeriodOccupancyVO();
                timePeriodOccupancyVO.setOccupancyRate(0L);
                timePeriodOccupancyVO.setOccupied(0L);
                timePeriodList.add(timePeriodOccupancyVO);
            }
            //遍历七天
            for (LocalDate i = finalStartDate; i.isBefore(finalEndDate.plusDays(1));
                 i = i.plusDays(1)) {
                // 跳过周末
                if (i.getDayOfWeek() == DayOfWeek.SATURDAY
                        || i.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    continue;
                }
                //统计从九点开始，十八点结束，半小时为一段
                LocalDateTime startTime = i.atStartOfDay().plusHours(9);
                LocalDateTime endTime = startTime.plusMinutes(60);
                //每天18个时间段
                for (int j = 0; j < 9; j++) {
                    LocalDateTime finalStartTime = startTime;
                    LocalDateTime finalEndTime = endTime;
                    long occupied = meetingRecordList.stream().filter(record -> record.getMeetingRoomId().equals(meetingRoom.getId())
                            //开始时间在时间段内 前含后不含  8-9 属于8-8.5不属于7.5-8
                            && (record.getStartTime().isAfter(finalStartTime.minusSeconds(1))
                            && record.getStartTime().isBefore(finalEndTime)
                            //结束时间在时间段内 前不含后含  8-9属于8.5-9不属于9-9.5
                            || record.getEndTime().isAfter(finalStartTime)
                            && record.getEndTime().isBefore(finalEndTime.plusSeconds(1))
                            //时间段包含在开始时间(含)和结束时间(含)之间    8-9 属于8-8.5属于8.5-9
                            || record.getStartTime().isBefore(finalStartTime.plusSeconds(1))
                            && record.getEndTime().isAfter(finalEndTime.minusSeconds(1)))).count();
                    TimePeriodOccupancyVO timePeriodOccupancyVO = timePeriodList.get(j);
                    timePeriodOccupancyVO.setTimePeriod(startTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                            + "-" + endTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                    if (occupied > 0) {
                        totalOccupancy++;
                        timePeriodOccupancyVO.setOccupied(timePeriodOccupancyVO.getOccupied() + 1);
                    }
                    timePeriodList.set(j, timePeriodOccupancyVO);
                    //下一时间段
                    startTime = startTime.plusMinutes(60);
                    endTime = endTime.plusMinutes(60);
                }
                //总时间段加
                total = total + 9;
            }
            MeetingRoomOccupancyVO meetingRoomOccupancyVO = new MeetingRoomOccupancyVO();
            meetingRoomOccupancyVO.setRoomId(meetingRoom.getId());
            meetingRoomOccupancyVO.setRoomName(meetingRoom.getRoomName());
            meetingRoomOccupancyVO.setTotal(total);
            meetingRoomOccupancyVO.setTotalOccupancy(totalOccupancy);
            float rate = (float) totalOccupancy / total;
            meetingRoomOccupancyVO.setTotalOccupancyRate(rate);
            long finalTotal = total;
            List<TimePeriodOccupancyVO> list = timePeriodList.stream()
                    .peek(timePeriod ->
                            timePeriod.setOccupancyRate(((float) timePeriod.getOccupied() / finalTotal)))
                    .sorted((o1, o2) -> (int) (o2.getOccupied() - o1.getOccupied()))
                    .toList();
            TimePeriodOccupancyVO others = new TimePeriodOccupancyVO();
            others.setTimePeriod("others");
            others.setOccupied(0L);
            for (int i = 3; i < 9; i++) {
                TimePeriodOccupancyVO timePeriod = list.get(i);
                others.setOccupied(others.getOccupied() + timePeriod.getOccupied());
            }
            others.setOccupancyRate(((float) others.getOccupied() / finalTotal));
            TimePeriodOccupancyVO notOccupancyVO = new TimePeriodOccupancyVO();
            notOccupancyVO.setOccupied(total - totalOccupancy);
            notOccupancyVO.setTimePeriod("未占用");
            notOccupancyVO.setOccupancyRate(1 - list.get(0).getOccupancyRate()
                    - list.get(1).getOccupancyRate() - list.get(2).getOccupancyRate()
                    - others.getOccupancyRate());
            ArrayList<TimePeriodOccupancyVO> occupancyVOList = new ArrayList<>();
            occupancyVOList.add(notOccupancyVO);
            occupancyVOList.add(list.get(0));
            occupancyVOList.add(list.get(1));
            occupancyVOList.add(list.get(2));
            occupancyVOList.add(others);
            List<TimePeriodOccupancyVO> voList = occupancyVOList.stream().toList();
            meetingRoomOccupancyVO.setTimePeriods(occupancyVOList);
            occupancyVOList.clear();
            occupancyVOList.addAll(voList);
            return meetingRoomOccupancyVO;
        }).toList();
        return Result.success(meetingRoomOccupancyVOList);
    }

    /**
     * 查询时间区间内各会议室选择率
     * @param datePeriodDTO 日期区间
     * @return 会议室占用比例VO
     */
    @Override
    public Result<List<MeetingRoomSelectionVO>> getAllMeetingRoomProportion (DatePeriodDTO datePeriodDTO) {
        //无条件默认统计前七天
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now().minusDays(1);
        if (datePeriodDTO.getStartDate() != null && datePeriodDTO.getEndDate() != null) {
            startDate = datePeriodDTO.getStartDate();
            endDate = datePeriodDTO.getEndDate();
        }
        //查询所有会议室
        List<MeetingRoom> meetingRoomList = list();
        if (meetingRoomList.isEmpty()) {
            return Result.success(new ArrayList<>());
        }
        //查出时间段内所有会议
        List<MeetingRecord> meetingRecordList = meetingRecordService.list(new LambdaQueryWrapper<MeetingRecord>()
                .in(MeetingRecord::getMeetingRoomId, meetingRoomList.stream()
                        .map(MeetingRoom::getId).collect(toList()))
                .gt(MeetingRecord::getStartTime, startDate.atStartOfDay())
                .lt(MeetingRecord::getEndTime, endDate.atStartOfDay().plusHours(18))
                .and(wrapper -> wrapper.eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_NOT_START)
                        .or().eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_PROCESSING)
                        .or().eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_END))
        );
        //会议总次数
        long total = meetingRecordList.size();
        //所有会议室被选择比例
        List<MeetingRoomSelectionVO> occupancyVOList = meetingRoomList.stream()
                .map(meetingRoom -> {
                    MeetingRoomSelectionVO roomSelectionVO = new MeetingRoomSelectionVO();
                    long occupied = meetingRecordList.stream().filter(meetingRecord ->
                            Objects.equals(meetingRecord.getMeetingRoomId(), meetingRoom.getId())).count();
                    if (total == 0) {
                        roomSelectionVO.setTotal(0L);
                    }
                    roomSelectionVO.setRoomName(meetingRoom.getRoomName());
                    roomSelectionVO.setTotal(total);
                    roomSelectionVO.setSelected(occupied);
                    return roomSelectionVO;
                })
                .filter(item -> item.getSelected() != 0)
                .sorted((o1, o2) -> Math.toIntExact(o2.getSelected() - o1.getSelected()))
                .toList();
        //大于五个会议室只保留前五，其余合并成其他
        if (occupancyVOList.size() > 5) {
            List<MeetingRoomSelectionVO> subList = occupancyVOList.subList(0, 5);
            occupancyVOList = new ArrayList<>(subList);
            MeetingRoomSelectionVO roomSelectionVO = new MeetingRoomSelectionVO();
            roomSelectionVO.setRoomName("其他会议室");
            roomSelectionVO.setRoomId(0L);
            roomSelectionVO.setTotal(occupancyVOList.getFirst().getTotal());
            roomSelectionVO.setSelected(0L);
            occupancyVOList.forEach(item ->
                    roomSelectionVO.setSelected(roomSelectionVO.getSelected() + item.getSelected()));
            //设置其他被选中次数
            roomSelectionVO.setSelected(roomSelectionVO.getTotal() - roomSelectionVO.getSelected());
            occupancyVOList.add(roomSelectionVO);
        }
        return Result.success(occupancyVOList);
    }

    /**
     * 修改会议室
     * @param meetingRoomDTO 会议室DTO对象
     * @return 修改结果
     */
    @Override
    public Result<Integer> updateRoom (MeetingRoomDTO meetingRoomDTO) {
        // meetingRoomDTO中的参数都不能为空
        if (meetingRoomDTO.getRoomName() == null || meetingRoomDTO.getCurrentLevel() == null
                || meetingRoomDTO.getCapacity() == null || meetingRoomDTO.getStatus() == null) {
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0410);
        }
        // 会议室位置非空且长度限制30个字符
        String location = meetingRoomDTO.getLocation();
        if (location != null && location.length() > MAX_ROOM_LOCATION_LENGTH) {
            return Result.fail("会议室位置长度不能超过30个字符！");
        }
        // 会议室名称长度限制为15个字符
        if (meetingRoomDTO.getRoomName().length() > MAX_ROOM_NAME_LENGTH) {
            throw new RRException("会议室名称长度不能超过15个字符！");
        }
        // 获取要修改会议室的原来的名称
        String roomName = meetingRoomMapper.selectById(meetingRoomDTO.getId()).getRoomName();
        // 判断新的会议室名称是否与原来的重复
        if (!roomName.equals(meetingRoomDTO.getRoomName())) {
            // 查询会议室名称,判断是否有重复的会议室名称
            List<MeetingRoom> roomList = meetingRoomMapper.selectList(new QueryWrapper<>());
            List<String> roomNameList = roomList.stream().map(MeetingRoom::getRoomName).toList();
            for (String room : roomNameList) {
                // 判断新的会议室名称和数据库中的会议室名称是否没有重复的，如果没有，则检查新的名字是否和数据库的其他名字相同
                if (meetingRoomDTO.getRoomName().equals(room)) {
                    throw new RRException("会议室名称重复！");
                }
            }
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
                .orderByAsc(MeetingRoom::getId));

        return meetingRoomList.stream().map(meetingRoom -> {
            MeetingRoomStatusVO meetingRoomStatusVO = new MeetingRoomStatusVO();
            //复制到VO
            BeanUtils.copyProperties(meetingRoom, meetingRoomStatusVO);
            //插入设备信息
            meetingRoomStatusVO.setEquipment(
                    String.join(",", meetingDeviceService
                            .list(new LambdaQueryWrapper<MeetingDevice>()
                                    .eq(MeetingDevice::getRoomId, meetingRoom.getId())
                                    .eq(MeetingDevice::getStatus, 1))
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
        }).sorted((o1, o2) -> o2.getStatus() - o1.getStatus()).collect(toList());
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
        long totalOccupancy = roomIds.size();
        if (totalOccupancy == 0) {
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
                        .or()
                        .eq(MeetingRecord::getStatus, MeetingRecordStatusConstant.MEETING_RECORD_STATUS_PROCESSING));
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
                if (size >= totalOccupancy) {
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
                recordQueryWrapper
                        .and(wrapper -> wrapper.eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_NOT_START)
                                .or().eq(MeetingRecord::getStatus,
                                        MeetingRecordStatusConstant.MEETING_RECORD_STATUS_PROCESSING));
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
        meetingRooms = meetingRooms.stream()
                .filter(meetingRoom -> meetingRecords.stream()
                        .noneMatch(meetingRecord -> meetingRoom.getId().equals(meetingRecord.getMeetingRoomId())))
                .toList();
        List<MeetingRoomVO> meetingRoomVOList = new ArrayList<>();
        for (MeetingRoom meetingRoom : meetingRooms) {
            MeetingRoomVO meetingRoomVO = new MeetingRoomVO();
            ArrayList<String> exceptionInfoList = new ArrayList<>();
            List<MeetingDevice> deviceList = meetingDeviceService.list(new LambdaQueryWrapper<MeetingDevice>()
                    .eq(MeetingDevice::getRoomId, meetingRoom.getId()));

            deviceList.stream().peek(device -> {
                Result<List<DeviceErrorMessage>> deviceErrorMessage = deviceErrorMessageService
                        .getInfo(Math.toIntExact(device.getId()));
                if (deviceErrorMessage.getData() != null && !deviceErrorMessage.getData().isEmpty()) {
                    String errorMessage = device.getDeviceName() + "异常：" +
                            String.join(",",
                                    deviceErrorMessage.getData().stream().map(DeviceErrorMessage::getInfo).toList());
                    exceptionInfoList.add(errorMessage);
                }
            }).toList();
            meetingRoomVO.setDeviceExceptionInfo(exceptionInfoList);
            meetingRoomVO.setId(meetingRoom.getId());
            meetingRoomVO.setRoomName(meetingRoom.getRoomName());
            meetingRoomVO.setStatus(meetingRoom.getStatus());
            meetingRoomVOList.add(meetingRoomVO);
        }
        return Result.success(meetingRoomVOList);
    }
}




