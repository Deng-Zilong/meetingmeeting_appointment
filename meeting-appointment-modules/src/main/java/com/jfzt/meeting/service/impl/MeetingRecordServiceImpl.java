package com.jfzt.meeting.service.impl;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.*;
import com.jfzt.meeting.entity.dto.MeetingRecordDTO;
import com.jfzt.meeting.entity.vo.*;
import com.jfzt.meeting.exception.ErrorCodeEnum;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.mapper.*;
import com.jfzt.meeting.poiWord.DynWordUtils;
import com.jfzt.meeting.service.*;
import com.jfzt.meeting.task.MeetingReminderScheduler;
import com.jfzt.meeting.utils.WxUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;

import jakarta.servlet.http.HttpServletResponse;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.jfzt.meeting.constant.IsDeletedConstant.IS_DELETED;
import static com.jfzt.meeting.constant.IsDeletedConstant.NOT_DELETED;
import static com.jfzt.meeting.constant.MeetingRecordStatusConstant.*;
import static com.jfzt.meeting.constant.MeetingRoomStatusConstant.MEETINGROOM_STATUS_PAUSE;
import static com.jfzt.meeting.constant.MessageConstant.*;
import static com.jfzt.meeting.utils.ExcelUtil.InsertRow;

/**
 * 针对表【meeting_record(会议记录表)】的数据库操作Service实现
 *
 * @author zilong.deng
 * @since 2024-04-28 11:47:39
 */
@Slf4j
@Service
public class MeetingRecordServiceImpl extends ServiceImpl<MeetingRecordMapper, MeetingRecord>
        implements MeetingRecordService {

    @Resource
    private WxUtil wxUtil;
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
    @Resource
    private MeetingReminderScheduler meetingReminderScheduler;
    @Resource
    private MeetingMinutesService meetingMinutesService;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysDepartmentMapper sysDepartmentMapper;
    @Autowired
    private MeetingMinutesMapper meetingMinutesMapper;
    @Autowired
    private MeetingWordMapper meetingWordMapper;


    /**
     * 获取当天用户参与的所有会议
     *
     * @param userId 用户id
     * @return 会议记录VO
     */
    @Override
    public List<MeetingRecordVO> getTodayMeetingRecord(String userId) {
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
            List<String> attendeesIds = attendeesList.stream().map(MeetingAttendees::getUserId).toList();
            List<String> userIds = new ArrayList<>();
            if (!attendeesIds.isEmpty()) {
                userIds.addAll(attendeesIds);
            }
            //当前用户参与会议
            if (userIds.contains(userId)) {
                List<SysUserVO> users = new ArrayList<>();
                StringBuffer attendees = new StringBuffer();
                //遍历参会人，拼接姓名，获取userIdList
                userIds.addFirst(meetingRecord.getCreatedBy());
                userService.getUserInfo(userIds, attendees, users);
                //更新会议状态
                updateRecordStatus(meetingRecord);
                meetingRecord = this.getById(meetingRecord.getId());
                //插入会议信息
                BeanUtils.copyProperties(meetingRecord, meetingRecordVO);
                //插入会议室信息,包括被删除的会议室
                MeetingRoom meetingRoom = meetingRoomMapper.getByRoomId(meetingRecord.getMeetingRoomId());
                if (meetingRoom != null) {
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
     * 查询今日中心会议总次数
     *
     * @return 会议总次数
     */
    @Override
    public Integer getRecordNumber() {
        return Math.toIntExact(this.baseMapper.selectCount(
                new LambdaQueryWrapper<MeetingRecord>()
                        .between(MeetingRecord::getStartTime, LocalDateTime.now().toLocalDate().atStartOfDay()
                                , LocalDateTime.now().toLocalDate().atTime(23, 59, 59))
                        .ne(MeetingRecord::getStatus, MEETING_RECORD_STATUS_CANCEL)));
    }

    /**
     * 更新会议状态
     *
     * @param meetingRecord 会议记录
     * @return 会议记录
     */
    @Override
    public MeetingRecord updateRecordStatus(MeetingRecord meetingRecord) {
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
     * 更新今日所有会议状态
     */
    @Override
    public void updateTodayRecordStatus() {
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
     * 分页获取用户参与的所有会议
     *
     * @param userId 用户id
     * @return 会议记录列表
     */
    @Override
    public List<MeetingRecordVO> getAllRecordVoListPage(String userId, Long pageNum, Long pageSize) {
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
                    .stream().map(MeetingAttendees::getUserId).distinct().collect(Collectors.toList());
            //创建人排在首位
            userIds.addFirst(record.getCreatedBy());
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
     * 查询所有会议记录
     *
     * @param pageNum      页码
     * @param pageSize     每页显示条数
     * @param currentLevel 当前登录用户的权限等级
     * @return MeetingRecordVO列表
     */
    @Override
    public List<MeetingRecordVO> getAllMeetingRecordVoListPage(Long pageNum, Long pageSize, Integer currentLevel) {
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
                if (meetingRoom != null) {
                    recordVO.setMeetingRoomName(meetingRoom.getRoomName());
                }
                // 设置参会人信息
                List<String> userIds = attendeesMapper.selectList(
                        new LambdaQueryWrapper<MeetingAttendees>()
                                .eq(MeetingAttendees::getMeetingRecordId, record.getId()))
                        .stream().map(MeetingAttendees::getUserId).collect(Collectors.toList());
                StringBuffer attendees = new StringBuffer();
                ArrayList<SysUserVO> users = new ArrayList<>();
                userIds.addFirst(record.getCreatedBy());
                userService.getUserInfo(userIds, attendees, users);
                // 设置参会人员详情
                recordVO.setAttendees(attendees.toString());
                recordVO.setUsers(users);
                recordVO.setMeetingNumber(users.size());
                return recordVO;
            }).sorted(Comparator.comparing(MeetingRecordVO::getStartTime).reversed())
                    .collect(Collectors.toList());
        }
        throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0301);
    }

    /**
     * 根据会议记录id删除会议(首页不展示非删除)
     *
     * @param userId    用户id
     * @param meetingId 会议id
     * @return 删除结果
     */
    @Override
    public Result<String> deleteMeetingRecord(String userId, Long meetingId) {
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
     * 根据会议记录id取消会议
     *
     * @param userId    用户id
     * @param meetingId 会议id
     * @return 取消结果
     */
    @Transactional
    @Override
    public Result<String> cancelMeetingRecord(String userId, Long meetingId) {
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
        List<String> userIds = attendeesMapper.selectList(new LambdaQueryWrapper<MeetingAttendees>()
                .eq(MeetingAttendees::getMeetingRecordId, meetingId))
                .stream().map(MeetingAttendees::getUserId).toList();
        this.baseMapper.updateById(meetingRecord);
        attendeesMapper.delete(new LambdaQueryWrapper<MeetingAttendees>()
                .eq(MeetingAttendees::getMeetingRecordId, meetingId));
        //取消会议提醒
        meetingReminderScheduler.cancelMeetingReminder(meetingId);
        //清除会议纪要
        meetingMinutesService.deleteMeetingMinutes(meetingId);
        MeetingRoom meetingRoom = meetingRoomService.getById(meetingRecord.getMeetingRoomId());
        SysUser sysUser = sysUserMapper.selectByUserId(meetingRecord.getCreatedBy());
        String reminder =
                "#### 会议提醒\n" +
                        "**会议主题 :** " + meetingRecord.getTitle() + "\n" +
                        "**发起人 :** " + sysUser.getUserName() + "\n" +
                        "**日期 :** " + meetingRecord.getStartTime().format(DateTimeFormatter.ofPattern("yyyy 年 MM 月 dd 日"))
                        + "\n" +
                        "**时间 :** " + meetingRecord.getStartTime().format(DateTimeFormatter.ofPattern("HH : mm"))
                        + " ~ "
                        + meetingRecord.getEndTime().format(DateTimeFormatter.ofPattern("HH : mm")) + "\n" +
                        "**会议室 :** " + meetingRoom.getRoomName() + "\n" +
                        "**地点 :** " + meetingRoom.getLocation() + "\n" +
                        "<font color=\"comment\">**会议已取消！** </font>";
        //发送取消会议提醒
        wxUtil.sendsWxReminders(userIds, reminder);
        return Result.success();
    }

    /**
     * 新增会议
     *
     * @param meetingRecordDTO 会议记录DTO
     * @return 新增会议结果
     */

    @Override
    @Transactional
    public Result<Objects> addMeeting(MeetingRecordDTO meetingRecordDTO) {
        // 创建一个新的MeetingRecord对象
        MeetingRecord meetingRecord = new MeetingRecord();
        // 将meetingRecordDTO中的属性复制到meetingRecord中
        BeanUtils.copyProperties(meetingRecordDTO, meetingRecord);
        // 判断meetingRecord的结束时间是否早于开始时间，如果是，则返回错误信息
        if (meetingRecord.getEndTime().isBefore(meetingRecord.getStartTime())
                || meetingRecord.getStartTime().equals(meetingRecord.getEndTime())) {
            throw new RRException(START_TIME_GT_END_TIME);
        } else if (meetingRecord.getStartTime().isBefore(LocalDateTime.now())) {
            // 判断meetingRecord的开始时间是否早于当前时间，如果是，则返回错误信息
            throw new RRException(START_TIME_LT_NOW);
        }
        //会议室是否被禁用
        MeetingRoom meetingRoom = meetingRoomService.getById(meetingRecord.getMeetingRoomId());
        if (Objects.equals(meetingRoom.getStatus(), MEETINGROOM_STATUS_PAUSE)) {
            throw new RRException("会议室不可用", ErrorCodeEnum.SERVICE_ERROR_A0400.getCode());
        }
        //根据时间段查看是否有会议占用
        LambdaQueryWrapper<MeetingRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.notIn(MeetingRecord::getStatus, MEETING_RECORD_STATUS_CANCEL)
                .eq(MeetingRecord::getMeetingRoomId, meetingRecord.getMeetingRoomId());
        //开始时间或结束时间在时间段内 或 开始时间与结束时间之间包含时间段
        queryWrapper.and(recordQueryWrapper -> recordQueryWrapper
                //开始时间在时间段内 前含后不含  8-9 属于8-8.5不属于7.5-8
                .between(MeetingRecord::getStartTime, meetingRecord.getStartTime(), meetingRecord.getEndTime().minusSeconds(1))
                //结束时间在时间段内 前不含后含  8-9属于8.5-9不属于9-9.5
                .or().between(MeetingRecord::getEndTime, meetingRecord.getStartTime().plusSeconds(1), meetingRecord.getEndTime())
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
        meetingRecordDTO.setId(meetingRecord.getId());
        SysUser sysUser = userService
                .getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserId, meetingRecordDTO.getCreatedBy()));
        String reminder =
                "#### 会议提醒\n" +
                        "**会议主题 :** " + meetingRecordDTO.getTitle() + "\n" +
                        "**发起人 :** " + sysUser.getUserName() + "\n" +
                        "**日期 :** " + meetingRecordDTO.getStartTime().format(DateTimeFormatter.ofPattern("yyyy 年 MM 月 dd 日"))
                        + "\n" +
                        "**时间 :** " + meetingRecordDTO.getStartTime().format(DateTimeFormatter.ofPattern("HH : mm"))
                        + " ~ "
                        + meetingRecordDTO.getEndTime().format(DateTimeFormatter.ofPattern("HH : mm")) + "\n" +
                        "**会议室 :** " + meetingRoom.getRoomName() + "\n" +
                        "**地点 :** " + meetingRoom.getLocation() + "\n" +
                        "<font color=\"info\">**会议已预约！** </font>";
        wxUtil.sendsWxReminders(meetingRecordDTO.getUsers().stream().map(SysUser::getUserId).toList(), reminder);
        //定时发送提醒
        meetingReminderScheduler.scheduleMeetingReminder(meetingRecordDTO);
        return Result.success(CREATE_SUCCESS);
    }


    /**
     * 更新会议
     *
     * @param meetingRecordDTO 会议记录DTO
     * @return 会议记录VO
     */
    @Override
    @Transactional
    public Result<List<MeetingRecordVO>> updateMeeting(MeetingRecordDTO meetingRecordDTO) {
        // 创建一个新的MeetingRecord对象
        MeetingRecord meetingRecord = new MeetingRecord();
        // 将meetingRecordDTO中的属性复制到meetingRecord中
        BeanUtils.copyProperties(meetingRecordDTO, meetingRecord);
        // 判断meetingRecord的结束时间是否早于开始时间，如果是，则返回错误信息
        if (meetingRecord.getEndTime().isBefore(meetingRecord.getStartTime())) {
            throw new RRException(START_TIME_GT_END_TIME, ErrorCodeEnum.SERVICE_ERROR_A0400.getCode());
        } else if (meetingRecord.getStartTime().isBefore(LocalDateTime.now())) {
            // 判断meetingRecord的开始时间是否早于当前时间，如果是，则返回错误信息
            throw new RRException(START_TIME_LT_NOW, ErrorCodeEnum.SERVICE_ERROR_A0400.getCode());
        }
        //会议室是否被禁用
        MeetingRoom meetingRoom = meetingRoomService.getById(meetingRecord.getMeetingRoomId());
        if (Objects.equals(meetingRoom.getStatus(), MEETINGROOM_STATUS_PAUSE)) {
            throw new RRException("会议室不可用", ErrorCodeEnum.SERVICE_ERROR_A0400.getCode());
        }
        //根据时间段查看是否有会议占用会议室
        LambdaQueryWrapper<MeetingRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.notIn(MeetingRecord::getStatus, MEETING_RECORD_STATUS_CANCEL)
                .eq(MeetingRecord::getMeetingRoomId, meetingRecord.getMeetingRoomId());
        //开始时间或结束时间在时间段内 或 开始时间与结束时间之间包含时间段
        queryWrapper.and(recordQueryWrapper -> recordQueryWrapper
                //开始时间在时间段内 前含后不含  8-9 属于8-8.5不属于7.5-8
                .between(MeetingRecord::getStartTime, meetingRecord.getStartTime(), meetingRecord.getEndTime().minusSeconds(1))
                //结束时间在时间段内 前不含后含  8-9属于8.5-9不属于9-9.5
                .or().between(MeetingRecord::getEndTime, meetingRecord.getStartTime().plusSeconds(1), meetingRecord.getEndTime())
                //时间段包含在开始时间(含)和结束时间(含)之间    8-9 属于8-8.5属于8.5-9
                .or().lt(MeetingRecord::getStartTime, meetingRecord.getStartTime().plusSeconds(1))
                .gt(MeetingRecord::getEndTime, meetingRecord.getEndTime().minusSeconds(1)));
        List<MeetingRecord> meetingRecords = list(queryWrapper);
        //占用会议为修改的会议则可以修改
        if (!meetingRecords.isEmpty()) {
            if (!Objects.equals(meetingRecords.getFirst().getId(), meetingRecord.getId())) {
                throw new RRException(OCCUPIED, ErrorCodeEnum.SERVICE_ERROR_A0400.getCode());
            }
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
        SysUser sysUser = userService
                .getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserId, meetingRecordDTO.getCreatedBy()));

        String reminder =
                "#### 会议提醒\n" +
                        "**会议主题 :** " + meetingRecordDTO.getTitle() + "\n" +
                        "**发起人 :** " + sysUser.getUserName() + "\n" +
                        "**日期 :** " + meetingRecordDTO.getStartTime().format(DateTimeFormatter.ofPattern("yyyy 年 MM 月 dd 日"))
                        + "\n" +
                        "**时间 :** " + meetingRecordDTO.getStartTime().format(DateTimeFormatter.ofPattern("HH : mm"))
                        + " ~ "
                        + meetingRecordDTO.getEndTime().format(DateTimeFormatter.ofPattern("HH : mm")) + "\n" +
                        "**会议室 :** " + meetingRoom.getRoomName() + "\n" +
                        "**地点 :** " + meetingRoom.getLocation() + "\n" +
                        "<font color=\"info\">**会议信息已变更，请注意！** </font>";
        wxUtil.sendsWxReminders(meetingRecordDTO.getUsers().stream().map(SysUser::getUserId).toList(), reminder);
        // 重新发送定时任务提醒
        meetingReminderScheduler.cancelMeetingReminder(meetingRecordDTO.getId());
        meetingReminderScheduler.scheduleMeetingReminder(meetingRecordDTO);
        return Result.success(UPDATE_SUCCESS);
    }

    /**
     * 统计七日内各时间段预约频率
     *
     * @return 预约频率
     */
    @Override
    public Result<List<PeriodTimesVO>> getTimePeriodTimes() {
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
                if (finalStartTime.plusMinutes(1).toLocalTime().isAfter(startTimes) && finalStartTime.toLocalTime().isBefore(endTimes) ||
                        finalEndTime.plusMinutes(1).toLocalTime().isAfter(startTimes) && finalEndTime.toLocalTime().isBefore(endTimes) ||
                        finalStartTime.plusMinutes(1).toLocalTime().isAfter(startTimes) && finalEndTime.minusMinutes(1).toLocalTime().isBefore(endTimes)
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

    /**
     * 会议创建自动提示最近三次
     *
     * @param userId 用户ID
     * @return 自动提示结果
     */
    public Result<List<MeetingPromptVO>> prompts(String userId) {
        //查询最后一次创建的会议
        List<MeetingRecord> records = lambdaQuery()
                .select(MeetingRecord::getTitle, MeetingRecord::getId, MeetingRecord::getMeetingRoomId)
                .eq(MeetingRecord::getCreatedBy, userId)
                .orderByDesc(MeetingRecord::getGmtCreate)
                .list();
        if (records.size() > 8) {
            List<MeetingPromptVO> list = records
                    .subList(0, 8)
                    .stream()
                    .map(lastMeeting -> {
                        try {
                            getMeetingPromptVO(lastMeeting);
                        } catch (Exception e) {
                            return null;
                        }
                        return getMeetingPromptVO(lastMeeting);
                    })
                    .filter(Objects::nonNull)
                    .toList()
                    .subList(0, 3);
            return Result.success(list);
        } else if (!records.isEmpty()) {
            if (records.size() >= 3) {
                return Result.success(records.stream().map(this::getMeetingPromptVO).toList().subList(0, 3));
            }
            return Result.success(records.stream().map(this::getMeetingPromptVO).toList());
        } else {
            return Result.success();
        }
    }

    /**
     * 会议创建自动提示最近一次
     *
     * @param userId 用户ID
     * @return 自动提示结果
     */
    @Override
    public Result<MeetingPromptVO> prompt(String userId) {
        try {
            MeetingRecord lastMeeting = lambdaQuery()
                    .select(MeetingRecord::getId, MeetingRecord::getMeetingRoomId)
                    .eq(MeetingRecord::getCreatedBy, userId)
                    .orderByDesc(MeetingRecord::getGmtCreate)
                    .list()
                    .getFirst();
            getMeetingPromptVO(lastMeeting);
            return Result.success(getMeetingPromptVO(lastMeeting));
        } catch (Exception e) {
            return Result.success();
        }
    }

    /**
     * @param userId              用户id
     * @param type                导出类型，0表示excel，1表示word
     * @param meetingRecordVOList 会议室历史记录信息
     * @param response            返回respose
     * @throws InvalidFormatException 1
     */
    @Override
    public void getRecordExport(String userId, String type, List<MeetingRecordVO> meetingRecordVOList, HttpServletResponse response) throws IOException, InvalidFormatException {
        //获取excel模板的路径，一个备份一个应用
        File templatePathApplyExcel = new File("./opt/会议纪要.xlsx");
        File templatePathBackupsExcel = new File("./opt/会议纪要备份.xlsx");
        if (!templatePathApplyExcel.exists() || !templatePathBackupsExcel.exists()) {
            log.error("没有找到模板信息");
            throw new RRException(ErrorCodeEnum.SYSTEM_ERROR_B01011);
        }

        //获取word模板的路径应用
        for (MeetingRecordVO meetingRecordVO : meetingRecordVOList) {
            if ("0".equals(type)) {
                //导出excel
                extractedExcel(userId, meetingRecordVO, response, templatePathApplyExcel, templatePathBackupsExcel);
            } else {
                //导出word
                extractedWord(meetingRecordVO, response);
            }
        }

    }

    private void extractedWord(MeetingRecordVO meetingRecordVO, HttpServletResponse response) {
        // 模板全的路径
        String templatePaht = "./opt/保险问答系统Sprint02回顾会议纪要.docx";
        String startHour = meetingRecordVO.getStartTime().getHour()+":";
        String startMinute = meetingRecordVO.getStartTime().getMinute() == 0 ? "00" : String.valueOf(meetingRecordVO.getStartTime().getMinute());
        String endtHour = meetingRecordVO.getEndTime().getHour()+":";
        String endtMinute = meetingRecordVO.getEndTime().getMinute() == 0 ? "00" : String.valueOf(meetingRecordVO.getEndTime().getMinute());
        Map<String, Object> paramMap = new HashMap<>(16);
        // 普通的占位符示例 参数数据结构 {str,str}
        paramMap.put("title", meetingRecordVO.getTitle());
        paramMap.put("time", meetingRecordVO.getStartTime().getYear() + "年" +
                meetingRecordVO.getStartTime().getMonthValue() + "月"
                + meetingRecordVO.getStartTime().getDayOfMonth() + "日  "
                + startHour+startMinute +
                "-" + endtHour+endtMinute);
        paramMap.put("meetingTime", meetingRecordVO.getMeetingRoomName());
        paramMap.put("attendees", meetingRecordVO.getAttendees());
        paramMap.put("meetinggenda", meetingRecordVO.getTitle());

        List<MeetingWord> meetingWords = meetingWordMapper.selectList(new LambdaQueryWrapper<MeetingWord>()
                .eq(MeetingWord::getMeetingRecordId, meetingRecordVO.getId()));

        List<Object> list1 = meetingWords.stream().filter((meetingWord -> meetingWord.getType() == 1))
                .map(MeetingWord::getContent)
                .collect(Collectors.toList());
        paramMap.put("thisGoal", list1);
        List<Object> list2 = meetingWords.stream().filter((meetingWord -> meetingWord.getType() == 2))
                .map(MeetingWord::getContent)
                .collect(Collectors.toList());
        paramMap.put("problem", list2);
        List<Object> list3 = meetingWords.stream().filter((meetingWord -> meetingWord.getType() == 3))
                .map(MeetingWord::getContent)
                .collect(Collectors.toList());
        paramMap.put("optimization", list3);
        List<Object> list4 = meetingWords.stream().filter((meetingWord -> meetingWord.getType() == 4))
                .map(MeetingWord::getContent)
                .collect(Collectors.toList());
        paramMap.put("requirements", list4);

        DynWordUtils.process(paramMap, templatePaht, response, meetingRecordVO.getTitle());
    }

    private void extractedExcel(String userId, MeetingRecordVO meetingRecordVO, HttpServletResponse response, File templatePathApplyExcel, File templatePathBackupsExcel) throws IOException, InvalidFormatException {
        //还原文件
        restoreFile(templatePathBackupsExcel.toString(), templatePathApplyExcel.toString());
        //查看参与用户
        List<SysUserVO> name = meetingRecordVO.getUsers();
        if (name.size() >1){
            //添加用户行
            ExcelWriter writer = ExcelUtil.getWriter(templatePathApplyExcel, "Sheet1");
            int startRow = 11;
            int rows = name.size() - 1;
            XSSFSheet sheets = (XSSFSheet) writer.getSheet();
            InsertRow(writer, startRow, rows, sheets, false);
        }
        //查询其他新增的内容
        MeetingMinutes meetingMinutes = new MeetingMinutes();
        meetingMinutes.setMeetingRecordId(Integer.valueOf(String.valueOf(meetingRecordVO.getId())));
        meetingMinutes.setUserId(meetingRecordVO.getCreatedBy());
        List<MeetingMinutesVO> minutesVOList = meetingMinutesService.getMeetingMinutes(meetingMinutes);
        List<MinutesPlanVO> minutesPlans = new ArrayList<>();
        int sizeOrder = 0;
        if (minutesVOList.size() > 1) {
            minutesPlans = minutesVOList.getFirst().getMinutesPlans();
            //添加"其他标题"行
            sizeOrder = minutesVOList.getFirst().getMinutesPlans().size() - 1;
            ExcelWriter other = ExcelUtil.getWriter(templatePathApplyExcel, "Sheet1");
            int start = 11 + name.size();
            XSSFSheet xssSheet = (XSSFSheet) other.getSheet();
            InsertRow(other, start, sizeOrder, xssSheet, false);
        }
        //读取模板文件产生workbook对象,这个workbook是一个有内容的工作薄
        Workbook workbook = new XSSFWorkbook(templatePathApplyExcel);
        //读取工作薄的第一个工作表，向工作表中放数据
        Sheet sheet = workbook.getSheetAt(0);
        //1写入excel图片
        sheet.getRow(0).getCell(0);
        File file = new File("./opt/image.png");
        // 获取路径
        String fileUrl = "file:///" + file.getAbsolutePath();
        picture(workbook, sheet, fileUrl);
        //2设置excel编号
        sheet.getRow(1).getCell(0).setCellValue("编号：9fzt-xx-HY字｛2023｝A-001");
        //3会议标题
        sheet.getRow(2).getCell(0).setCellValue("会议纪要");
        //4参会部门
        SysDepartment sysDepartment = sysDepartmentMapper.findDepartment(meetingRecordVO.getCreatedBy());
        sheet.getRow(3).getCell(0).setCellValue("参会部门：" + sysDepartment.getDepartmentName());
        //5
        sheet.getRow(4).getCell(0).setCellValue("会议主题：" + meetingRecordVO.getTitle());
        //6
        sheet.getRow(5).getCell(0).setCellValue("主持人：" + meetingRecordVO.getAdminUserName());
        //7
        sheet.getRow(6).getCell(0).setCellValue("参会人员：" + meetingRecordVO.getAttendees());

        String hour = meetingRecordVO.getStartTime().getHour()+":";
        String minute = meetingRecordVO.getStartTime().getMinute() == 0 ? "00" : String.valueOf(meetingRecordVO.getStartTime().getMinute());
        //8
        sheet.getRow(7).getCell(0).setCellValue("参会时间：" + meetingRecordVO.getStartTime().getYear() + "年" +
                meetingRecordVO.getStartTime().getMonthValue() + "月"
                + meetingRecordVO.getStartTime().getDayOfMonth() + "日  "
                +hour+minute);
        //9
        sheet.getRow(8).getCell(0).setCellValue("参会地点：" + meetingRecordVO.getMeetingRoomName());
        //10
        List<String> titie = new ArrayList<>();
        titie.add("所属部门");
        titie.add("汇报人");
        titie.add("序号");
        titie.add("工作细则");
        titie.add("责任人");
        titie.add("完成时间");
        titie.add("备注");
        for (int i = 0; i < titie.size(); i++) {
            sheet.getRow(9).getCell(i).setCellValue(titie.get(i));
        }
        //11
        int size;
        for (size = 0; size < name.size(); size++) {
            extracted(meetingRecordVO, sheet, name, size, sysDepartment);
        }
        //12其他
        sheet.getRow(10 + name.size()).getCell(0).setCellValue("其他");
        sheet.getRow(10 + name.size()).getCell(1).setCellValue(sysUserMapper.selectByUserId(userId).getUserName());
        sheet.getRow(10 + name.size()).getCell(3).setCellValue("目标与工作内容:");
        if (sizeOrder != 0) {
            //把数据插入到新增行里
            for (int i = 0; i < sizeOrder + 1; i++) {
                sheet.getRow(10 + name.size() + i + 1).getCell(3).setCellValue(minutesPlans.get(i).getPlan());
                sheet.getRow(10 + name.size() + i + 1).getCell(6).setCellValue(minutesPlans.get(i).getStatus() == 1 ? "待优化" : "研发需求");
            }
        }
        //13
        sheet.getRow(9 + name.size() + 3 + sizeOrder).getCell(0).setCellValue("抄送对象:");
        sheet.getRow(9 + name.size() + 3 + sizeOrder).getCell(4).setCellValue("制表人：" + meetingRecordVO.getCreatedBy());
        if (name.size() >1){
            sheet.addMergedRegion(new CellRangeAddress(10, 9 + name.size(), 0, 0));
            sheet.addMergedRegion(new CellRangeAddress(10, 9 + name.size(), 4, 4));
            sheet.addMergedRegion(new CellRangeAddress(10, 9 + name.size(), 5, 5));
        }
        sheet.addMergedRegion(new CellRangeAddress(10 + name.size(), 11 + sizeOrder + name.size(), 0, 0));
        //直接导出电脑文件夹
//            FileOutputStream outputStream = new FileOutputStream("F:\\会议纪要.xlsx");
//            workbook.write(outputStream);
//            outputStream.close();
//            workbook.close();
//            //还原文件
//            restoreFile(templatePaths.toString(), templatePath.toString());
        // 另一种，下载到浏览器。
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(meetingRecordVO.getTitle() + ".xlsx", StandardCharsets.UTF_8));
        OutputStream os = response.getOutputStream();
        workbook.write(os);
        os.flush();
        os.close();
        //还原文件
        restoreFile(templatePathBackupsExcel.toString(), templatePathApplyExcel.toString());
    }


    public static void restoreFile(String backupFilePath, String targetFilePath) {
        // 确保目标路径的目录存在
        Path targetDir = Paths.get(targetFilePath).getParent();
        if (Files.notExists(targetDir)) {
            try {
                Files.createDirectories(targetDir);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // 还原文件
        try {
            Files.copy(Paths.get(backupFilePath), Paths.get(targetFilePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void extracted(MeetingRecordVO meetingRecordVO, Sheet sheet, List<SysUserVO> name, int size, SysDepartment sysDepartment) {
        if (size < 1) {
            sheet.getRow(size + 10).getCell(0).setCellValue(sysDepartment.getDepartmentName());
            sheet.getRow(size + 10).getCell(4).setCellValue(meetingRecordVO.getAdminUserName());
            String hour = meetingRecordVO.getEndTime().getHour()+":";
            String minute = meetingRecordVO.getEndTime().getMinute() == 0 ? "00" : String.valueOf(meetingRecordVO.getEndTime().getMinute());

            sheet.getRow(size + 10).getCell(5).setCellValue(hour+minute);
        }
        sheet.getRow(size + 10).getCell(1).setCellValue(name.get(size).getUserName());
        sheet.getRow(size + 10).getCell(2).setCellValue(size + 1);
        LambdaQueryWrapper<MeetingMinutes> meetingMinutesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        meetingMinutesLambdaQueryWrapper.eq(MeetingMinutes::getMeetingRecordId, meetingRecordVO.getId());
        meetingMinutesLambdaQueryWrapper.eq(MeetingMinutes::getUserId, name.get(size).getUserId());
        MeetingMinutes meetingMinutes = meetingMinutesMapper.selectOne(meetingMinutesLambdaQueryWrapper);
        if (meetingMinutes == null) {
            sheet.getRow(size + 10).getCell(3).setCellValue("工作细节" + "未填写");
        } else {
            sheet.getRow(size + 10).getCell(3).setCellValue("工作细节" + meetingMinutes.getMinutes());
        }
        sheet.getRow(size + 10).getCell(6).setCellValue(" ");


    }


    /**
     * 查询对应会议室及参会人
     *
     * @param lastMeeting 最近会议记录
     * @return 自动提示结果
     */
    private MeetingPromptVO getMeetingPromptVO(MeetingRecord lastMeeting) {
        MeetingRoom meetingRoom = meetingRoomService.getById(lastMeeting.getMeetingRoomId());
        List<String> userIds = meetingAttendeesService.lambdaQuery()
                .eq(MeetingAttendees::getMeetingRecordId, lastMeeting.getId())
                .list()
                .stream()
                .map(MeetingAttendees::getUserId)
                .toList();
        List<SysUser> userList = userService.lambdaQuery()
                .in(SysUser::getUserId, userIds)
                .list()
                .stream()
                .peek(sysUser -> sysUser.setPassword(null))
                .toList();
        return MeetingPromptVO.builder()
                .meetingRoomId(lastMeeting.getMeetingRoomId())
                .meetingRoomName(meetingRoom.getRoomName())
                .users(userList)
                .build();
    }

    public static void picture(Workbook workbook, Sheet sheet, String fileUrl) {
        try {
            Drawing<?> patriarch = sheet.createDrawingPatriarch();
            URL url = new URL(fileUrl);  // 构造URL
            URLConnection con = url.openConnection();   // 打开连接
            con.setConnectTimeout(8 * 1000);  //设置请求超时
            InputStream is = con.getInputStream();    // 输入流
            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            BufferedImage bufferImg = ImageIO.read(is);
            ImageIO.write(bufferImg, "png", byteArrayOut);
            bufferImg.flush();
            byteArrayOut.flush();
            XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, 0, 0, 3, 1);
            patriarch.createPicture(anchor, workbook.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));   //参数二是图片格式 还有png格式等
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}




