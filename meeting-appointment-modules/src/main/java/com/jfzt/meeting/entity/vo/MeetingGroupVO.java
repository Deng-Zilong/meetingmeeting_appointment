package com.jfzt.meeting.entity.vo;

import com.jfzt.meeting.entity.MeetingGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: chenyu.di
 * @since: 2024-04-29 16:33
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MeetingGroupVO extends MeetingGroup {

    /**
     * 创建人姓名
     */
    private String userName;



}