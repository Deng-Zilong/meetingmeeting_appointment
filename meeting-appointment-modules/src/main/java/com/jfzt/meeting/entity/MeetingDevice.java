package com.jfzt.meeting.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * 设备(MeetingDevice)实体类
 * @author chenyu.di
 * @since 2024-06-12 10:54
 */
@TableName(value = "meeting_device")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class MeetingDevice implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 设备id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 会议室id
     */
    private Long roomId;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 停止报损（1：禁止报损、0：可以报损）
     */
    private Integer stopSend;
    /**
     * 设备状态（1：修复、0：禁用）
     */
    private Integer status;
    /**
     * 程度（报损次数）
     */
    private Integer extent;
    /**
     * 创建人id
     */
    private String userId;
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