package com.jfzt.meeting.constant;

/**
 * 信息提示类
 *
 * @Author: chenyu.di
 * @since: 2024-05-08 18:06
 */
public class MessageConstant {
    public static final String EXCEPTION_TYPE = "======错误原因为：{}";
    public static final String SAME_NAME = "群组名称已存在";
    public static final String START_TIME_GT_END_TIME = "结束时间不能早于等于开始时间";
    public static final String DELETE_FAIL = "删除失败！";
    public static final String DELETE_SUCCESS = "删除成功！";
    public static final String CANCEL_SUCCESS = "取消成功！";
    public static final String CANCEL_FAIL = "取消失败！";
    public static final String START_TIME_LT_NOW = "开始时间不能小于当前时间";
    public static final String GROUP_NOT_EXIST = "群组不存在";
    public static final String USER_ERROR = "用户错误！";
    public static final String ADD_SUCCESS = "添加成功！";
    public static final String UPDATE_SUCCESS = "修改成功！";
    public static final String NO_USER = "成员不能为空！";
    public static final String CREATE_SUCCESS = "创建成功！";
    public static final String OCCUPIED = "当前会议室已被占用！";

    /**
     * 权限处理提示信息
     */
    public static final String UPDATE_FAIL = "修改会议室状态失败!";
    public static final String SUCCESS = "成功！";
    public static final String FAIL = "失败！";

    /**
     * 定义权限等级(0超级管理员，1管理员，2员工)
     */
    public static final Integer SUPER_ADMIN_LEVEL = 0;
    public static final Integer ADMIN_LEVEL = 1;
    public static final Integer EMPLOYEE_LEVEL = 2;


}