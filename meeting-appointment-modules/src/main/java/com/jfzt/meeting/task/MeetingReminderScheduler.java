package com.jfzt.meeting.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jfzt.meeting.entity.MeetingRoom;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.dto.MeetingRecordDTO;
import com.jfzt.meeting.service.MeetingRoomService;
import com.jfzt.meeting.service.SysUserService;
import com.jfzt.meeting.utils.WxUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 会议提醒定时任务
 * @author zilong.deng
 * @since 2024-06-04 15:26:28
 */
@Slf4j
@Component
public class MeetingReminderScheduler {

    MeetingRoomService meetingRoomService;
    SysUserService userService;
    WxUtil wxUtil;
    private TaskScheduler taskScheduler;

    /**
     * setter注入
     */
    @Autowired
    public void setMeetingRoomService (MeetingRoomService meetingRoomService) {
        this.meetingRoomService = meetingRoomService;
    }
    @Autowired
    public void setUserService (SysUserService userService) {
        this.userService = userService;
    }
    @Autowired
    public void setWxUtil (WxUtil wxUtil) {
        this.wxUtil = wxUtil;
    }
    @Autowired
    public void setTaskScheduler (TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    /**
     * 创建定时任务发送会议提醒
     * @param meeting 会议信息
     **/
    public void scheduleMeetingReminder (MeetingRecordDTO meeting) {
        LocalDateTime reminderTime = meeting.getStartTime().minusMinutes(10);
        //        LocalDateTime reminderTime = LocalDateTime.now().plusSeconds(30);
        log.info("定时任务会议提醒执行时间:{}", reminderTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        long delay = Duration.between(LocalDateTime.now(), reminderTime).toMillis();
        if (delay > 0) {
            ScheduledFuture<?> scheduledTask = taskScheduler.schedule(
                    () -> sendReminder(meeting),
                    new Date(System.currentTimeMillis() + delay)
            );
            scheduledTasks.put("MeetingReminder_" + meeting.getId(), scheduledTask);
        }
    }


    /**
     *  取消会议提醒定时任务
     * @param meetingId 会议id
     */
    public void cancelMeetingReminder (Long meetingId) {
        ScheduledFuture<?> scheduledTask = scheduledTasks.get("MeetingReminder_" + meetingId);
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
            scheduledTasks.remove("MeetingReminder_" + meetingId);
            log.info("取消会议提醒任务{}", "MeetingReminder_" + meetingId);
        }
    }

    /**
     *  发送会议提醒
     * @param meetingRecordDTO 会议信息
     */
    private void sendReminder (MeetingRecordDTO meetingRecordDTO){
        List<String> usrIds = meetingRecordDTO.getUsers().stream().map(SysUser::getUserId).toList();
        MeetingRoom meetingRoom = meetingRoomService.getById(meetingRecordDTO.getMeetingRoomId());
        List<SysUser> userList = userService.list(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserId, meetingRecordDTO.getCreatedBy()));
        String creatorUserName = "";
        if (!userList.isEmpty()) {
            creatorUserName = userList.getFirst().getUserName();
            log.info("发送会议提醒给用户： {}", creatorUserName);
        }
        String notices =
                "##### 会议提醒\n" +
                        "**会议主题 :** " + meetingRecordDTO.getTitle() + "\n" +
                        "**发起人 :** " + creatorUserName + "\n" +
                        "**日期 :** " + meetingRecordDTO.getStartTime().format(DateTimeFormatter.ofPattern("yyyy 年MM 月dd 日"))
                        + "\n" +
                        "**时间 :** " + meetingRecordDTO.getStartTime().format(DateTimeFormatter.ofPattern("HH : mm"))
                        + " ~ "
                        + meetingRecordDTO.getEndTime().format(DateTimeFormatter.ofPattern("HH : mm")) + "\n" +
                        "**会议室 :** " + meetingRoom.getRoomName() + "\n" +
                        "**地点 :** " + meetingRoom.getLocation() + "\n" +
                        "<font color=\"warning\">**会议还有十分钟就开始了，请注意及时参会!** </font>";
            wxUtil.sendsWxReminders(usrIds, notices);
    }

}
