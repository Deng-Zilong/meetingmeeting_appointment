package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingRecord;
import com.jfzt.meeting.entity.MeetingRoom;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.vo.MeetingRoomStatusVO;
import com.jfzt.meeting.entity.vo.MeetingRoomVO;
import com.jfzt.meeting.mapper.MeetingAttendeesMapper;
import com.jfzt.meeting.mapper.MeetingRoomMapper;
import com.jfzt.meeting.service.MeetingRecordService;
import com.jfzt.meeting.service.MeetingRoomService;
import com.jfzt.meeting.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zilong.deng
 * @description 针对表【meeting_room(会议室表)】的数据库操作Service实现
 * @createDate 2024-04-28 11:50:45
 */
@Slf4j
@Service
public class MeetingRoomServiceImpl extends ServiceImpl<MeetingRoomMapper, MeetingRoom>
        implements MeetingRoomService {

    private MeetingRecordService meetingRecordService;


    @Autowired
    private MeetingRoomService meetingRoomService;

    private SysUserService userService;

    private MeetingAttendeesMapper attendeesMapper;


    /**
     * @param meetingRoomVO 会议室对象
     * @return {@code Boolean}
     */
    @Override
    public Result<String> addMeetingRoom (MeetingRoomVO meetingRoomVO) {
        List<MeetingRoom> list = this.list(new LambdaQueryWrapper<MeetingRoom>().eq(MeetingRoom::getLocation, meetingRoomVO.getLocation()));
        if (!list.isEmpty()) {
            //location重复，添加失败
            log.error("{}:location重复，添加失败", meetingRoomVO);
            return Result.fail("location重复，添加失败");
        }
        MeetingRoom room = new MeetingRoom();
        room.setCapacity(meetingRoomVO.getCapacity());
        room.setRoomName(meetingRoomVO.getRoomName());
        room.setLocation(meetingRoomVO.getLocation());
        room.setCreatedBy(meetingRoomVO.getCreatedBy());
        boolean saved = this.save(room);
        if (saved) {
            return Result.success("添加成功");
        } else {
            return Result.fail("添加失败");

        }
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
                        .eq(MeetingRoom::getIsDeleted, 0)
                        .orderByAsc(MeetingRoom::getLocation));

        return meetingRoomList.stream().map(meetingRoom -> {
            MeetingRoomStatusVO meetingRoomStatusVO = new MeetingRoomStatusVO();
            BeanUtils.copyProperties(meetingRoom, meetingRoomStatusVO);
            //暂停使用直接返回
            if (meetingRoomStatusVO.getStatus() == 0) {
                return meetingRoomStatusVO;
            }
            LambdaQueryWrapper<MeetingRecord> recordQueryWrapper = new LambdaQueryWrapper<>();
            recordQueryWrapper
                    .lt(MeetingRecord::getStartTime, LocalDateTime.now())
                    .gt(MeetingRecord::getEndTime, LocalDateTime.now())
                    .eq(MeetingRecord::getMeetingRoomId, meetingRoom.getId());
            recordQueryWrapper.and(wrapper -> wrapper.eq(MeetingRecord::getStatus, 0)
                    .or().eq(MeetingRecord::getStatus, 1)
                    .or().eq(MeetingRecord::getStatus, 2));

            List<MeetingRecord> meetingRecords = meetingRecordService.list(recordQueryWrapper);
            if (meetingRecords.isEmpty()) {
                //当前没有会议
                meetingRoomStatusVO.setStatus(1);
            } else {
                MeetingRecord meetingRecord = meetingRecords.getFirst();
                //更新会议室状态
                meetingRoomStatusVO.setStatus(2);
                //更新会议状态
                meetingRecordService.updateRecordStatus(meetingRecord);
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

    @Override
    public Boolean deleteMeetingRoom (MeetingRoomVO meetingRoomVO) {
        return null;
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
                timeStatus.add(i, 0);
                startTime = startTime.plusMinutes(30);
                endTime = endTime.plusMinutes(30);
                continue;
            }
            //之后的时间，判断是否有会议的开始时间或结束时间包含在里面，有的话且包含所有的会议室则为1已预订
            LambdaQueryWrapper<MeetingRecord> recordQueryWrapper = new LambdaQueryWrapper<>();
            //开始时间或结束时间包含在时间段内
            recordQueryWrapper
                    .between(MeetingRecord::getStartTime, startTime, endTime)
                    .or().between(MeetingRecord::getEndTime, startTime, endTime)
                    .or().lt(MeetingRecord::getStartTime, startTime).gt(MeetingRecord::getEndTime, endTime);
            //没有逻辑删除
            recordQueryWrapper.and(recordQueryWrapper1 -> recordQueryWrapper1.eq(MeetingRecord::getStatus, 0)
                    .or().eq(MeetingRecord::getStatus, 1));
            recordQueryWrapper.and(recordQueryWrapper1 -> recordQueryWrapper1.eq(MeetingRecord::getStatus, 0)
                    .or().eq(MeetingRecord::getStatus, 1));
            List<MeetingRecord> meetingRecords = meetingRecordService.list(recordQueryWrapper);
            //根据会议室id收集获得占用不同会议室的数量
            long size = meetingRecords.stream().collect(Collectors.groupingBy(MeetingRecord::getMeetingRoomId))
                    .size();
            //获取可使用的会议室总数
            long count = this.count(new LambdaQueryWrapper<MeetingRoom>()
                    .eq(MeetingRoom::getIsDeleted, 0)
                    .eq(MeetingRoom::getStatus, 1));
            if (count == size) {
                System.out.println(timeStatus);
                //相同表示时间段全被占用
                timeStatus.add(i, 1);
                startTime = startTime.plusMinutes(30);
                endTime = endTime.plusMinutes(30);
                continue;
            }
            //没全部占用
            timeStatus.add(i, 2);
            System.out.println(timeStatus);
            startTime = startTime.plusMinutes(30);
            endTime = endTime.plusMinutes(30);
        }
        return timeStatus;
    }

    /**
     * setter注入
     */
    @Autowired
    public void setUserService (SysUserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setAttendeesMapper (MeetingAttendeesMapper attendeesMapper) {
        this.attendeesMapper = attendeesMapper;
    }

    @Autowired
    public void setMeetingRecordService (MeetingRecordService meetingRecordService) {
        this.meetingRecordService = meetingRecordService;
    }
}




