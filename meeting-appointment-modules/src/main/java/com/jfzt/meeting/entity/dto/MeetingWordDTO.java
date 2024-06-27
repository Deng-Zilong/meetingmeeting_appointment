package com.jfzt.meeting.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
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
    private Integer id;
    /**
     * 文本内容
     */
    private String content;
    /**
     * 标题
     */
    private String title;
    /**
     * 标题等级（0：非标题、1：一级标题、2：二级标题、3：三级标题）
     */
    private Integer level;

}