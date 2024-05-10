package com.jfzt.meeting.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.jfzt.meeting.entity.UserGroup;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: chenyu.di
 * @since: 2024-04-29 16:56
 */
@Data
public class MeetingGroupDTO {
    /**
     * 群组Id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 群组名称
     */
    private String groupName;
    /**
     * 创建人Id
     */
    private String createdBy;
    /**
     * 创建人名称
     */
    private String userName;
    /**
     * 群组成员
     */
    private List<UserGroup> users = new ArrayList<>();

}