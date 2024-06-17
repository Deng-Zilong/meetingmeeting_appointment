package com.jfzt.meeting.entity.dto;

import com.jfzt.meeting.entity.MeetingRoom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: chenyu.di
 * @since: 2024-06-12 16:29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingDeviceDTO {
    /**
     * 会议室id
     */
    private List<Long> roomId = new ArrayList<>();
    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 创建人id
     */
    private String userId;
}