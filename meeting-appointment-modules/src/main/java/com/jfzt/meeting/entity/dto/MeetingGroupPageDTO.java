package com.jfzt.meeting.entity.dto;

import lombok.Data;

/**
 * 会议分组分页DTO
 * @author chenyu.di
 * @since 2024-05-14 10:50
 */
@Data
public class MeetingGroupPageDTO {
    /**
     * 当前页
     */
    private Integer pageNum;
    /**
     * 每页显示条数
     */
    private Integer pageSize;
    /**
     * 用户id
     */
    private String userId;

}