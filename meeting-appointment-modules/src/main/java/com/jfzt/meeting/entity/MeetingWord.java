package com.jfzt.meeting.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jfzt.meeting.entity.dto.MeetingWordDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bouncycastle.pqc.crypto.rainbow.Layer;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 会议纪要-word
 * @author zilong.deng
 * @since 2024-06-18 12:05:03
 */
@TableName(value = "meeting_word")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class MeetingWord implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 会议id
     */
    private Long meetingRecordId;

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

    /**
     * 父id
     */
    private Integer parentId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtModified;

    /**
     * 子标题
     */
    @TableField(exist = false)
    private List<MeetingWord> childrenPart;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}