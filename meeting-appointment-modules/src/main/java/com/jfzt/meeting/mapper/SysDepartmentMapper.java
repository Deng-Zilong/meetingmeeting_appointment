package com.jfzt.meeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jfzt.meeting.entity.SysDepartment;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * @author zhenxing.lu
 * @description 针对表【sys_department】的数据库操作Mapper
 * @createDate 2024-04-28 16:04:11
 * @Entity com.jfzt.meeting.entity.SysDepartment
 */
@Mapper
public interface SysDepartmentMapper extends BaseMapper<SysDepartment> {


    /**
     * 一次性插入部门信息
     * @param sysDepartmentLists
     */
    void insertAll(List<SysDepartment> sysDepartmentLists);
}




