package com.cyd.xs.mapper.groups;

import com.cyd.xs.entity.User.Group.GroupDynamic;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GroupDynamicMapper {

    @Insert("INSERT INTO group_dynamics (id, group_id, user_id, nickname, avatar, title, content, image_urls, tags, like_count, comment_count, publish_time, status) " +
            "VALUES (#{id}, #{groupId}, #{userId}, #{nickname}, #{avatar}, #{title}, #{content}, #{imageUrls}, #{tags}, #{likeCount}, #{commentCount}, #{publishTime}, #{status})")
    int insert(GroupDynamic dynamic);

    @Select("SELECT * FROM group_dynamics WHERE group_id = #{groupId} AND status = 'approved' ORDER BY publish_time DESC LIMIT #{offset}, #{pageSize}")
    List<GroupDynamic> findDynamicsByGroupId(@Param("groupId") String groupId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM group_dynamics WHERE group_id = #{groupId} AND status = 'approved'")
    Long countDynamicsByGroupId(String groupId);
}