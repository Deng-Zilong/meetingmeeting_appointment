package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.common.Result;
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
import jakarta.annotation.Resource;
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
        if (meetingMinutes.getMeetingRecordId() == null && Strings.isBlank(meetingMinutes.getUserId())) {
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
    /**
     * @Description 保存会议纪要
     * @Param [meetingMinutes]
     * @return com.jfzt.meeting.common.Result<java.lang.Object>
     */
    @Override
    public Result<Object> saveOrUpdateMinutes(MeetingMinutes meetingMinutes) {
        MeetingMinutes selfMinutes = lambdaQuery().eq(MeetingMinutes::getMeetingRecordId, meetingMinutes.getMeetingRecordId())
                .eq(MeetingMinutes::getUserId, meetingMinutes.getUserId())
                .one();
        if (selfMinutes == null){
            save(meetingMinutes);
        }else {
            SysUser user = sysUserService.lambdaQuery()
                    .eq(SysUser::getUserId, meetingMinutes.getUserId())
                    .one();
            MeetingRecord meetingRecord = meetingRecordService.lambdaQuery()
                    .eq(MeetingRecord::getId, meetingMinutes.getMeetingRecordId())
                    .one();
            if (user == null || meetingRecord == null){
                throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0400);
            }
            meetingMinutes.setId(selfMinutes.getId());
            updateById(meetingMinutes);
        }
        return Result.success();
    }

}



