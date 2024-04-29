package com.jfzt.meeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jfzt.meeting.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zilong.deng
 * @description 针对表【sys_user(企微部门成员表)】的数据库操作Mapper
 * @createDate 2024-04-28 16:03:44
 * @Entity com.jfzt.meeting.entity.SysUser
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

}




