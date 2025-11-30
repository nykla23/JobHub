package com.cyd.xs.mapper.groups;

import com.cyd.xs.entity.User.Group.Group;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GroupMapper {

    @Insert("INSERT INTO groups (id, name, tags, member_count, activity_type, intro, avatar, creator, create_time, status) " +
            "VALUES (#{id}, #{name}, #{tags}, #{memberCount}, #{activityType}, #{intro}, #{avatar}, #{creator}, #{createTime}, #{status})")
    int insert(Group group);

    @Select("SELECT * FROM groups WHERE id = #{id}")
    Group findById(String id);

    @Select("<script>" +
            "SELECT * FROM groups WHERE status = 'approved' " +
            "<if test='keyword != null'> AND (name LIKE CONCAT('%', #{keyword}, '%') OR tags LIKE CONCAT('%', #{keyword}, '%'))</if>" +
            "<if test='tag != null'> AND tags LIKE CONCAT('%', #{tag}, '%')</if>" +
            " ORDER BY " +
            "<if test='sort == \"member\"'> member_count DESC </if>" +
            "<if test='sort == \"activity\"'> create_time DESC </if>" +
            " LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Group> findGroups(@Param("keyword") String keyword, @Param("tag") String tag,
                           @Param("sort") String sort, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM groups WHERE status = 'approved'")
    Long countGroups();

    @Update("UPDATE groups SET member_count = member_count + #{increment} WHERE id = #{groupId}")
    int updateMemberCount(@Param("groupId") String groupId, @Param("increment") int increment);
}