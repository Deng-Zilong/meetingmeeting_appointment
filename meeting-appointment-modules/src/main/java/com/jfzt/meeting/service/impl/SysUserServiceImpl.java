package com.jfzt.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.mapper.SysUserMapper;
import com.jfzt.meeting.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zilong.deng
 * @description 针对表【sys_user】的数据库操作Service实现
 * @createDate 2024-05-06 16:47:54
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService {

    /**
     * 定义权限等级(0超级管理员，1管理员，2员工)
     */
    public static final Integer SUPER_ADMIN_LEVEL = 0;
    public static final Integer ADMIN_LEVEL = 1;
    public static final Integer EMPLOYEE_LEVEL= 2;

    @Autowired
    private SysUserMapper sysUserMapper;


    /**
     * 获取所有不是管理员的企业微信用户的姓名
     * @param sysUser
     * @return
     */
    @Override
    public List<String> selectAll(SysUser sysUser) {
        List<SysUser> sysUsers = sysUserMapper.selectList(new QueryWrapper<>());
        return sysUsers.stream()
                .filter(user -> !ADMIN_LEVEL.equals(user.getLevel()) && !SUPER_ADMIN_LEVEL.equals(user.getLevel()))
                .map(SysUser::getUserName)
                .collect(Collectors.toList());
    }

    /**
     * 查询所有的管理员
     * @return
     */
    @Override
    public List<String> selectAdmin() {
        List<SysUser> sysUsers = sysUserMapper.selectList(new QueryWrapper<>());
        return sysUsers.stream()
                .filter(user -> ADMIN_LEVEL.equals(user.getLevel()) || SUPER_ADMIN_LEVEL.equals(user.getLevel()))
                .map(SysUser::getUserName)
                .collect(Collectors.toList());
    }

    /**
     * 修改用户权限等级
     * @param userName
     * @param level
     * @return
     */
    @Override
    public boolean updateLevel(String userName, Integer level) {
        return sysUserMapper.updateLevel(userName, level);
    }
}




