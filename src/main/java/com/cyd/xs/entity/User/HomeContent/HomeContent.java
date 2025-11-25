package com.cyd.xs.entity.User.HomeContent;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "home_contents")
public class HomeContent {
    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contentType; // expert/user/activity

    private String authorId;
    private String authorName;
    private Integer viewCount = 0;
    private Integer interactiveCount = 0;
    private String content;
    private LocalDateTime createdAt;

    public HomeContent() {
    }

    public HomeContent(String id, String title, String contentType, String authorId, String authorName, Integer viewCount, Integer interactiveCount, String content, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.contentType = contentType;
        this.authorId = authorId;
        this.authorName = authorName;
        this.viewCount = viewCount;
        this.interactiveCount = interactiveCount;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getInteractiveCount() {
        return interactiveCount;
    }

    public void setInteractiveCount(Integer interactiveCount) {
        this.interactiveCount = interactiveCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}