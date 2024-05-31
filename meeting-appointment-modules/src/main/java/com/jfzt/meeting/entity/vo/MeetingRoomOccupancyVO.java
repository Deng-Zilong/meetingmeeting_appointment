package com.jfzt.meeting.entity.vo;

import lombok.Data;

/**
 * @author zilong.deng
 * @date 2024/05/27
 * @description 会议室占用率
 */
@Data
public class MeetingRoomOccupancyVO {
    //时间段总数
    private Long total;

    //已占用数
    private Long occupied;

    //会议室id
    private Long id;

    //会议室名称
    private String name;

}
