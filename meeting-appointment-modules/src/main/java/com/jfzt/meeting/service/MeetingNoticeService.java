package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingNotice;
import com.jfzt.meeting.entity.vo.MeetingNoticeVO;

import java.util.List;

/**
 * @author zilong.deng
 * @description 针对表【meeting_notice(会议公告表)】的数据库操作Service
 * @since 2024-04-28 11:45:33
 */
public interface MeetingNoticeService extends IService<MeetingNotice> {

    /**
     * 新增公告
     * @param meetingNoticeVO 公告信息VO对象
     * @return com.jfzt.meeting.common.Result<java.lang.Integer>
     */
    Result<Integer> addNotice(MeetingNoticeVO meetingNoticeVO);


    /**
     * 查询所有公告
     * @return com.jfzt.meeting.common.Result<java.util.List<java.lang.String>>
     */
    Result<List<String>> selectAll();

}
