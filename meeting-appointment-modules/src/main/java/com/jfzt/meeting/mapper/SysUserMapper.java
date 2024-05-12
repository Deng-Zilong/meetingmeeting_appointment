package com.jfzt.meeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jfzt.meeting.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zilong.deng
 * @description 针对表【sys_user】的数据库操作Mapper
 * @createDate 2024-05-06 16:47:54
 * @Entity com.jfzt.meeting.entity.SysUser
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    
    /**
     * 修改用户的权限等级
     * @param userId
     * @param level
     * @return
     */
    int updateLevel(@Param("userId") String userId, @Param("level") Integer level);

}




