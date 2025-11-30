package com.cyd.xs.mapper.groups;

import com.cyd.xs.entity.User.Group.GroupResource;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GroupResourceMapper {

    @Insert("INSERT INTO group_resources (id, group_id, title, description, file_url, file_name, file_size, tag, uploader, upload_time, download_count, status) " +
            "VALUES (#{id}, #{groupId}, #{title}, #{description}, #{fileUrl}, #{fileName}, #{fileSize}, #{tag}, #{uploader}, #{uploadTime}, #{downloadCount}, #{status})")
    int insert(GroupResource resource);

    @Select("SELECT * FROM group_resources WHERE group_id = #{groupId} AND status = 'approved' ORDER BY upload_time DESC LIMIT #{offset}, #{pageSize}")
    List<GroupResource> findResourcesByGroupId(@Param("groupId") String groupId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM group_resources WHERE group_id = #{groupId} AND status = 'approved'")
    Long countResourcesByGroupId(String groupId);
}