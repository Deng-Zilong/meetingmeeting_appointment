package com.jfzt.meeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jfzt.meeting.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author zilong.deng
 * @description 针对表【sys_user】的数据库操作Mapper
 * @createDate 2024-05-06 16:47:54
 * @Entity com.jfzt.meeting.entity.SysUser
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 新增管理员,修改用户的权限等级为1
     * @param userId 用户id
     * @return int
     */
    int addAdmin(@Param("userId") String userId);


    /**
     * 删除管理员,修改用户的权限等级为2
     * @param userId 用户id
     * @return int
     */
    int deleteAdmin(@Param("userId") String userId);

    /**
     * 查询用户信息
     * @param userId 用户id
     * @return SysUser
     */
    SysUser selectByUserId(@Param("userId") String userId);


}




