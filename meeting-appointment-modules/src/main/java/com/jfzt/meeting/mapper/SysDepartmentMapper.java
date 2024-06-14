package com.jfzt.meeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jfzt.meeting.entity.SysDepartment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *  针对表【sys_department】的数据库操作Mapper
 * @author zhenxing.lu
 * @since  2024-04-28 16:04:11
 */
@Mapper
public interface SysDepartmentMapper extends BaseMapper<SysDepartment> {


    /**
     * 一次性插入部门信息
     * @param sysDepartmentLists 部门信息
     */
    void insertAll(List<SysDepartment> sysDepartmentLists);

    /**
     * 删除非0的部门
     */
    @Delete("delete from sys_department where department_id !=0")
    void deleteAll();

    /**
     * 通过用户id查看部门
     * @param userId 用户id
     * @return
     */
    SysDepartment findDepartment(String userId);
}




