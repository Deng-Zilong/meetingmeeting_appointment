package com.jfzt.meeting.entity.vo;

import lombok.Data;

/**
 * @author zilong.deng
 * @date 2024/04/28
 */

@Data
public class MeetingRoomVO {

    private Integer location;

    private String roomName;

    private Integer status;

    private Integer capacity;

    private String createdBy;


}
