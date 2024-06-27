package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingWord;

import java.util.List;

/**
 * @author zilong.deng
 * @since 2024-06-18 12:02:48
 */
public interface MeetingWordService extends IService<MeetingWord> {

    /**
     * 根据会议纪要id和用户id获取会议纪要
     * @param meetingRecordId 会议纪要id
     * @param userId 用户id
     * @return 会议纪要结果
     */
    Result<List<MeetingWord>> getMeetingWord(Long meetingRecordId,String userId);

    /**
     * 保存或更新会议纪要
     * @param meetingWord 会议纪要
     * @return 保存结果
     */
    Result<Object> saveOrUpdateWord(MeetingWord meetingWord);

    /**
     * 删除会议纪要
     * @param planId Excel计划id
     * @param contentId Word内容标题id
     * @return 删除结果
     */
    Result<Object> deleteWordOrPlan(Long planId, Long contentId);

}
