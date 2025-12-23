package com.cyd.xs.mapper;

import com.cyd.xs.entity.User.UserContent;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserContentMapper {

    // 获取推荐内容（按热度排序）
    @Select("SELECT uc.*, u.display_name as author_name, u.avatar_url " +
            "FROM user_contents uc " +
            "LEFT JOIN users u ON uc.user_id = u.id " +
            "WHERE uc.status = 'passed' " +
            "ORDER BY uc.hot_score DESC, uc.created_at DESC " +
            "LIMIT #{limit}")
    List<UserContent> findRecommendedContents(@Param("limit") int limit);

    // 分页获取推荐内容
    @Select("SELECT uc.*, u.display_name as author_name, u.avatar_url " +
            "FROM user_contents uc " +
            "LEFT JOIN users u ON uc.user_id = u.id " +
            "WHERE uc.status = 'passed' " +
            "ORDER BY uc.hot_score DESC, uc.created_at DESC " +
            "LIMIT #{offset}, #{pageSize}")
    List<UserContent> findRecommendedContentsByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    // 统计已发布内容数量
    @Select("SELECT COUNT(*) FROM user_contents WHERE status = 'passed'")
    Long countPublishedContents();
    // 根据标签推荐内容（基于 user_contents.tag）
    @Select("<script>" +
            "SELECT uc.*, u.display_name AS author_name, u.avatar_url " +
            "FROM user_contents uc " +
            "LEFT JOIN users u ON uc.user_id = u.id " +
            "WHERE uc.status = 'passed' " +
            "<if test='tags != null and tags.size() > 0'>" +
            " AND (" +
            "   <foreach collection='tags' item='tag' separator=' OR '>" +
            "     FIND_IN_SET(#{tag}, uc.tag)" +
            "   </foreach>" +
            " )" +
            "</if>" +
            "ORDER BY uc.hot_score DESC, uc.created_at DESC " +
            "LIMIT #{limit}" +
            "</script>")
    List<UserContent> findRecommendedContentsByTags(
            @Param("tags") List<String> tags,
            @Param("limit") int limit
    );

    // 搜索用户内容（支持分页和排序）
    @Select("<script>" +
            "SELECT id, title, LEFT(content, 200) AS content_summary, " +
            "       view_count, like_count, created_at " +
            "FROM user_contents " +
            "WHERE status = 'passed' " +
            "  AND (title LIKE CONCAT('%', #{keyword}, '%') " +
            "       OR content LIKE CONCAT('%', #{keyword}, '%')) " +
            "  <if test='type != null'>" +
            "    AND type = #{type} " +
            "  </if>" +
            "ORDER BY " +
            "  <choose>" +
            "    <when test='sort == \"hot\"'>" +
            "      (view_count + like_count) DESC, created_at DESC" +
            "    </when>" +
            "    <when test='sort == \"time\"'>" +
            "      created_at DESC" +
            "    </when>" +
            "    <otherwise>" +
            "      (view_count + like_count) DESC, created_at DESC" +
            "    </otherwise>" +
            "  </choose>" +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Map<String, Object>> searchUserContents(@Param("keyword") String keyword,
                                                 @Param("type") String type,
                                                 @Param("sort") String sort,
                                                 @Param("offset") int offset,
                                                 @Param("pageSize") int pageSize);

    // 统计用户内容搜索结果数量
    @Select("<script>" +
            "SELECT COUNT(*) " +
            "FROM user_contents " +
            "WHERE status = 'passed' " +
            "  AND (title LIKE CONCAT('%', #{keyword}, '%') " +
            "       OR content LIKE CONCAT('%', #{keyword}, '%')) " +
            "  <if test='type != null'>" +
            "    AND type = #{type} " +
            "  </if>" +
            "</script>")
    Long countSearchUserContents(@Param("keyword") String keyword,
                                 @Param("type") String type);
}