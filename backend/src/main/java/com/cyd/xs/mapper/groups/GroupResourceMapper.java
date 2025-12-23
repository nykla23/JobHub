package com.cyd.xs.mapper.groups;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyd.xs.entity.Group.GroupResource;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GroupResourceMapper extends BaseMapper<GroupResource> {

    @Insert("""
        INSERT INTO group_resources
        (id, group_id, title, type, description, uploader_id,
         download_count, size, link, status, created_at)
        VALUES
        (#{id}, #{groupId}, #{title}, #{type}, #{description},
         #{uploaderId}, #{downloadCount}, #{size}, #{link},
         #{status}, #{createdAt})
    """)
    int insert(GroupResource resource);

    @Select("""
        SELECT *
        FROM group_resources
        WHERE group_id = #{groupId}
          AND status = 'approved'
        ORDER BY created_at DESC
        LIMIT #{limit}
    """)
    List<GroupResource> findResourcesByGroupId(
            @Param("groupId") Long groupId,
            @Param("limit") Integer limit
    );

    @Select("""
        SELECT COUNT(*)
        FROM group_resources
        WHERE group_id = #{groupId}
          AND status = 'approved'
    """)
    Long countResourcesByGroupId(@Param("groupId") Long groupId);
}
