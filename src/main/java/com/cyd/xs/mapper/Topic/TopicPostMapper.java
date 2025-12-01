package com.cyd.xs.mapper.Topic;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyd.xs.entity.User.Topic.TopicPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TopicPostMapper extends BaseMapper<TopicPost> {

    @Select("SELECT * FROM topic_posts WHERE topic_id = #{topicId} ORDER BY created_at DESC LIMIT #{pageSize} OFFSET #{offset}")
    List<TopicPost> findPostsByTopicId(String topicId, int offset, int pageSize);

    String generateId();
}
