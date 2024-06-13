package com.jfzt.meeting.entity.vo;

/**
 * @author: chenyu.di
 * @since: 2024-06-12 14:58
 */
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
public class PageVO<T> {
    /**
     * 每页的大小
     */
    private long size;

    /**
     * 当前是第几页
     */
    private long pageNum;

    /**
     * 总的数据个数
     */
    private long total;

    /**
     * 数据列表
     */
    private List<T> dataList;
}