package com.jfzt.meeting.entity.vo;

import com.jfzt.meeting.entity.MeetingGroup;
import com.jfzt.meeting.entity.SysDepartment;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.UserGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @Author: chenyu.di
 * @since: 2024-04-29 16:33
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MeetingGroupVO extends MeetingGroup {

    /**
     * 创建人姓名
     */
    private String userName;
    /**
     * 群组成员
     */
    private Set<SysUserVO> users = new LinkedHashSet<>();




}