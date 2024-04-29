package com.jfzt.meeting.entity.vo;

import com.jfzt.meeting.entity.MeetingRoom;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zilong.deng
 * @date 2024/04/28
 */

@Setter
@Getter
public class MeetingRoomVO extends MeetingRoom {

    /**
     * 创建人id
     */
    private Long userId;

}
