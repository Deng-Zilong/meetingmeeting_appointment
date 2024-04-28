package com.jfzt.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.jfzt.meeting.entity.SysUserEnterpriseEntity;


/**
 * @author zhenxing.lu
 * @description :用户企业微信表
 * @sine 2024-04-24 11:03:26
 */
public interface SysUserEnterpriseService extends IService<SysUserEnterpriseEntity> {

    //获取登录tocken
    String findTocken();
    //获取登录用户信息
    void findUserName(String access_token,String code);
    //获取二维码
    String findQrCode();
    //获取企业部门用户
    void findDepartment(String access_token, String code);
}

