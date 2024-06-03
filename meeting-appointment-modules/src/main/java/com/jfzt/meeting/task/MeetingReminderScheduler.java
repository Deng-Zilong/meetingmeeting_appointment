package com.jfzt.meeting.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jfzt.meeting.entity.MeetingRoom;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.dto.MeetingRecordDTO;
import com.jfzt.meeting.service.MeetingRoomService;
import com.jfzt.meeting.service.SysUserService;
import com.jfzt.meeting.utils.WxUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
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
 *
 * @author zilong.deng
 * @date 2024/05/29
 */
@Slf4j
@Component
public class MeetingReminderScheduler {

    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    @Autowired
    MeetingRoomService meetingRoomService;
    @Autowired
    SysUserService userService;
    @Autowired
    WxUtil wxUtil;
    @Autowired
    private TaskScheduler taskScheduler;

    public void scheduleMeetingReminder (MeetingRecordDTO meeting) {
        LocalDateTime reminderTime = meeting.getStartTime().minusMinutes(10);
        //        LocalDateTime reminderTime = LocalDateTime.now().plusSeconds(30);
        log.info("定时任务会议提醒执行时间:{}", reminderTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        long delay = Duration.between(LocalDateTime.now(), reminderTime).toMillis();
        if (delay > 0) {
            ScheduledFuture<?> scheduledTask = taskScheduler.schedule(
                    () -> {
                        try {
                            sendReminder(meeting);
                        } catch (WxErrorException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    new Date(System.currentTimeMillis() + delay)
            );
            scheduledTasks.put("MeetingReminder_" + meeting.getId(), scheduledTask);
        }
    }


    /**
     * @param meetingId 会议id
     * @description 取消会议提醒定时任务
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
     * @param meetingRecordDTO 会议信息
     * @description 发送会议提醒
     */
    private void sendReminder (MeetingRecordDTO meetingRecordDTO) throws WxErrorException {
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
                "**会议提醒**\n" +
                        "会议 : " + meetingRecordDTO.getTitle() + "\n" +
                        "发起人 : " + creatorUserName + "\n" +
                        "日期 : " + meetingRecordDTO.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        + "\n" +
                        "时间 : " + meetingRecordDTO.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                        + "  ~  "
                        + meetingRecordDTO.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n" +
                        "会议室 : " + meetingRoom.getRoomName() + "\n" +
                        "地点 : " + meetingRoom.getLocation() + "\n" +
                        "会议还有十分钟就开始了，请注意及时参会!";
        try {
            wxUtil.sendsWxReminders(usrIds, notices);
        } catch (Exception e) {
            log.error("发送会议提醒失败", e);
        }
    }

}
