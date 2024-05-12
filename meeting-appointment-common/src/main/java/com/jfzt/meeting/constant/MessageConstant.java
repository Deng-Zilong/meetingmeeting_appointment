package com.jfzt.meeting.constant;

/**
 * 信息提示类
 * @Author: chenyu.di
 * @since: 2024-05-08 18:06
 */
public class MessageConstant {
    public static final String SAME_NAME = "群组名称已存在";
    public static final String START_TIME_GT_END_TiME = "结束时间不能早于开始时间";
    public static final String DELETE_FAIL = "删除失败！";
    public static final String START_TIME_LT_NOW = "开始时间不能小于当前时间";
    public static final String GROUP_NOT_EXIST = "群组不存在";
    public static final String USER_ERROR = "用户错误！";
    public static final String ADD_SUCCESS = "添加成功！";
    public static final String UPDATE_SUCCESS = "添加成功！";
    public static final String NO_USER = "成员不能为空！";

    /**
     * 权限处理提示信息
     */
    public static final String HAVE_NO_AUTHORITY = "请联系管理员获取权限!";
    public static final String SUCCESS = "成功！";
    public static final String FAIL = "失败！";

    /**
     * 定义权限等级(0超级管理员，1管理员，2员工)
     */
    public static final Integer SUPER_ADMIN_LEVEL = 0;
    public static final Integer ADMIN_LEVEL = 1;
    public static final Integer EMPLOYEE_LEVEL = 2;


}