package com.meeting_appointment_back.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 类介绍:群组人员表
 *
 * @author zhenxing.lu
 * @sine 2024-04-24 11:03:26
 */
@Data
@TableName("user_group")
public class UserGroupEntity implements Serializable {
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
     * 用户id
     */
    private Integer userId;
    /**
     * 群组名称
     */
    private String userName;
    /**
     * 0未删除 1删除
     */
    private Integer isDelete;

}
