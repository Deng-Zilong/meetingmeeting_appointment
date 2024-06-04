package com.jfzt.meeting.entity.vo;

import com.jfzt.meeting.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: chenyu.di
 * @since: 2024-06-03 13:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeetingPromptVO {
    /**
     * 会议主题
     */
    private String title;
    /**
     * 会议室id
     */
    private Long meetingRoomId;
    /**
     * 会议室名称
     */
    private String meetingRoomName;
    /**
     * 参会人详细信息
     */
    private List<SysUser> users;

}