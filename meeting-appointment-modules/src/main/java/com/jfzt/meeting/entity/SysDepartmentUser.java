package com.jfzt.meeting.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * (SysDepartmentUser)用户部门关联表实体类
 * @author zilong.deng
 * @since 2024-06-05 09:42:33
 */
@TableName(value = "sys_department_user")
@Data
public class SysDepartmentUser implements Serializable {
    /**
     *id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 成员id（非成员user_id）
     */
    private String userId;

    /**
     * 部门id
     */
    private Long departmentId;

    @TableLogic
    private Integer isDeleted;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals (Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        SysDepartmentUser other = (SysDepartmentUser) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
                && (this.getDepartmentId() == null ? other.getDepartmentId() == null : this.getDepartmentId().equals(other.getDepartmentId()));
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getDepartmentId() == null) ? 0 : getDepartmentId().hashCode());
        return result;
    }

    @Override
    public String toString () {
        return getClass().getSimpleName() +
                " [" +
                "Hash = " + hashCode() +
                ", id=" + id +
                ", userId=" + userId +
                ", departmentId=" + departmentId +
                ", serialVersionUID=" + serialVersionUID +
                "]";
    }
}