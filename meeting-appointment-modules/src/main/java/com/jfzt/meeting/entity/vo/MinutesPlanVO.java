package com.jfzt.meeting.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author zilong.deng
 * @since 2024-06-18 14:55:56
 */
@Data
public class MinutesPlanVO {

    /**
     * 迭代内容id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 会议纪要id
     */
    private Long minutesId;
    /**
     * 迭代内容
     */
    private String plan;
    /**
     * 状态
     */
    private Integer status;
}
