package com.cyd.xs.mapper.Topic;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyd.xs.entity.User.Topic.Topic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface TopicMapper extends BaseMapper<Topic> {

    @Select("SELECT * FROM topics WHERE title LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%') ORDER BY ${sort} DESC LIMIT #{offset}, #{pageSize}")
    List<Topic> searchTopics(@Param("keyword") String keyword, @Param("sort") String sort, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM topics WHERE title LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%')")
    Long countSearchTopics(@Param("keyword") String keyword);

    @Select("<script>" +
            "SELECT * FROM topics WHERE 1=1" +
            "<if test='tag != null'> AND tag = #{tag}</if>" +
            "<if test='sortType == \"hot\"'> ORDER BY interactive_count DESC</if>" +
            "<if test='sortType == \"recent\"'> ORDER BY latest_reply_time DESC</if>" +
            "<if test='sortType == \"comprehensive\" or sortType == null'> ORDER BY interactive_count DESC, latest_reply_time DESC</if>" +
            " LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<Topic> findTopicsByCondition(String tag, String sortType, int offset, int pageSize);

    @Select("SELECT COUNT(*) FROM topics WHERE tag = #{tag}")
    long countByTag(String tag);
}
