package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.MeetingRecord;
import com.jfzt.meeting.entity.MeetingWord;
import com.jfzt.meeting.mapper.MeetingWordMapper;
import com.jfzt.meeting.service.MeetingRecordService;
import com.jfzt.meeting.service.MeetingWordService;
import com.jfzt.meeting.service.MinutesPlanService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    @Resource
    private MinutesPlanService minutesPlanService;

    /**
     * 根据会议纪要id和用户id获取会议纪要
     * @param meetingRecordId 会议纪要id
     * @param userId 用户id
     * @return 会议纪要结果
     */
    @Override
    @Transactional
    public Result<List<MeetingWord>> getMeetingWord (Long meetingRecordId, String userId) {
        try {
            //查询标题是否存在
            List<MeetingWord> meetingWords = meetingWordService.lambdaQuery()
                    .eq(MeetingWord::getMeetingRecordId, meetingRecordId)
                    .eq(MeetingWord::getUserId, userId)
                    .list();
            //创建默认标题
            if (meetingWords.isEmpty()) {
                //标题
                ArrayList<MeetingWord> titleList = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    MeetingWord defaultTitle = MeetingWord.builder()
                            .meetingRecordId(meetingRecordId)
                            .userId(userId)
                            .content("")
                            .level(1)
                            .gmtCreate(LocalDateTime.now())
                            .gmtModified(LocalDateTime.now())
                            .parentId(0L)
                            .build();
                    switch (i) {
                        case 0:
                            defaultTitle.setTitle("本次目标:");
                            titleList.add(defaultTitle);

                            break;
                        case 1:
                            defaultTitle.setTitle("所遇问题:");
                            titleList.add(defaultTitle);
                            break;
                        case 2:
                            defaultTitle.setTitle("未来优化方向:");
                            titleList.add(defaultTitle);
                            break;
                        case 3:
                            defaultTitle.setTitle("迭代需求:");
                            titleList.add(defaultTitle);
                            break;
                    }
                }
                saveBatch(titleList);
                meetingWords.addAll(titleList);
            }

            //查询子标题
            List<MeetingWord> wordList = meetingWords.stream()
                    .filter(meetingWord -> meetingWord.getParentId() != null && meetingWord.getParentId() == 0)
                    .sorted((node1, node2) -> Math.toIntExact(node1.getId() - node2.getId()))
                    .peek(meetingWord -> {
                        meetingWord.setChildrenPart(getChildren(meetingWord, meetingWords));
                        if (meetingWord.getChildrenPart().isEmpty()) {
                            MeetingWord word = MeetingWord.builder()
                                    .content(" ")
                                    .level(0)
                                    .parentId(meetingWord.getId())
                                    .build();
                            List<MeetingWord> words = new ArrayList<>();
                            words.add(word);
                            meetingWord.setChildrenPart(words);
                        };
                    })
                    .toList();
            return Result.success(wordList);
        } catch (Exception e) {
            log.error("获取会议内容失败", e);
            return Result.success();
        }

    }

    /**
     * 递归查询子标题
     * @param root 当前节点
     * @param all 查询集合
     * @return 子标题集合
     */
    private List<MeetingWord> getChildren(MeetingWord root, List<MeetingWord> all) {
        return all.stream()
                .filter(meetingWord -> Objects.equals(meetingWord.getParentId(), root.getId()))
                .peek(childrenNode -> childrenNode.setChildrenPart(getChildren(childrenNode, all)))
                .collect(Collectors.toList());
    }


    /**
     * 保存或更新会议纪要
     * @param meetingWord 会议纪要
     * @return 保存结果
     */
    @Override
    public Result<Object> saveOrUpdateWord (MeetingWord meetingWord) {


        if (meetingWord.getId() != null) {
            updateById(meetingWord);
            return Result.success();
        }
        MeetingRecord meetingRecord = meetingRecordService.getById(meetingWord.getMeetingRecordId());
        if (meetingRecord == null) {
            return Result.fail("会议不存在");
        }
        if (meetingWord.getContent() == null || meetingWord.getContent().isEmpty()) {
            meetingWord.setContent("");
        }
        Long parentId = meetingWord.getParentId();
        if (parentId == null) {
            meetingWord.setParentId(0L);
        }
        save(meetingWord);

        return Result.success("保存成功");
    }

    /**
     * 删除会议纪要
     * @param planId Excel计划id
     * @param contentId Word内容标题id
     * @return 删除结果
     */
    @Override
    @Transactional
    public Result<Object> deleteWordOrPlan (Long planId, Long contentId) {
        if (contentId == null && planId == null) {
            return Result.success();
        }
        List<MeetingWord> contents = meetingWordService.lambdaQuery()
                .eq(MeetingWord::getParentId, contentId)
                .list();
        meetingWordService.removeBatchByIds(contents);
        meetingWordService.removeById(contentId);
        minutesPlanService.removeById(planId);
        return Result.success();
    }

}





