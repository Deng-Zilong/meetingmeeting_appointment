package com.jfzt.meeting.entity.vo;

import com.jfzt.meeting.entity.MeetingDevice;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @author: chenyu.di
 * @since: 2024-06-12 13:49
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MeetingDeviceVO extends MeetingDevice {
    /**
     * 会议室名称
     */
    private String roomName;
}