package com.jfzt.meeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jfzt.meeting.entity.UserGroup;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zilong.deng
 * @description 针对表【user_group(群组人员关联表)】的数据库操作Mapper
 * @createDate 2024-04-28 11:53:18
 * @Entity com.jfzt.meeting.entity.UserGroup
 */
@Mapper
public interface UserGroupMapper extends BaseMapper<UserGroup> {

}




