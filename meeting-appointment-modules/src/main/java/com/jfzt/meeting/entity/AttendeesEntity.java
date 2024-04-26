package com.jfzt.meeting.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author zhenxing.lu
 * @description :参会人员
 * @sine 2024-04-24 11:03:26
 */
@Data
@TableName("attendees")
public class AttendeesEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会议id
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
     * 参与人id
     */
    private Integer userId;
    /**
     * 会议预约id
     */
    private Long meetingRecordId;
    /**
     * 0未删除 1删除
     */
    private Integer isDelete;

}
