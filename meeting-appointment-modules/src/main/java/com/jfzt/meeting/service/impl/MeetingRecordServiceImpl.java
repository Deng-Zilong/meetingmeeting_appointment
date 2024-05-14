package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.constant.MeetingRecordStatusConstant;
import com.jfzt.meeting.constant.MessageConstant;
import com.jfzt.meeting.entity.MeetingAttendees;
import com.jfzt.meeting.entity.MeetingRecord;
import com.jfzt.meeting.entity.MeetingRoom;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.dto.MeetingRecordDTO;
import com.jfzt.meeting.entity.vo.MeetingRecordVO;
import com.jfzt.meeting.entity.vo.SysUserVO;
import com.jfzt.meeting.exception.ErrorCodeEnum;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.mapper.MeetingAttendeesMapper;
import com.jfzt.meeting.mapper.MeetingRecordMapper;
import com.jfzt.meeting.service.MeetingAttendeesService;
import com.jfzt.meeting.service.MeetingRecordService;
import com.jfzt.meeting.service.MeetingRoomService;
import com.jfzt.meeting.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.jfzt.meeting.constant.IsDeletedConstant.IS_DELETED;
import static com.jfzt.meeting.constant.IsDeletedConstant.NOT_DELETED;
import static com.jfzt.meeting.constant.MeetingRecordStatusConstant.*;
import static com.jfzt.meeting.constant.MessageConstant.START_TIME_GT_END_TiME;
import static com.jfzt.meeting.constant.MessageConstant.START_TIME_LT_NOW;

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
    @Resource
    private MeetingRoomService meetingRoomService;
    @Resource
    private SysUserService userService;
    @Resource
    private MeetingAttendeesMapper attendeesMapper;


    /**
     * 获取当天用户参与的所有会议
     *
     * @param userId 用户id
     * @return {@code List<MeetingRecordVO>}
     */
    @Override
    public List<MeetingRecordVO> getRecordVoList (String userId) {
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
        recordQueryWrapper
                .and(wq -> wq
                        //删除的不展示
                        .eq(MeetingRecord::getIsDeleted, NOT_DELETED));
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
            if (meetingRecord == null) {
                return null;
            }
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
            ArrayList<SysUserVO> sysUserVOList = new ArrayList<>();
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
                SysUserVO sysUserVO = new SysUserVO();
                sysUserVO.setUserName(user.getUserName());
                sysUserVO.setUserId(user.getUserId());
                sysUserVOList.add(sysUserVO);

                attendees.append(user.getUserName());
                if (attendeesList.indexOf(attendee) != attendeesList.size() - 1) {
                    attendees.append(",");
                }

            });
            //当前用户参与会议
            if (userIdList.contains(userId)) {
                //更新会议状态
                updateRecordStatus(meetingRecord);
                meetingRecord = this.getById(meetingRecord.getId());
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
                meetingRecordVO.setUsers(sysUserVOList);
                meetingRecordVO.setMeetingNumber(userIdList.size());
            }
            return meetingRecordVO;
        }).filter(Objects::nonNull).filter(meetingRecordVO -> meetingRecordVO.getId() != null).toList();

    }

    private StringBuffer getStringBuffer (List<String> userIds) {
        HashSet<String> userIdSet = new LinkedHashSet<>(userIds);
        ArrayList<String> userIdList = new ArrayList<>(userIdSet);
        StringBuffer attendees = new StringBuffer();
        userIdList.forEach(userId -> {
            SysUser user = userService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserId, userId));
            if (user != null) {
                //拼接参会人姓名
                attendees.append(user.getUserName());
                if (userIdList.indexOf(userId) < userIdList.size() - 1) {
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
        if (meetingRecord == null) {
            return;
        }
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
        recordQueryWrapper.and(queryWrapper -> queryWrapper.eq(MeetingRecord::getIsDeleted, NOT_DELETED));
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
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0410);
        }
        //通过参会表查询用户参与的所有会议id
        Page<MeetingAttendees> meetingAttendeesPage = attendeesMapper
                .selectPage(new Page<>(pageNum, pageSize)
                        , new LambdaQueryWrapper<MeetingAttendees>()
                                .eq(MeetingAttendees::getUserId, userId)
                                .orderByAsc(MeetingAttendees::getGmtModified));
        List<Long> recordIds = meetingAttendeesPage.getRecords().stream().map(MeetingAttendees::getMeetingRecordId).sorted().toList();
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
                    ArrayList<SysUserVO> users = new ArrayList<>();
                    userIds.forEach(id -> {
                        List<SysUser> userList = userService.list(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserId, userId));
                        SysUser user = userList.getFirst();
                        SysUserVO userVO = new SysUserVO();
                        BeanUtils.copyProperties(user, userVO);
                        users.add(userVO);
                    });
                    //更新会议状态
                    updateRecordStatus(meetingRecord);
                    meetingRecord = this.baseMapper.selectById(recordId);
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
                    meetingRecordVO.setAttendees(attendees.toString());
                    meetingRecordVO.setUsers(users);
                    meetingRecordVO.setMeetingNumber(userIds.size());
                    return meetingRecordVO;
                }).filter(Objects::nonNull)
                .sorted(Comparator.comparing(MeetingRecordVO::getStartTime).reversed())
                .toList();
    }


    /**
     * @param pageNum      页码
     * @param pageSize     每页显示条数
     * @param currentLevel 当前登录用户的权限等级
     * @return com.jfzt.meeting.common.Result<java.util.List < com.jfzt.meeting.entity.vo.MeetingRecordVO>>
     * @description 查询所有会议记录
     */
    @Override
    public Result<List<MeetingRecordVO>> getAllMeetingRecordVoListPage (Long pageNum, Long pageSize, Integer currentLevel) {
        // 获取当前登录用户的权限等级
        if (MessageConstant.SUPER_ADMIN_LEVEL.equals(currentLevel) || MessageConstant.ADMIN_LEVEL.equals(currentLevel)) {
            if (pageNum == null || pageSize == null) {
                log.error("请求必填参数为空" + ErrorCodeEnum.SERVICE_ERROR_A0410);
                throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0410);
            }
            // 查询出来所有的会议记录
            Page<MeetingRecord> meetingRecordPage = this.baseMapper.selectPage(new Page<>(pageNum, pageSize), new QueryWrapper<>());
            // 查询出所有的会议预约的id
            List<Long> longList = meetingRecordPage.getRecords().stream().map(MeetingRecord::getId).toList();
            if (longList.isEmpty()) {
                return null;
            }
            return Result.success(longList.stream().map(id -> {
                // 通过会议记录id获取到参会人员的信息
                List<String> userIds = attendeesMapper.selectUserIdsByRecordId(id);
                StringBuffer attendees = getStringBuffer(userIds);
                ArrayList<SysUserVO> users = new ArrayList<>();
                userIds.forEach(item -> {
                    List<SysUser> userList = userService.list(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserId, item));
                    SysUser user = userList.getFirst();
                    SysUserVO userVO = new SysUserVO();
                    BeanUtils.copyProperties(user, userVO);
                    users.add(userVO);
                });
                //更新会议状态
                MeetingRecord meetingRecord = this.baseMapper.selectById(id);
                updateRecordStatus(meetingRecord);
                meetingRecord = this.baseMapper.selectById(id);
                //插入会议信息
                MeetingRecordVO meetingRecordVO = new MeetingRecordVO();
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
                meetingRecordVO.setAttendees(attendees.toString());
                meetingRecordVO.setUsers(users);
                meetingRecordVO.setMeetingNumber(userIds.size());
                return meetingRecordVO;
            }).sorted((o1, o2) -> o1.getStartTime().isBefore(o2.getStartTime()) ? 1 : -1).toList());
        }
        return Result.fail(ErrorCodeEnum.SERVICE_ERROR_A0301);
    }


    /**
     * 根据会议记录id取消会议
     *
     * @param userId    用户id
     * @param meetingId 会议id
     * @return {@code Boolean}
     */
    @Override
    public Result<String> deleteMeetingRecord (String userId, Long meetingId) {
        //查询会议记录
        MeetingRecord meetingRecord = this.baseMapper.selectById(meetingId);
        updateRecordStatus(meetingRecord);
        meetingRecord = this.baseMapper.selectById(meetingId);
        if (meetingRecord.getStatus().equals(MEETING_RECORD_STATUS_NOT_START)) {
            throw new RRException("当前会议状态无法删除！", ErrorCodeEnum.SERVICE_ERROR_A0400.getCode());
        }
        //判断用户是否为参会人
        List<String> userIds = attendeesMapper.selectUserIdsByRecordId(meetingId);
        if (userIds.contains(userId)) {
            //删除
            meetingRecord.setIsDeleted(IS_DELETED);
            this.baseMapper.updateById(meetingRecord);
            return Result.success();
        }
        throw new RRException("当前用户没有删除权限！", ErrorCodeEnum.SERVICE_ERROR_A0400.getCode());
    }

    /**
     * 根据会议记录id取消会议
     *
     * @param userId    用户id
     * @param meetingId 会议id
     * @return {@code Boolean}
     */
    @Override
    public Result<String> cancelMeetingRecord (String userId, Long meetingId) {

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
        return Result.success();
    }

    /**
     * @return com.jfzt.meeting.common.Result<java.util.Objects>
     * @Description 新增会议
     * @Param [meetingRecordDTO]
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
            return Result.fail(START_TIME_GT_END_TiME);
        } else if (meetingRecord.getStartTime().isBefore(LocalDateTime.now())) {
            // 判断meetingRecord的开始时间是否早于当前时间，如果是，则返回错误信息
            return Result.fail(START_TIME_LT_NOW);
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




