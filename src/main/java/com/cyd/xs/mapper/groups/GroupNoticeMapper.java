package com.cyd.xs.mapper.groups;

import com.cyd.xs.entity.User.Group.GroupNotice;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GroupNoticeMapper {

    @Select("SELECT * FROM group_notices WHERE group_id = #{groupId} ORDER BY publish_time DESC LIMIT 10")
    List<GroupNotice> findNoticesByGroupId(String groupId);

    @Select("SELECT COUNT(*) FROM group_notices WHERE group_id = #{groupId}")
    Long countNoticesByGroupId(String groupId);
}