package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.SysUser;
import com.jfzt.meeting.entity.vo.SysUserVO;
import com.jfzt.meeting.entity.vo.LoginVo;
import org.apache.ibatis.annotations.Param;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * @author zilong.deng
 * @description 针对表【sys_user】的数据库操作Service
 * @createDate 2024-05-06 16:47:54
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 获取所有的企业微信用户的姓名
     */
    List<String> selectAll(SysUser sysUser);

    /**
     * 根据权限等级查询企微用户是否为管理员
     */
    List<String> selectAdmin();

    /**
     * 修改用户的权限等级
     */
    boolean updateLevel(@Param("userName") String userName, @Param("level") Integer level);


    BufferedImage getCaptcha(String uuid);

    SysUser findUser(LoginVo loginVo);

    /**
     * @Description 模糊查询成员
     * @Param [name]
     * @return com.jfzt.meeting.common.Result<java.util.List<com.jfzt.meeting.entity.vo.SysUserVO>>
     */
    Result<List<SysUserVO>> findByName(String name);
}
