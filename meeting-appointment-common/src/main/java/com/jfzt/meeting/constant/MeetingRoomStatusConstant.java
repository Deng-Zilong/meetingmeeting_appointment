package com.jfzt.meeting.constant;

/**
 * 会议室状态常量类
 * @author zilong.deng
 * @since 2024-5-9 14:13:51
 */
public class MeetingRoomStatusConstant {

    /**
     * 暂停使用
     */
    public static final Integer MEETINGROOM_STATUS_PAUSE = 0;

    /**
     * 可使用/空闲
     */
    public static final Integer MEETINGROOM_STATUS_AVAILABLE = 1;

    /**
     * 使用中,不保存至数据库，实时获取
     */
    public static final Integer MEETINGROOM_STATUS_USING = 2;
}
