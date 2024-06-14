package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingNotice;
import com.jfzt.meeting.entity.vo.MeetingNoticeVO;

import java.util.List;

/**
 * 针对表【meeting_notice(会议公告表)】的数据库操作Service
 * @author zilong.deng
 * @since 2024-04-28 11:45:33
 */
public interface MeetingNoticeService extends IService<MeetingNotice> {


    /**
     * 上传公告
     * @param meetingNoticeVO 公告信息VO对象
     * @return Result<Integer> 返回结果
     */
    Result<Integer> addNotice(MeetingNoticeVO meetingNoticeVO);


    /**
     * 查询所有通告信息
     * @return 通告信息
     */
    List<String> selectAll();

}
