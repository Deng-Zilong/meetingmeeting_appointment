package com.jfzt.meeting.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户VO
 * @author chenyu.di
 * @since 2024-05-08 17:10
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysUserVO  {
    /**
     * 企微id
     */
    private String userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 权限
     */
    private String level;

}