package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingRecord;
import com.jfzt.meeting.entity.MeetingWord;
import com.jfzt.meeting.entity.dto.MeetingWordDTO;
import com.jfzt.meeting.entity.vo.MeetingMinutesVO;
import com.jfzt.meeting.mapper.MeetingWordMapper;
import com.jfzt.meeting.service.MeetingRecordService;
import com.jfzt.meeting.service.MeetingWordService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 针对表【meeting_word】的数据库操作Service实现
 * @author zilong.deng
 * @since 2024-06-18 12:05:57
 */
@Service
public class MeetingWordServiceImpl extends ServiceImpl<MeetingWordMapper, MeetingWord>
        implements MeetingWordService {
    @Resource
    private MeetingWordService meetingWordService;
    @Resource
    private MeetingRecordService meetingRecordService;

    @Override
    public Result <List<MeetingWord>> getMeetingWord(Long meetingRecordId) {
        try{
            List<MeetingWord> meetingWords = meetingWordService.lambdaQuery()
                    .eq(MeetingWord::getMeetingRecordId, meetingRecordId)
                    .list();
            return Result.success(meetingWords);

        }catch (Exception e){
            return Result.success();
        }

    }

    @Override
    public Result<Object> saveOrUpdateWord(MeetingWord meetingWord) {

        MeetingRecord meetingRecord = meetingRecordService.getById(meetingWord.getMeetingRecordId());
        if (meetingRecord == null) {
            return Result.fail("会议不存在");
        }

        List<MeetingWordDTO> meetingWordDTOList = meetingWord.getMeetingWordDTOList();
        meetingWordDTOList.stream().peek(meetingWordDTO -> {
            if (meetingWordDTO.getId() == null) {
                //新增内容
                meetingWord.setId(null);
                meetingWord.setContent(meetingWordDTO.getContent());
                meetingWord.setType(meetingWordDTO.getType());
                meetingWordService.save(meetingWord);
            } else{
                MeetingWord word = MeetingWord.builder()
                        .id(meetingWordDTO.getId())
                        .content(meetingWordDTO.getContent())
                        .type(meetingWordDTO.getType())
                        .build();
                meetingWordService.updateById(word);
        }
        }).toList();
            return Result.success("保存成功");
        }

    }





