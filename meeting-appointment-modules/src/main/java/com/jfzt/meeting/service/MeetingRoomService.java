package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingRoom;
import com.jfzt.meeting.entity.dto.MeetingRoomDTO;
import com.jfzt.meeting.entity.vo.MeetingRoomOccupancyVO;
import com.jfzt.meeting.entity.vo.MeetingRoomStatusVO;
import com.jfzt.meeting.entity.vo.MeetingRoomVO;
import com.jfzt.meeting.entity.vo.TimePeriodStatusVO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zilong.deng
 * @since 2024-06-04 13:57:38
 */
public interface MeetingRoomService extends IService<MeetingRoom> {

    /**
     * 新增会议室
     * @param meetingRoom 会议室对象
     * @return 结果
     */
    Result<Integer> addMeetingRoom (MeetingRoom meetingRoom);


    /**
     * 查询会议室状态
     * @return 会议室状态VO
     */
    List<MeetingRoomStatusVO> getMeetingRoomStatus ();

    /**
     * 删除会议室
     * @param id 会议室id
     * @return 删除结果
     */
    Result<Integer> deleteMeetingRoom (Long id, Integer currentLevel);


     /**
     * 查询当天各个时间段会议室占用情况
     * @return 时间段占用状态
     */
    List<Integer> getTodayTimePeriodStatus ();

    /**
     * 查询指定会议室当天各个时间段占用情况
     * @param id   会议室id
     * @param date 日期
     * @return 时间段状态
     */
    Result<List<TimePeriodStatusVO>> getTimePeriodStatusByIdAndDate (Long id, LocalDate date);

    /**
     * 根据时间段获取可用的会议室
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 会议室VO
     */
    Result<List<MeetingRoomVO>> getAvailableMeetingRooms (LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 修改会议室状态
     * @param meetingRoomDTO 会议室DTO对象
     * @return 修改结果
     */
    Result<Integer> updateStatus (MeetingRoomDTO meetingRoomDTO);

    /**
     * 查询被禁用的会议室的id
     * @param currentLevel 当前登录用户的权限等级
     * @return 权限等级
     */
    Result<List<Long>> selectUsableRoom (Integer currentLevel);


    /**
     * 查询近七天会议室占用率（9：00-18：00）不包括周末
     * @return 会议室占用率VO
     */
    Result<List<MeetingRoomOccupancyVO>> getAllMeetingRoomOccupancy ();

    /**
     * 查询最近五个工作日内各会议室占用比例
     * @return 会议室占用比例VO
     */
    Result<List<MeetingRoomOccupancyVO>> getAllMeetingRoomProportion ();

}
