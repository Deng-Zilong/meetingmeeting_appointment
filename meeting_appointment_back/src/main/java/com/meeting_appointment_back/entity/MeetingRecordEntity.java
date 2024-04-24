package com.meeting_appointment_back.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 类介绍:会议预约记录表
 *
 * @author zhenxing.lu
 * @sine 2024-04-24 11:03:26
 */
@Data
@TableName("meeting_record")
public class MeetingRecordEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 预约id
     */
    @TableId
    private Long id;
    /**
     * 添加时间
     */
    private Date gmtCreate;
    /**
     * 修改时间
     */
    private Date gmtModified;
    /**
     * 会议主题
     */
    private String title;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 日期
     */
    private Date dateTime;
    /**
     * 地点
     */
    private String place;
    /**
     * 会议概述
     */
    private String description;
    /**
     * 会议室名称ID
     */
    private Integer meetingRoomId;
    /**
     * 会议状态1空闲2使用中3暂停使用
     */
    private Integer status;
    /**
     * 创建人id
     */
    private Integer adminid;
    /**
     * 0未删除 1删除
     */
    private Integer isDelete;

}
