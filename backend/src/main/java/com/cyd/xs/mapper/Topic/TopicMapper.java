package com.cyd.xs.mapper.Topic;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyd.xs.entity.Topic.Topic;
import org.apache.ibatis.annotations.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TopicMapper extends BaseMapper<Topic> {

    /**
     * 插入话题
     */
    @Insert("INSERT INTO topics (title, level, tag, participant_count, interactive_count, latest_reply_time, guide_text, host, created_at) " +
            "VALUES (#{title}, #{level}, #{tag}, #{participantCount}, #{interactiveCount}, #{latestReplyTime}, #{guideText}, #{host}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(Topic topic);

    @Select("SELECT * FROM topics WHERE title LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%') ORDER BY ${sort} DESC LIMIT #{offset}, #{pageSize}")
    List<Topic> searchTopics(@Param("keyword") String keyword, @Param("sort") String sort, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT * FROM topics WHERE id = #{id}")
    Topic selectById(Long id);

    @Select("SELECT COUNT(*) FROM topics WHERE title LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%')")
    Long countSearchTopics(@Param("keyword") String keyword);

    @Select("<script>" +
            "SELECT * FROM topics WHERE deleted = 0 AND status = 'PUBLISHED'" +
            "<if test='tag != null and tag != \"\"'>" +
            " AND FIND_IN_SET(#{tag}, tag)" +
            "</if>" +
            "<if test='level != null and level != \"\"'>" +
            " AND level = #{level}" +
            "</if>" +
            "<choose>" +
            "  <when test='sort == \"hot\"'> ORDER BY interactive_count DESC</when>" +
            "  <otherwise> ORDER BY created_at DESC</otherwise>" +
            "</choose>" +
            " LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<Topic> findTopicsByCondition(@Param("tag") String tag,
                                      @Param("level") String level,
                                      @Param("sort") String sort,
                                      @Param("offset") int offset,
                                      @Param("pageSize") int pageSize);

    @Select("<script>" +
            "SELECT COUNT(*) FROM topics WHERE deleted = 0 AND status = 'PUBLISHED'" +
            "<if test='tag != null and tag != \"\"'>" +
            " AND FIND_IN_SET(#{tag}, tag)" +
            "</if>" +
            "<if test='level != null and level != \"\"'>" +
            " AND level = #{level}" +
            "</if>" +
            "</script>")
    Long countByCondition(@Param("tag") String tag,
                          @Param("level") String level);

    /**
     * 更新话题参与人数
     */
    @Update("UPDATE topics SET participant_count = participant_count + 1 WHERE id = #{id}")
    int incrementParticipantCount(@Param("id") Long id);

    /**
     * 更新话题互动次数
     */
    @Update("UPDATE topics SET interactive_count = interactive_count + 1 WHERE id = #{id}")
    int incrementInteractiveCount(@Param("id") Long id);

    /**
     * 更新最新回复时间
     */
    @Update("UPDATE topics SET latest_reply_time = #{latestReplyTime} WHERE id = #{id}")
    int updateLatestReplyTime(@Param("id") Long id, @Param("latestReplyTime") LocalDateTime latestReplyTime);
    /**
     * 首页推荐：根据多个标签推荐话题
     */
    @Select("<script>" +
            "SELECT * FROM topics " +
            "WHERE deleted = 0 AND status = 'PUBLISHED' " +
            "<if test='tags != null and tags.size() > 0'>" +
            " AND ( " +
            "   <foreach collection='tags' item='tag' separator=' OR '> " +
            "       FIND_IN_SET(#{tag}, tag) " +
            "   </foreach> " +
            " ) " +
            "</if> " +
            "ORDER BY interactive_count DESC, latest_reply_time DESC " +
            "LIMIT #{limit}" +
            "</script>")
    List<Topic> findRecommendedTopicsByTags(
            @Param("tags") List<String> tags,
            @Param("limit") Integer limit
    );
    @Select("<script>" +
            "SELECT * FROM topics " +
            "WHERE deleted = 0 AND status = 'PUBLISHED' " +
            "<if test='tags != null and tags.size() > 0'>" +
            " AND ( " +
            "   <foreach collection='tags' item='tag' separator=' OR '> " +
            "       FIND_IN_SET(#{tag}, tag) " +
            "   </foreach> " +
            " ) " +
            "</if>" +
            "ORDER BY interactive_count DESC, latest_reply_time DESC" +
            "</script>")
    List<Topic> findAllRecommendedTopicsByTags(
            @Param("tags") List<String> tags
    );

}

