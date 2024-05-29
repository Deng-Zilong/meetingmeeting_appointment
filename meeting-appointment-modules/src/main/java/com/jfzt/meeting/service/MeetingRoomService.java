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
 * @description 针对表【meeting_room(会议室表)】的数据库操作Service
 * @createDate 2024-04-28 11:50:45
 */
public interface MeetingRoomService extends IService<MeetingRoom> {

    /**
     * @param meetingRoom 会议室
     * @return {@code Result<Integer>}
     * @description 新增会议室
     */
    Result<Integer> addMeetingRoom (MeetingRoom meetingRoom);


    /**
     * @return {@code Result<List<MeetingRoomStatusVO>>}
     * @description 查询会议室状态
     */
    List<MeetingRoomStatusVO> getMeetingRoomStatus ();

    /**
     * @param id 会议室id
     * @return {@code Result<Integer>}
     * @description 删除会议室
     */
    Result<Integer> deleteMeetingRoom (Long id, Integer currentLevel);

    /**
     * @return {@code List<Integer>}
     * @description 查询当天各个时间段会议室占用情况
     */

    List<Integer> getTodayTimePeriodStatus ();

    /**
     * @param id   会议室id
     * @param date 日期
     * @return {@code Result<List<TimePeriodStatusVO>>}
     * @description 查询指定会议室当天各个时间段占用情况
     */
    Result<List<TimePeriodStatusVO>> getTimePeriodStatusByIdAndDate (Long id, LocalDate date);

    /**
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return {@code Result<List<MeetingRoomVO>>}
     * @description 根据时间段获取可用的会议室
     */
    Result<List<MeetingRoomVO>> getAvailableMeetingRooms (LocalDateTime startTime, LocalDateTime endTime);

    /**
     * @param meetingRoomDTO 会议室DTO对象
     * @return @return com.jfzt.meeting.common.Result<java.lang.Integer>
     * @description 更新会议室状态
     */
    Result<Integer> updateStatus (MeetingRoomDTO meetingRoomDTO);

    /**
     * @param currentLevel 当前登录用户的权限等级
     * @return com.jfzt.meeting.common.Result<java.util.List < < java.lang.Integer>>
     * @description 查询被禁用的会议室的id
     */
    Result<List<Long>> selectUsableRoom (Integer currentLevel);


    /**
     * @return {@code Result<List<MeetingRoomOccupancyVO>>}
     * @description 查询最近七天会议室占用率
     */
    Result<List<MeetingRoomOccupancyVO>> getAllMeetingRoomOccupancy ();
}
