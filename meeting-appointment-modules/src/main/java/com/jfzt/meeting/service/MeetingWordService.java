package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingWord;
import com.jfzt.meeting.entity.dto.MeetingWordDTO;
import com.jfzt.meeting.entity.vo.MeetingMinutesVO;

import java.util.List;

/**
 * @author zilong.deng
 * @since 2024-06-18 12:02:48
 */
public interface MeetingWordService extends IService<MeetingWord> {

    Result<List<MeetingWord>> getMeetingWord(Long meetingRecordId);

    Result<Object> saveOrUpdateWord(MeetingWord meetingWord);


    Result<Object> deleteWordOrPlan(Long planId, Long contentId);
}
