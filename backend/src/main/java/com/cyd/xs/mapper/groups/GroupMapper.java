package com.cyd.xs.mapper.groups;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyd.xs.entity.Group.Group;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GroupMapper extends BaseMapper<Group> {

    /**
     * 查询小组详情（显式字段，禁止 SELECT *）
     */
    @Select("""
        SELECT
            id,
            name,
            intro,
            avatar,
            activity_type AS activityType,
            creator_id AS creatorId,
            status,
            created_at AS createdAt,
            member_count AS memberCount,
            tags
        FROM `groups`
        WHERE id = #{id}
        """)
    Group findById(@Param("id") Long id);

    /**
     * 小组列表（带标签、成员数、是否加入）
     */
    @Select("""
<script>
SELECT
  g.id,
  g.name,
  g.intro,
  g.avatar,
  g.activity_type AS activityType,
  g.creator_id AS creatorId,
  g.status,
  g.created_at AS createdAt,
  g.member_count AS memberCount,
  g.tags
FROM `groups` g
WHERE g.status = 'ACTIVE'
<if test="keyword != null and keyword != ''">
  AND (g.name LIKE CONCAT('%', #{keyword}, '%')
       OR g.intro LIKE CONCAT('%', #{keyword}, '%'))
</if>
<if test="tag != null and tag != ''">
  AND g.tags LIKE CONCAT('%', #{tag}, '%')
</if>
ORDER BY g.member_count DESC
LIMIT #{offset}, #{pageSize}
</script>
""")
    List<Group> findGroups(
            @Param("keyword") String keyword,
            @Param("tag") String tag,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );


    /**
     * 小组数量统计
     */
    @Select("""
        <script>
        SELECT COUNT(*)
        FROM `groups` g
        WHERE g.status = 'active'
        <if test="keyword != null and keyword != ''">
            AND (g.name LIKE CONCAT('%', #{keyword}, '%')
             OR g.intro LIKE CONCAT('%', #{keyword}, '%'))
        </if>
        <if test="tag != null and tag != ''">
            AND g.id IN (
                SELECT group_id FROM group_tags WHERE tag = #{tag}
            )
        </if>
        </script>
        """)
    Long countGroups(
            @Param("keyword") String keyword,
            @Param("tag") String tag
    );

    /**
     * 更新成员数
     */
    @Update("""
        UPDATE `groups`
        SET member_count = member_count + #{increment}
        WHERE id = #{groupId}
        """)
    int updateMemberCount(
            @Param("groupId") Long groupId,
            @Param("increment") int increment
    );

    /**
     * 更新小组状态
     */
    @Update("""
        UPDATE `groups`
        SET status = #{status}
        WHERE id = #{groupId}
        """)
    int updateStatus(
            @Param("groupId") Long groupId,
            @Param("status") String status
    );
}
