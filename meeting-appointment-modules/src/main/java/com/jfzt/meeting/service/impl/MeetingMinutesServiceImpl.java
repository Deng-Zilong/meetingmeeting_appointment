package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.entity.MeetingMinutes;
import com.jfzt.meeting.entity.MeetingMinutesVO;
import com.jfzt.meeting.entity.MeetingRecord;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.exception.ErrorCodeEnum;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.mapper.MeetingMinutesMapper;
import com.jfzt.meeting.service.MeetingMinutesService;
import com.jfzt.meeting.service.MeetingRecordService;
import com.jfzt.meeting.service.SysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zilong.deng
 * @description 针对表【meeting_minutes(会议纪要)】的数据库操作Service实现
 * @createDate 2024-05-29 17:59:44
 */
@Service
public class MeetingMinutesServiceImpl extends ServiceImpl<MeetingMinutesMapper, MeetingMinutes>
        implements MeetingMinutesService {
    @Autowired
    SysUserService sysUserService;
    @Autowired
    MeetingRecordService meetingRecordService;

    /**
     * @param meetingMinutes 会议记录id或用户id
     * @return {@code List<MeetingMinutesVO>}
     * @description 根据会议记录id或用户id查询会议纪要
     */
    @Override
    public List<MeetingMinutesVO> getMeetingMinutes (MeetingMinutes meetingMinutes) {
        if (meetingMinutes.getMeetingRecordId() != null && meetingMinutes.getUserId() != null) {
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0410);
        }
        LambdaQueryWrapper<MeetingMinutes> wrapper = new LambdaQueryWrapper<>();
        if (meetingMinutes.getMeetingRecordId() != null) {
            wrapper.eq(MeetingMinutes::getMeetingRecordId, meetingMinutes.getMeetingRecordId());
        }
        if (meetingMinutes.getUserId() != null) {
            wrapper.eq(MeetingMinutes::getUserId, meetingMinutes.getUserId());
        }
        List<MeetingMinutes> minutesList = list(wrapper);
        return minutesList.stream().map(minutes -> {
            MeetingMinutesVO meetingMinutesVO = new MeetingMinutesVO();
            BeanUtils.copyProperties(minutes, meetingMinutesVO);
            List<SysUser> userList = sysUserService.list(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserId, minutes.getUserId()));
            if (!userList.isEmpty()) {
                meetingMinutesVO.setUserName(userList.getFirst().getUserName());
            }
            List<MeetingRecord> meetingRecords = meetingRecordService.list(new LambdaQueryWrapper<MeetingRecord>().eq(MeetingRecord::getId, minutes.getMeetingRecordId()));
            if (!meetingRecords.isEmpty()) {
                meetingMinutesVO.setMeetingRecordTitle(meetingRecords.getFirst().getTitle());
            }
            return meetingMinutesVO;
        }).toList();
    }

}




