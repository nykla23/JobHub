package com.cyd.xs.dto.Topic;

import lombok.Data;

@Data
public class TopicPostDTO {
    private String postId;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
