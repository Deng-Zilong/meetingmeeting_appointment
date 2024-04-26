package com.jfzt.meeting.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author zhenxing.lu
 * @description 群组
 * @sine 2024-04-24 11:03:26
 */
@Data
@TableName("meeting_group")
public class MeetingGroupEntity implements Serializable {
    @Serial
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
