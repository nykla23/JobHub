package com.cyd.xs.dto.user.Topic;

import lombok.Data;

@Data
public class TopicCommentLikeDTO {
    private String commentId;
    private Integer likeCount;
    private Boolean isLike;
}