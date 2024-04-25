package com.jfzt.meeting.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 类介绍:会议室表
 *
 * @author zhenxing.lu
 * @sine 2024-04-24 11:03:26
 */
@Data
@TableName("meeting_room")
public class MeetingRoomEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
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
     * 会议室名称
     */
    private String roomName;
    /**
     * 会议室位置
     */
    private String location;
    /**
     * 容量
     */
    private Integer number;
    /**
     * 设备
     */
    private String tool;
    /**
     * 会议状态1空闲2使用中3暂停使用
     */
    private Integer status;
    /**
     * 0未删除 1删除
     */
    private Integer isDelete;

}
