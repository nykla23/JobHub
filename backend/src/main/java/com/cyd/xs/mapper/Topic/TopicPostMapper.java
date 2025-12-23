package com.cyd.xs.mapper.Topic;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyd.xs.entity.Topic.TopicPost;
import com.cyd.xs.entity.Topic.TopicPostLike;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TopicPostMapper extends BaseMapper<TopicPost> {

    @Select("""
        SELECT *
        FROM topic_posts
        WHERE topic_id = #{topicId}
        ORDER BY created_at DESC
        LIMIT #{pageSize} OFFSET #{offset}
    """)
    List<TopicPost> findPostsByTopicId(Long topicId, int offset, int pageSize);

    @Select("SELECT * FROM topic_posts WHERE id = #{id}")
    TopicPost selectById(Long id);

    // ---------- 点赞相关（独立表，不冲突） ----------

    @Insert("""
    INSERT INTO topic_post_like (post_id, user_id, created_at)
    VALUES (#{postId}, #{userId}, #{createdAt})
""")
    int insertLike(TopicPostLike like);

    @Delete("""
        DELETE FROM topic_post_like
        WHERE post_id = #{postId} AND user_id = #{userId}
    """)
    void deleteByPostAndUser(@Param("postId") Long postId,
                             @Param("userId") Long userId);

    @Select("""
    SELECT COUNT(*)
    FROM topic_post_like
    WHERE post_id = #{postId} AND user_id = #{userId}
""")
    int exists(@Param("postId") Long postId,
               @Param("userId") Long userId);


    @Select("""
        SELECT COUNT(*)
        FROM topic_post_like
        WHERE post_id = #{postId}
    """)
    int countByPostId(@Param("postId") Long postId);

    @Delete("""
        DELETE FROM topic_post_like
        WHERE post_id = #{postId}
    """)
    int deleteByPostId(@Param("postId") Long postId);
}
