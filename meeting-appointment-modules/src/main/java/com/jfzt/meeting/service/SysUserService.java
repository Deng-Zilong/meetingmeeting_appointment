package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.entity.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zilong.deng
 * @description 针对表【sys_user】的数据库操作Service
 * @createDate 2024-05-06 16:47:54
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 获取所有的企业微信用户的姓名
     * @param sysUser
     * @return
     */
    List<String> selectAll(SysUser sysUser);

    /**
     * 根据权限等级查询企微用户是否为管理员
     * @return
     */
    List<String> selectAdmin();

    /**
     * 修改用户的权限等级
     * @param userName
     * @param level
     * @return
     */
    boolean updateLevel(@Param("userName") String userName, @Param("level") Integer level);


}
