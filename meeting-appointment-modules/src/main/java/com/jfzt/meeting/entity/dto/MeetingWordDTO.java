package com.jfzt.meeting.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.jfzt.meeting.entity.MeetingWord;
import com.jfzt.meeting.entity.MinutesPlan;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author: chenyu.di
 * @since: 2024-06-18 13:48
 */
@Data
public class MeetingWordDTO {

    /**
     * 主键id
     */
    @TableId
    private Integer id;
    /**
     * 文本内容
     */
    private String content;
    /**
     * 类型（1：本次目标、2.问题、3.项目未来的优化方向、4.迭代需求）
     */
    private Integer type;
}