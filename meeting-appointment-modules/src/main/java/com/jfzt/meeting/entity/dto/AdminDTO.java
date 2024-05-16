package com.jfzt.meeting.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 新增,删除管理员DTO
 * @author xuchang.yang
 * @since: 2024-05-15 10:50
 */
@Data
public class AdminDTO {

    /**
     * 用户
     */
    String userId;

    /**
     * 用户id数组
     */
    List<String> userIds;
}
