package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.entity.MeetingNotice;

import java.util.List;

/**
 * @author zilong.deng
 * @description 针对表【meeting_notice(会议公告表)】的数据库操作Service
 * @since 2024-04-28 11:45:33
 */
public interface MeetingNoticeService extends IService<MeetingNotice> {

    int addNotice(MeetingNotice meetingNotice);

    int deleteNotice(Long id);

    List<String> selectAll(MeetingNotice meetingNotice);

}
