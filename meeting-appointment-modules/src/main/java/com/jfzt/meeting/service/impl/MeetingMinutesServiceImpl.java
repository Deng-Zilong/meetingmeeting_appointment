package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingMinutes;
import com.jfzt.meeting.entity.MeetingRecord;
import com.jfzt.meeting.entity.MinutesPlan;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.dto.MeetingMinutesDTO;
import com.jfzt.meeting.entity.vo.MeetingMinutesVO;
import com.jfzt.meeting.entity.vo.MinutesPlanVO;
import com.jfzt.meeting.exception.ErrorCodeEnum;
import com.jfzt.meeting.exception.RRException;
import com.jfzt.meeting.mapper.MeetingMinutesMapper;
import com.jfzt.meeting.service.MeetingMinutesService;
import com.jfzt.meeting.service.MeetingRecordService;
import com.jfzt.meeting.service.MinutesPlanService;
import com.jfzt.meeting.service.SysUserService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 针对表【meeting_minutes(会议纪要)】的数据库操作Service实现
 * @author zilong.deng
 * @since 2024-06-04 14:09:31
 */
@Service
public class MeetingMinutesServiceImpl extends ServiceImpl<MeetingMinutesMapper, MeetingMinutes>
        implements MeetingMinutesService {
    @Autowired
    SysUserService sysUserService;
    @Autowired
    MeetingRecordService meetingRecordService;
    @Resource
    private MinutesPlanService minutesPlanService;

    /**
     * 根据会议记录id或用户id查询会议纪要
     * @param meetingMinutes 会议记录id或用户id
     * @return 会议纪要VO
     */
    @Override
    public List<MeetingMinutesVO> getMeetingMinutes (MeetingMinutes meetingMinutes) {
        if (meetingMinutes.getMeetingRecordId() == null && Strings.isBlank(meetingMinutes.getUserId())) {
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0410);
        }
        LambdaQueryWrapper<MeetingMinutes> wrapper = new LambdaQueryWrapper<>();
        //当前用户所有纪要
        if (meetingMinutes.getMeetingRecordId() != null) {
            wrapper.eq(MeetingMinutes::getMeetingRecordId, meetingMinutes.getMeetingRecordId());
        }
        //当前会议所有会议纪要
        if (meetingMinutes.getUserId() != null) {
            wrapper.eq(MeetingMinutes::getUserId, meetingMinutes.getUserId());
        }
        List<MeetingMinutes> minutesList = list(wrapper);
        return minutesList.stream().map(minutes -> {
            MeetingMinutesVO meetingMinutesVO = new MeetingMinutesVO();
            BeanUtils.copyProperties(minutes, meetingMinutesVO);
            List<SysUser> userList = sysUserService.list(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserId,
                    minutes.getUserId()));
            if (!userList.isEmpty()) {
                meetingMinutesVO.setUserName(userList.getFirst().getUserName());
            }
            List<MeetingRecord> meetingRecords = meetingRecordService.list(
                    new LambdaQueryWrapper<MeetingRecord>().eq(MeetingRecord::getId, minutes.getMeetingRecordId()));
            if (!meetingRecords.isEmpty()) {
                meetingMinutesVO.setMeetingRecordTitle(meetingRecords.getFirst().getTitle());
                if (Objects.equals(meetingRecords.getFirst().getCreatedBy(), meetingMinutes.getUserId())) {
                    List<MinutesPlanVO> minutesPlans = new ArrayList<>(minutesPlanService.list(
                                    new LambdaQueryWrapper<MinutesPlan>()
                                            .eq(MinutesPlan::getMinutesId, minutes.getId()))
                            .stream().map(minutesPlan -> {
                                MinutesPlanVO minutesPlanVO = new MinutesPlanVO();
                                BeanUtils.copyProperties(minutesPlan, minutesPlanVO);
                                return minutesPlanVO;
                            }).toList());
                    if (minutesPlans.isEmpty()){
                        minutesPlans.add(new MinutesPlanVO());
                    }
                    meetingMinutesVO.setMinutesPlans(minutesPlans);
                }
            }
            return meetingMinutesVO;
        }).toList();
    }

    /**
     * 保存会议纪要
     * @param meetingMinutesDTO 会议纪要
     * @return 保存结果
     */
    @Override
    public Result<Object> saveOrUpdateMinutes (MeetingMinutesDTO meetingMinutesDTO) {
        MeetingMinutes selfMinutes = lambdaQuery()
                .eq(MeetingMinutes::getMeetingRecordId, meetingMinutesDTO.getMeetingRecordId())
                .eq(MeetingMinutes::getUserId, meetingMinutesDTO.getUserId())
                .one();
        // 当前用户没有会议纪要，保存会议纪要
        if (selfMinutes == null) {
            MeetingMinutes minutes = new MeetingMinutes();
            BeanUtils.copyProperties(meetingMinutesDTO, minutes);
            save(minutes);
            meetingMinutesDTO.setId(minutes.getId());
        } else {
            // 当前用户有会议纪要，修改会议纪要
            SysUser user = sysUserService.lambdaQuery()
                    .eq(SysUser::getUserId, meetingMinutesDTO.getUserId())
                    .one();
            MeetingRecord meetingRecord = meetingRecordService.lambdaQuery()
                    .eq(MeetingRecord::getId, meetingMinutesDTO.getMeetingRecordId())
                    .one();
            if (user == null || meetingRecord == null) {
                throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0400);
            }
            meetingMinutesDTO.setId(selfMinutes.getId());
            MeetingMinutes minutes = new MeetingMinutes();
            BeanUtils.copyProperties(meetingMinutesDTO, minutes);
            updateById(minutes);

        }
        plan(meetingMinutesDTO);

        return Result.success();
    }

    /**
     * 新增修改迭代内容
     * @param meetingMinutesDTO 入参请求体
     */
    private void plan (MeetingMinutesDTO meetingMinutesDTO) {
        if (meetingMinutesDTO.getMinutesPlan() == null || meetingMinutesDTO.getMinutesPlan().isEmpty()) {
            return;
        }
        meetingMinutesDTO.getMinutesPlan().stream().peek(plan -> {
            MinutesPlan existMinutesPlan = minutesPlanService.lambdaQuery()
                    .eq(MinutesPlan::getId, plan.getId())
                    .one();
            //新增内容
            if (existMinutesPlan == null) {
                MinutesPlan minutesPlan = MinutesPlan.builder()
                        .minutesId(meetingMinutesDTO.getId())
                        .status(plan.getStatus())
                        .plan(plan.getPlan())
                        .build();
                minutesPlanService.save(minutesPlan);
            } else {
                //更新内容
                MinutesPlan updateMinutesPlan = MinutesPlan.builder()
                        .id(plan.getId())
                        .status(plan.getStatus())
                        .minutesId(meetingMinutesDTO.getId())
                        .plan(plan.getPlan())
                        .build();
                minutesPlanService.updateById(updateMinutesPlan);
            }
        }).toList();
    }

    /**
     * 根据会议id删除所有会议纪要
     * @param meetingRecordId 会议id
     */
    @Transactional
    @Override
    public void deleteMeetingMinutes (Long meetingRecordId) {
        if (meetingRecordId == null) {
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0410);
        }
        List<Integer> minutesIds = list(new LambdaQueryWrapper<MeetingMinutes>()
                .eq(MeetingMinutes::getMeetingRecordId, meetingRecordId))
                .stream().map(MeetingMinutes::getId).toList();
        //删除纪要
        removeBatchByIds(minutesIds);
        //删除迭代计划
        if (!minutesIds.isEmpty()) {
            minutesPlanService.remove(new LambdaQueryWrapper<MinutesPlan>()
                    .in(MinutesPlan::getMinutesId, minutesIds));
        }
    }

    /**
     * 根据用户id 纪要id删除指定纪要
     * @param meetingMinutes userId id
     */
    @Transactional
    @Override
    public void deleteMeetingMinutes (MeetingMinutes meetingMinutes) {
        if (meetingMinutes == null) {
            throw new RRException(ErrorCodeEnum.SERVICE_ERROR_A0410);
        }
        MeetingMinutes minutes = getById(meetingMinutes.getId());
        if (!Objects.equals(minutes.getUserId(), meetingMinutes.getUserId())) {
            throw new RRException("用户没有删除权限！", ErrorCodeEnum.SERVICE_ERROR_A0312.getCode());
        }
        removeById(meetingMinutes.getId());
        //删除迭代计划
        minutesPlanService.remove(new LambdaQueryWrapper<MinutesPlan>()
                .eq(MinutesPlan::getMinutesId, minutes.getId()));
    }

}




