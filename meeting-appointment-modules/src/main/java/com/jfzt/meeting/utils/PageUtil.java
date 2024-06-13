package com.jfzt.meeting.utils;

/**
 * @author: chenyu.di
 * @since: 2024-06-12 14:48
 */
import com.jfzt.meeting.entity.dto.PageDTO;
import com.jfzt.meeting.entity.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PageUtil {
    public static <T> PageVO<T> toPage(List<T> list, PageDTO PageDTO) {
        int startIndex = PageDTO.getCurrent() * PageDTO.getSize();
        int allSize = list.size();
        int dataSize = allSize - startIndex;
        List<T> listResult = list.subList(startIndex, dataSize - startIndex);
        PageVO<T> PageVO = new PageVO<>();
        PageVO.setSize(PageDTO.getSize());
        PageVO.setPageNum(PageDTO.getCurrent());
        PageVO.setTotal(allSize);
        PageVO.setDataList(listResult);
        return PageVO;
    }
}