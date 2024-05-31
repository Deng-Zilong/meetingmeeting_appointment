package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jfzt.meeting.common.Result;
import com.jfzt.meeting.entity.SysDepartment;
import com.jfzt.meeting.entity.SysDepartmentUser;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.bean.WxCpUser;

import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author zilong.deng
 * @description 针对表【sys_department_user(企微部门成员关联表)】的数据库操作Service
 * @createDate 2024-04-29 14:46:29
 */
public interface SysDepartmentUserService extends IService<SysDepartmentUser> {

    /**
     * 查找用户token
     */
    String findTocken () throws WxErrorException;

    /**
     * 查找用户信息
     */
    WxCpUser findUserName (String accessToken, String code) throws WxErrorException;

    /**
     * 查找企业微信部门
     */
    Long findDepartment () throws WxErrorException;

    /**
     * 查找企业微信用户部门关联表
     */
    void findDepartmentUser (Long departmentLength) throws WxErrorException, NoSuchAlgorithmException;

    /**
     * 获取部门成员树
     */
    Result<List<SysDepartment>> gainUsers();
}
