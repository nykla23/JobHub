package com.cyd.xs.dto.Topic;

import lombok.Data;
import java.util.List;

@Data
public class TopicCommentRequest {
    private String content;
    private List<String> imageUrls; // 图片URL列表
    private String parentId;        // 父评论ID
}