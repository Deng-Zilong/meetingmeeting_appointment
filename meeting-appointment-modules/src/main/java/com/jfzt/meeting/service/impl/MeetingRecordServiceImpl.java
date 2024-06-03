package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingAttendees;
import com.jfzt.meeting.entity.MeetingRecord;
import com.jfzt.meeting.entity.MeetingRoom;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.dto.MeetingRecordDTO;
import com.jfzt.meeting.entity.vo.MeetingRecordVO;
import com.jfzt.meeting.entity.vo.PeriodTimesVO;
import com.jfzt.meeting.entity.vo.SysUserVO;
import com.jfzt.meeting.exception.ErrorCodeEnum;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.mapper.MeetingAttendeesMapper;
import com.jfzt.meeting.mapper.MeetingRecordMapper;
import com.jfzt.meeting.mapper.MeetingRoomMapper;
import com.jfzt.meeting.service.*;
import com.jfzt.meeting.task.MeetingReminderScheduler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.jfzt.meeting.constant.IsDeletedConstant.IS_DELETED;
import static com.jfzt.meeting.constant.IsDeletedConstant.NOT_DELETED;
import static com.jfzt.meeting.constant.MeetingRecordStatusConstant.*;
import static com.jfzt.meeting.constant.MessageConstant.*;

/**
 * @author zilong.deng
 * @description 针对表【meeting_record(会议记录表)】的数据库操作Service实现
 * @since 2024-04-28 11:47:39
 */
@Slf4j
@Service
public class MeetingRecordServiceImpl extends ServiceImpl<MeetingRecordMapper, MeetingRecord>
        implements MeetingRecordService {

    @Resource
    private MeetingAttendeesService meetingAttendeesService;
    @Resource
    private MeetingRoomService meetingRoomService;
    @Resource
    private SysUserService userService;
    @Resource
    private MeetingAttendeesMapper attendeesMapper;
    @Resource
    private MeetingRoomMapper meetingRoomMapper;
    @Autowired
    private MeetingReminderScheduler meetingReminderScheduler;
    @Autowired
    private MeetingMinutesService meetingMinutesService;


    /**
     * @param userId 用户id
     * @return {@code List<MeetingRecordVO>}
     * @description 获取当天用户参与的所有会议
     */
    @Override
    public List<MeetingRecordVO> getTodayMeetingRecord (String userId) {
        if (userId.isBlank()) {
            throw new RRException("用户id不能为空", ErrorCodeEnum.SERVICE_ERROR_A0400.getCode());
        }
        LambdaQueryWrapper<MeetingRecord> recordQueryWrapper = new LambdaQueryWrapper<>();
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 获取当天开始时间（00:00:00）
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        // 获取当天结束时间（23:59:59）
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        recordQueryWrapper.and(wq -> wq.between(MeetingRecord::getStartTime, startOfDay, endOfDay)
                .or().between(MeetingRecord::getEndTime, startOfDay, endOfDay));
        recordQueryWrapper.and(wq -> wq
                //删除的不展示
                .eq(MeetingRecord::getIsDeleted, NOT_DELETED));
        //展示未取消的会议
        recordQueryWrapper.and(wq -> wq.eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_NOT_START)
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
                    if (meetingRecord == null) {
                        return null;
                    }
                    MeetingRecordVO meetingRecordVO = new MeetingRecordVO();
                    //通过会议id找到参会人列表
                    List<MeetingAttendees> attendeesList = meetingAttendeesService
                            .list(new LambdaQueryWrapper<MeetingAttendees>()
                                    .eq(MeetingAttendees::getMeetingRecordId, meetingRecord.getId()));
                    if (attendeesList.isEmpty()) {
                        //没有匹配的参会人，返回空对象，最后过滤
                        return null;
                    }
                    List<String> userIds = attendeesList.stream().map(MeetingAttendees::getUserId).toList();
                    //当前用户参与会议
                    if (userIds.contains(userId)) {
                        List<SysUserVO> users = new ArrayList<>();
                        StringBuffer attendees = new StringBuffer();
                        //遍历参会人，拼接姓名，获取userIdList
                        userService.getUserInfo(userIds, attendees, users);
                        //更新会议状态
                        updateRecordStatus(meetingRecord);
                        meetingRecord = this.getById(meetingRecord.getId());
                        //插入会议信息
                        BeanUtils.copyProperties(meetingRecord, meetingRecordVO);
                        //插入会议室信息
                        List<MeetingRoom> meetingRoomList = meetingRoomService
                                .list(new LambdaQueryWrapper<MeetingRoom>()
                                        .eq(MeetingRoom::getId, meetingRecord.getMeetingRoomId()));
                        if (!meetingRoomList.isEmpty()) {
                            MeetingRoom meetingRoom = meetingRoomList.getFirst();
                            meetingRecordVO.setMeetingRoomName(meetingRoom.getRoomName());
                            meetingRecordVO.setLocation(meetingRoom.getLocation());
                        }
                        //插入会议创建人信息
                        SysUser user = userService.getOne(new LambdaQueryWrapper<SysUser>()
                                .eq(SysUser::getUserId, meetingRecord.getCreatedBy()));
                        if (user != null) {
                            meetingRecordVO.setAdminUserName(user.getUserName());
                        }
                        //插入参会人信息
                        meetingRecordVO.setAttendees(String.valueOf(attendees));
                        meetingRecordVO.setUsers(users);
                        meetingRecordVO.setMeetingNumber(users.size());
                    }
                    return meetingRecordVO;
                }).filter(Objects::nonNull)
                .filter(meetingRecordVO -> meetingRecordVO.getId() != null)
                .toList();

    }

    /**
     * @return {@code Integer}
     * @description 查询今日中心会议总次数
     */
    @Override
    public Integer getRecordNumber () {
        return Math.toIntExact(this.baseMapper.selectCount(
                new LambdaQueryWrapper<MeetingRecord>()
                        .between(MeetingRecord::getStartTime, LocalDateTime.now().toLocalDate().atStartOfDay()
                                , LocalDateTime.now().toLocalDate().atTime(23, 59, 59))));
    }

    /**
     * @param meetingRecord 会议记录
     * @return {@code MeetingRecord}
     * @description 更新会议状态
     */
    @Override
    public MeetingRecord updateRecordStatus (MeetingRecord meetingRecord) {
        if (meetingRecord == null) {
            return null;
        }
        if (meetingRecord.getStartTime().isBefore(LocalDateTime.now())
                && meetingRecord.getEndTime().isAfter(LocalDateTime.now())) {
            //会议进行中
            meetingRecord.setStatus(MEETING_RECORD_STATUS_PROCESSING);
            this.updateById(meetingRecord);
        } else if (meetingRecord.getEndTime().isBefore(LocalDateTime.now())) {
            //会议已结束
            meetingRecord.setStatus(MEETING_RECORD_STATUS_END);
            this.updateById(meetingRecord);
        }
        return meetingRecord;
    }

    /**
     * @description 更新今日所有会议状态
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
        recordQueryWrapper
                .and(queryWrapper -> queryWrapper
                        .eq(MeetingRecord::getIsDeleted, NOT_DELETED));
        //展示未取消的会议
        recordQueryWrapper
                .and(queryWrapper -> queryWrapper.
                        eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_NOT_START)
                        .or().eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_PROCESSING)
                        .or().eq(MeetingRecord::getStatus, MEETING_RECORD_STATUS_END));
        recordQueryWrapper.orderByDesc(MeetingRecord::getStartTime);
        //获取当天所有会议
        List<MeetingRecord> meetingRecords = this.list(recordQueryWrapper);
        //更新
        meetingRecords.forEach(this::updateRecordStatus);
    }

    /**
     * @param userId 用户id
     * @return {@code List<MeetingRecordVO>}
     * @description 分页获取用户参与的所有会议
     */
    @Override
    public List<MeetingRecordVO> getAllRecordVoListPage (String userId, Long pageNum, Long pageSize) {
        // 检查参数
        if (pageNum == null || pageSize == null || pageNum < 1 || userId == null) {
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0410);
        }
        //查询用户参与的所有会议记录ID
        List<MeetingAttendees> meetingAttendees = attendeesMapper
                .selectList(new LambdaQueryWrapper<MeetingAttendees>()
                        .eq(MeetingAttendees::getUserId, userId));
        List<Long> recordIds = meetingAttendees.stream()
                .map(MeetingAttendees::getMeetingRecordId)
                .collect(Collectors.toList());
        // 若无参与会议，则返回空列表
        if (recordIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 查询会议记录信息
        Page<MeetingRecord> meetingRecordPage = this.baseMapper
                .selectPage(new Page<>(pageNum, pageSize)
                        , new LambdaQueryWrapper<MeetingRecord>()
                                .in(MeetingRecord::getId, recordIds)
                                .ne(MeetingRecord::getStatus, MEETING_RECORD_STATUS_CANCEL)
                                .orderByDesc(MeetingRecord::getStartTime));
        // 构建MeetingRecordVO列表
        return meetingRecordPage.getRecords().stream().map(record -> {
            MeetingRecordVO recordVO = new MeetingRecordVO();
            record = updateRecordStatus(record);
            // 设置会议信息
            BeanUtils.copyProperties(record, recordVO);
            // 设置会议室信息
            //不使用逻辑删除
            MeetingRoom meetingRoom = meetingRoomMapper.getByRoomId(record.getMeetingRoomId());
            recordVO.setMeetingRoomName(meetingRoom.getRoomName());
            recordVO.setLocation(meetingRoom.getLocation());
            // 设置创建人信息
            SysUser adminUser = userService.getOne(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getUserId, record.getCreatedBy()));
            if (adminUser != null) {
                recordVO.setAdminUserName(adminUser.getUserName());
            }
            // 设置参会人信息
            List<String> userIds = attendeesMapper.selectList(
                            new LambdaQueryWrapper<MeetingAttendees>()
                                    .eq(MeetingAttendees::getMeetingRecordId, record.getId()))
                    .stream().map(MeetingAttendees::getUserId).collect(Collectors.toList());
            StringBuffer attendees = new StringBuffer();
            ArrayList<SysUserVO> users = new ArrayList<>();
            userService.getUserInfo(userIds, attendees, users);
            recordVO.setAttendees(attendees.toString());
            recordVO.setUsers(users);
            recordVO.setMeetingNumber(users.size());
            return recordVO;
        }).collect(Collectors.toList());
    }

    /**
     * @param pageNum      页码
     * @param pageSize     每页显示条数
     * @param currentLevel 当前登录用户的权限等级
     * @return com.jfzt.meeting.common.Result<java.util.List < com.jfzt.meeting.entity.vo.MeetingRecordVO>>
     * @description 查询所有会议记录
     */
    @Override
    public List<MeetingRecordVO> getAllMeetingRecordVoListPage (Long pageNum, Long pageSize, Integer currentLevel) {
        // 获取当前登录用户的权限等级
        if (SUPER_ADMIN_LEVEL.equals(currentLevel) || ADMIN_LEVEL.equals(currentLevel)) {
            // 检查参数
            if (pageNum == null || pageSize == null) {
                throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0410);
            }
            // 查询所有会议记录
            Page<MeetingRecord> meetingRecordPage = this.baseMapper.selectPage(new Page<>(pageNum, pageSize)
                    , new LambdaQueryWrapper<MeetingRecord>()
                            .notIn(MeetingRecord::getStatus, MEETING_RECORD_STATUS_CANCEL)
                            .orderByDesc(MeetingRecord::getStartTime));
            // 构建MeetingRecordVO列表
            return meetingRecordPage.getRecords().stream().map(record -> {
                MeetingRecordVO recordVO = new MeetingRecordVO();
                record = updateRecordStatus(record);
                // 设置会议信息
                BeanUtils.copyProperties(record, recordVO);
                // 设置会议室信息
                // 不使用逻辑删除
                MeetingRoom meetingRoom = meetingRoomMapper.getByRoomId(record.getMeetingRoomId());
                if (meetingRoom != null){
                    recordVO.setMeetingRoomName(meetingRoom.getRoomName());
                }
                // 设置创建人信息
                SysUser adminUser = userService.getById(record.getCreatedBy());
                if (adminUser != null) {
                    recordVO.setAdminUserName(adminUser.getUserName());
                }
                // 设置参会人信息
                List<String> userIds = attendeesMapper.selectList(
                                new LambdaQueryWrapper<MeetingAttendees>()
                                        .eq(MeetingAttendees::getMeetingRecordId, record.getId()))
                        .stream().map(MeetingAttendees::getUserId).collect(Collectors.toList());
                StringBuffer attendees = new StringBuffer();
                ArrayList<SysUserVO> users = new ArrayList<>();
                userService.getUserInfo(userIds, attendees, users);
                // 设置参会人员详情
                recordVO.setAttendees(attendees.toString());
                recordVO.setUsers(users);
                recordVO.setMeetingNumber(users.size());
                return recordVO;
            }).sorted(Comparator.comparing(MeetingRecordVO::getStartTime).reversed()).collect(Collectors.toList());
        }
        throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0301);
    }

    /**
     * @param userId    用户id
     * @param meetingId 会议id
     * @return {@code Result<String>}
     * @description 根据会议记录id删除会议(首页不展示非删除)
     */
    @Override
    public Result<String> deleteMeetingRecord (String userId, Long meetingId) {
        if (userId == null || meetingId == null) {
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0410);
        }
        //查询会议记录
        MeetingRecord meetingRecord = this.baseMapper.selectById(meetingId);
        updateRecordStatus(meetingRecord);
        meetingRecord = this.baseMapper.selectById(meetingId);
        if (meetingRecord.getStatus().equals(MEETING_RECORD_STATUS_NOT_START)) {
            throw new RRException("当前会议状态无法删除！", ErrorCodeEnum.SERVICE_ERROR_A0400.getCode());
        }
        if (!meetingRecord.getCreatedBy().equals(userId)) {
            //会议创建人才可以删除会议
            throw new RRException("当前用户没有删除权限！", ErrorCodeEnum.SERVICE_ERROR_A0400.getCode());
        }
        //判断用户是否为参会人
        List<String> userIds = attendeesMapper.selectList(
                        new LambdaQueryWrapper<MeetingAttendees>()
                                .eq(MeetingAttendees::getMeetingRecordId, meetingId))
                .stream().map(MeetingAttendees::getUserId).toList();
        if (userIds.contains(userId)) {
            //删除
            meetingRecord.setIsDeleted(IS_DELETED);
            this.baseMapper.updateById(meetingRecord);
            return Result.success();
        }
        throw new RRException("当前用户没有删除权限！", ErrorCodeEnum.SERVICE_ERROR_A0400.getCode());
    }

    /**
     * @param userId    用户id
     * @param meetingId 会议id
     * @return {@code Boolean}
     * @description 根据会议记录id取消会议
     */
    @Transactional
    @Override
    public Result<String> cancelMeetingRecord (String userId, Long meetingId) {
        if (userId == null || meetingId == null) {
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0410);
        }
        //查询会议记录
        MeetingRecord meetingRecord = this.baseMapper.selectById(meetingId);
        updateRecordStatus(meetingRecord);
        meetingRecord = this.baseMapper.selectById(meetingId);
        //更新会议状态
        if (meetingRecord == null) {
            throw new RRException("会议不存在！", ErrorCodeEnum.SERVICE_ERROR_A0400.getCode());
        }
        //会议不是未开始状态或者会议创建人不是当前用户无法取消
        if (!Objects.equals(meetingRecord.getStatus(), MEETING_RECORD_STATUS_NOT_START)) {
            throw new RRException("会议当前状态不可取消！", ErrorCodeEnum.SERVICE_ERROR_A0400.getCode());
        }
        if (!meetingRecord.getCreatedBy().equals(userId)) {
            throw new RRException("当前用户没有修改权限！", ErrorCodeEnum.SERVICE_ERROR_A0400.getCode());
        }
        meetingRecord.setStatus(MEETING_RECORD_STATUS_CANCEL);
        this.baseMapper.updateById(meetingRecord);
        attendeesMapper.delete(new LambdaQueryWrapper<MeetingAttendees>()
                .eq(MeetingAttendees::getMeetingRecordId, meetingId));
        //清除会议纪要
        meetingMinutesService.deleteMeetingMinutes(meetingId);
        return Result.success();
    }

    /**
     * @param meetingRecordDTO 会议记录DTO
     * @return com.jfzt.meeting.common.Result<java.util.Objects>
     * @Description 新增会议
     */
    @Override
    @Transactional
    public Result<Objects> addMeeting (MeetingRecordDTO meetingRecordDTO) {
        // 创建一个新的MeetingRecord对象
        MeetingRecord meetingRecord = new MeetingRecord();
        // 将meetingRecordDTO中的属性复制到meetingRecord中
        BeanUtils.copyProperties(meetingRecordDTO, meetingRecord);
        // 判断meetingRecord的结束时间是否早于开始时间，如果是，则返回错误信息
        if (meetingRecord.getEndTime().isBefore(meetingRecord.getStartTime())) {
            throw new RRException(START_TIME_GT_END_TiME);
        } else if (meetingRecord.getStartTime().isBefore(LocalDateTime.now())) {
            // 判断meetingRecord的开始时间是否早于当前时间，如果是，则返回错误信息
            throw new RRException(START_TIME_LT_NOW);
        }
        //根据时间段查看是否有会议占用
        LambdaQueryWrapper<MeetingRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.notIn(MeetingRecord::getStatus, MEETING_RECORD_STATUS_CANCEL)
                .eq(MeetingRecord::getMeetingRoomId, meetingRecord.getMeetingRoomId());
        //开始时间或结束时间在时间段内 或 开始时间与结束时间之间包含时间段
        queryWrapper.and(recordQueryWrapper -> recordQueryWrapper
                //开始时间在时间段内 前含后不含  8-9 属于8-8.5不属于7.5-8
                .between(MeetingRecord::getStartTime, meetingRecord.getStartTime(),
                        meetingRecord.getEndTime().minusSeconds(1))
                //结束时间在时间段内 前不含后含  8-9属于8.5-9不属于9-9.5
                .or().between(MeetingRecord::getEndTime, meetingRecord.getStartTime().plusSeconds(1),
                        meetingRecord.getEndTime())
                //时间段包含在开始时间(含)和结束时间(含)之间    8-9 属于8-8.5属于8.5-9
                .or().lt(MeetingRecord::getStartTime, meetingRecord.getStartTime().plusSeconds(1))
                .gt(MeetingRecord::getEndTime, meetingRecord.getEndTime().minusSeconds(1)));
        List<MeetingRecord> meetingRecords = list(queryWrapper);
        if (!meetingRecords.isEmpty()) {
            throw new RRException(OCCUPIED, ErrorCodeEnum.SERVICE_ERROR_A0400.getCode());
        }
        // 保存meetingRecord
        save(meetingRecord);
        // 创建一个MeetingAttendees的列表，并将meetingRecordDTO中的用户信息转换为MeetingAttendees对象
        List<MeetingAttendees> attendeesList = meetingRecordDTO.getUsers().stream()
                .map(user -> MeetingAttendees
                        .builder()
                        .userId(user.getUserId())
                        .meetingRecordId(meetingRecord.getId()).build())
                .collect(Collectors.toList());
        // 保存MeetingAttendees列表
        meetingAttendeesService.saveBatch(attendeesList);


        return Result.success(CREATE_SUCCESS);
    }


    /**
     * @return com.jfzt.meeting.common.Result<java.util.List < com.jfzt.meeting.entity.vo.MeetingRecordVO>>
     * @Description 更新会议
     * @Param [meetingRecordDTO]
     */
    @Override
    @Transactional
    public Result<List<MeetingRecordVO>> updateMeeting (MeetingRecordDTO meetingRecordDTO) {
        // 创建一个新的MeetingRecord对象
        MeetingRecord meetingRecord = new MeetingRecord();
        // 将meetingRecordDTO中的属性复制到meetingRecord中
        BeanUtils.copyProperties(meetingRecordDTO, meetingRecord);
        // 判断meetingRecord的结束时间是否早于开始时间，如果是，则返回错误信息
        if (meetingRecord.getEndTime().isBefore(meetingRecord.getStartTime())) {
            return Result.fail(START_TIME_GT_END_TiME);
        } else if (meetingRecord.getStartTime().isBefore(LocalDateTime.now())) {
            // 判断meetingRecord的开始时间是否早于当前时间，如果是，则返回错误信息
            return Result.fail(START_TIME_LT_NOW);
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
                .map(user -> MeetingAttendees
                        .builder()
                        .userId(user.getUserId())
                        .meetingRecordId(meetingRecord.getId())
                        .build())
                .collect(Collectors.toList());
        // 更新MeetingAttendees列表
        meetingAttendeesService.saveBatch(attendeesList);
        return Result.success(UPDATE_SUCCESS);
    }

    /**
     * @Description 统计七日内各时间段预约频率
     * @return com.jfzt.meeting.common.Result<java.util.List<com.jfzt.meeting.entity.vo.PeriodTimesVO>>
     */
    @Override
    public Result<List<PeriodTimesVO>> getTimePeriodTimes () {
        ArrayList<PeriodTimesVO> list = new ArrayList<>();
        LocalDateTime startTime = LocalDateTime.now().toLocalDate().atStartOfDay().plusHours(9);
        LocalDateTime endTime = startTime.plusMinutes(30);
        LocalDateTime startTimeAll = LocalDateTime.now().toLocalDate().atStartOfDay().minusDays(7);
        LocalDateTime endTimeAll = LocalDateTime.now().toLocalDate().atStartOfDay();
        List<MeetingRecord> records = lambdaQuery().between(MeetingRecord::getGmtCreate, startTimeAll, endTimeAll).list();
        for (int i = 0; i < 18; i++) {

            LocalDateTime finalStartTime = startTime;
            LocalDateTime finalEndTime = endTime;
            long count = records.stream().map(record -> {

                LocalTime startTimes = record.getStartTime().toLocalTime();
                LocalTime endTimes = record.getEndTime().toLocalTime();
                if (finalStartTime.plusMinutes(1).toLocalTime().isAfter(startTimes)
                        && finalStartTime.toLocalTime().isBefore(endTimes) ||
                        finalEndTime.plusMinutes(1).toLocalTime().isAfter(startTimes)
                                && finalEndTime.toLocalTime().isBefore(endTimes) ||
                        finalStartTime.plusMinutes(1).toLocalTime().isAfter(startTimes)
                                && finalEndTime.minusMinutes(1).toLocalTime().isBefore(endTimes)
                ) {
                    return record;
                }
                return null;
            }).filter(Objects::nonNull).count();

            list.add(PeriodTimesVO.builder()
                    .count(count)
                    .startTime(startTime)
                    .endTime(endTime)
                    .build());
            startTime = startTime.plusMinutes(30);

            endTime = endTime.plusMinutes(30);
        }
        list.sort((o1, o2) -> Math.toIntExact(o2.getCount() - o1.getCount()));
        return Result.success(list);
    }


}




