package com.cyd.xs.entity.User.Topic;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "topic_posts")
public class TopicPost {
    @Id
    private String id;

    @Column(nullable = false)
    private String topicId;

    @Column(nullable = false)
    private String userId;

    private String userName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "JSON")
    private String images;

    @Column(columnDefinition = "JSON")
    private String tags;

    private Integer likeCount = 0;
    private Integer commentCount = 0;
    private Integer collectCount = 0;
    private LocalDateTime createdAt;

    public TopicPost() {
    }

    public TopicPost(String id, String topicId, String userId, String userName, String content, String images, String tags, Integer likeCount, Integer commentCount, Integer collectCount, LocalDateTime createdAt) {
        this.id = id;
        this.topicId = topicId;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.images = images;
        this.tags = tags;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.collectCount = collectCount;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(Integer collectCount) {
        this.collectCount = collectCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
