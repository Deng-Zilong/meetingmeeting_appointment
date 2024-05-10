package com.jfzt.meeting.constant;

/**
 * 会议室状态
 *
 * @author zilong.deng
 * @date 2024/05/10
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
