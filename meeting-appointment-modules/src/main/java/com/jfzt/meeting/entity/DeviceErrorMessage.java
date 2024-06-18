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
 * 报损信息(DeviceErrorMessage)实体类
 * @author chenyu.di
 * @since 2024-06-12 11:40
 */
@TableName(value = "device_error_message")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DeviceErrorMessage implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 报损信息id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 设备id
     */
    private Long deviceId;
    /**
     * 报损人id
     */
    private String userId;
    /**
     * 报损人姓名
     */
    @TableField(exist = false)
    private String userName;
    /**
     * 报损信息
     */
    private String info;
    /**
     * 添加时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreate;

}