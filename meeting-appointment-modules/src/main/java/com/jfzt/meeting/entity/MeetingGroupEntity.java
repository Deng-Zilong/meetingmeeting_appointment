package com.jfzt.meeting.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 类介绍:群组表
 *
 * @author zhenxing.lu
 * @sine 2024-04-24 11:03:26
 */
@Data
@TableName("meeting_group")
public class MeetingGroupEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 群组id
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
     * 创建人id
     */
    private Integer adminid;
    /**
     * 群组名称
     */
    private String name;

}
