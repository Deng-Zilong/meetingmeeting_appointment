package com.jfzt.meeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jfzt.meeting.entity.SysDepartmentUser;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zilong.deng
 * @description 针对表【sys_department_user(企微部门成员关联表)】的数据库操作Mapper
 * @createDate 2024-04-29 14:46:29
 * @Entity com.jfzt.meeting.entity.SysDepartmentUser
 */
@Mapper
public interface SysDepartmentUserMapper extends BaseMapper<SysDepartmentUser> {

    @Delete("delete from sys_department_user")
    int deleteAll();


}




