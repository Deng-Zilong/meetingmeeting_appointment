package com.jfzt.meeting.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * 会议记录查询参数
 * @author zilong.deng
 * @since 2024-06-25 11:07:20
 */
@Data
public class RecordQueryParameters {
    /**
     * 用户id
     */
    private String userId;
    /**
     * 会议标题
     */
    private String title;
    /**
     * 创建人姓名
     */
    private String adminUserName;
    /**
     * 部门名称
     */
    private String department;

    /**
     * 会议室id
     */
    private Long meetingRoomId;
    /**
     * 开始日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    /**
     * 结束日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    /**
     * 每页条数
     */
    private Integer pageSize;
    /**
     * 当前页码
     */
    private Integer pageNum;
}
