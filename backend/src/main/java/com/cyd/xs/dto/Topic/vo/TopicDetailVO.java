package com.cyd.xs.dto.Topic.vo;

import lombok.Data;

@Data
public class TopicDetailVO {

    private TopicInfoVO topicInfo;

    private CommentPageVO comments;
}
