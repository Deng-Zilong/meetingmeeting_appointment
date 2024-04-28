package com.jfzt.meeting.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 企业微信表
 * 
 * @author ${author}
 * @email ${email}
 * @date 2024-04-25 15:03:36
 */
@Data
@TableName("sys_enterprise")
public class SysEnterpriseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 企业ID
	 */
	@TableId
	private Long enterpriseId;
	/**
	 * 企业名称
	 */
	private String enterpriseName;
	/**
	 * 企业唯一corpid
	 */
	private String corpid;
	/**
	 * 企业应用ID
	 */
	private String agentid;
	/**
	 * 企业应用秘钥
	 */
	private String corpsecret;
	/**
	 * 回调URL
	 */
	private String redirectUri;
	/**
	 * 删除标志
	 */
	private String delFlag;
	/**
	 * 创建者
	 */
	private String createBy;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新者
	 */
	private String updateBy;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 备注
	 */
	private String remark;

}
