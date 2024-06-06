package com.jfzt.meeting.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 部门表(SysDepartment)实体类
 * @author zilong.deng
 * @since 2024-06-05 09:43:09
 */
@TableName(value = "sys_department")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SysDepartment implements Serializable {
    /**
     *id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 企微部门id
     */
    private Long departmentId;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 父部门id(根部门为0)
     */
    private Long parentId;

    /**
     * 子部门
     */
    @TableField(exist = false)
    private List<SysDepartment> childrenPart;

    @TableField(exist = false)
    private List<SysUser> treeUsers;

    /**
     * 0未删除 1删除
     */
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
        SysDepartment other = (SysDepartment) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getDepartmentId() == null ? other.getDepartmentId() == null : this.getDepartmentId().equals(other.getDepartmentId()))
                && (this.getDepartmentName() == null ? other.getDepartmentName() == null : this.getDepartmentName().equals(other.getDepartmentName()))
                && (this.getParentId() == null ? other.getParentId() == null : this.getParentId().equals(other.getParentId()));
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getDepartmentId() == null) ? 0 : getDepartmentId().hashCode());
        result = prime * result + ((getDepartmentName() == null) ? 0 : getDepartmentName().hashCode());
        result = prime * result + ((getParentId() == null) ? 0 : getParentId().hashCode());
        return result;
    }

    @Override
    public String toString () {
        return getClass().getSimpleName() +
                " [" +
                "Hash = " + hashCode() +
                ", id=" + id +
                ", departmentId=" + departmentId +
                ", departmentName=" + departmentName +
                ", parentId=" + parentId +
                ", serialVersionUID=" + serialVersionUID +
                "]";
    }
}