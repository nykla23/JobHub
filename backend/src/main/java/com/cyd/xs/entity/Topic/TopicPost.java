package com.cyd.xs.entity.Topic;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("topic_posts")
public class TopicPost {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("topic_id")
    private Long topicId;

    @TableField("user_id")
    private Long userId;

    @TableField("user_name")
    private String userName;

    private String content;

    private String images;
    private String tags;

    @TableField("like_count")
    private Integer likeCount = 0;

    @TableField("comment_count")
    private Integer commentCount = 0;

    @TableField("collect_count")
    private Integer collectCount = 0;

    @TableField("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    private String status = "PUBLISHED";
    private Integer deleted = 0;
}
