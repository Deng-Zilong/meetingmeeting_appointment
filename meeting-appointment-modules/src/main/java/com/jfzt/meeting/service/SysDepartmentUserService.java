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
 * 针对表【sys_department_user(企微部门成员关联表)】的数据库操作Service
 * @author zilong.deng
 * @since  2024-04-29 14:46:29
 */
public interface SysDepartmentUserService extends IService<SysDepartmentUser> {

    /**
     * 查找用户token
     * @return token
     * @throws WxErrorException 微信异常
     **/
    String findTocken () throws WxErrorException;


    /**
     * 查找用户信息
     * @param accessToken Token
     * @param code code
     * @return WxCpUser
     * @throws WxErrorException 微信异常
     **/
    WxCpUser findUserName (String accessToken, String code) throws WxErrorException;

    /**
     * 查找企业微信部门
     * @return 部门
     * @throws WxErrorException 微信异常
     **/
    Long findDepartment () throws WxErrorException;

    /**
     * 查找企业微信用户部门关联表
     *
     * @param departmentLength 部门长度
     * @return
     * @throws WxErrorException         WxErrorException
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     */
    Boolean findDepartmentUser (Long departmentLength) throws WxErrorException, NoSuchAlgorithmException;

    /**
     * 获取部门成员树
     * @return 部门成员树
     */
    Result<List<SysDepartment>> gainUsers();
}
