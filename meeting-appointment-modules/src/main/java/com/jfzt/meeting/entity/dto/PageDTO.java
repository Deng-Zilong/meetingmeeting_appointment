package com.jfzt.meeting.entity.dto;

import lombok.Data;

/**
 * @author: chenyu.di
 * @since: 2024-06-12 14:49
 */
@Data
public class PageDTO {
    /**
     * 当前页
     */
    private Integer current;
    /**
     * 每页显示条数
     */
    private Integer size;
}