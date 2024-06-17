package com.jfzt.meeting.entity.dto;

import com.jfzt.meeting.entity.MeetingMinutes;
import com.jfzt.meeting.entity.MinutesPlan;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author: chenyu.di
 * @since: 2024-06-17 14:47
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MeetingMinutesDTO extends MeetingMinutes {
    /**
     *迭代内容
     */
    private List<MinutesPlan> minutesPlan;
}