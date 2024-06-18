package com.jfzt.meeting.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会议纪要迭代内容(MinutesPlan)表实体类
 * @author chenyu.di
 * @since 2024-06-17 14:51
 */
@TableName(value = "minutes_plan")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class MinutesPlan implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 迭代内容id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 会议纪要id
     */
    private Integer minutesId;
    /**
     * 迭代内容
     */
    private String plan;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 添加时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreate;
    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtModified;
}