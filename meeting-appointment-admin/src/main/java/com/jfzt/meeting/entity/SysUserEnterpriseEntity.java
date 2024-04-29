package com.jfzt.meeting.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户企业微信表
 * 
 * @author ${author}
 * @email ${email}
 * @date 2024-04-25 15:03:36
 */
@Data
@TableName("sys_user_enterprise")
public class SysUserEnterpriseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户ID，关联sys_user的id
	 */
	@TableId
	private Long userId;
	/**
	 * 企业ID，关联sys_enterprise的id
	 */
	private Long enterpriseId;
	/**
	 * 企业微信的企业ID
	 */
	private String enterpriseCorpId;
	/**
	 * 企业名称
	 */
	private String enterpriseName;
	/**
	 * 企业微信用户ID（字符串）
	 */
	private String enterpriseUserId;
	/**
	 * 企业微信用户昵称
	 */
	private String enterpriseNickname;
	/**
	 * 企业微信部门ID
	 */
	private Long enterpriseDeptId;
	/**
	 * 企业微信部门名称
	 */
	private String enterpriseDeptName;

}
